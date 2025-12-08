package com.BMS.BookingManagementSystem.services;

import com.BMS.BookingManagementSystem.models.*;
import com.BMS.BookingManagementSystem.repositories.ShowRepository;
import com.BMS.BookingManagementSystem.repositories.ShowSeatRepository;
import com.BMS.BookingManagementSystem.repositories.TicketRepository;
import com.BMS.BookingManagementSystem.repositories.UserRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.util.List;
import java.util.Optional;

@Service
public class RedisBookingService implements BookingService {

    private final CacheService cacheService;
    private final ShowSeatRepository showSeatRepository;
    private final TicketRepository ticketRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public RedisBookingService(
        RedisService cacheService,
        ShowSeatRepository showSeatRepository,
        TicketRepository ticketRepository,
        ShowRepository showRepository,
        UserRepository userRepository
    ) {
        this.cacheService = cacheService;
        this.showSeatRepository = showSeatRepository;
        this.ticketRepository = ticketRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean blockSeats(long showId, List<Long> seatIds, long userId) {
        System.out.println("Blocking seats in RedisBookingService");
        // Step 1: We will first of all check if the seats are available or not
        // A: Check if the seats are not blocked already
        List<ShowSeat> showSeats = showSeatRepository.findAllByShowIdAndSeatIdIn(showId, seatIds);

        System.out.println("Printing showSeats");
        showSeats.forEach(showSeat -> {
            System.out.println("ShowSeat ID: " + showSeat.getId() + " Status: " + showSeat.getStatus());
        });

        for (ShowSeat seat : showSeats) {
            if(seat.getStatus().equals(ShowSeatStatus.BOOKED)) {
                return false;
            }
        }

        // B: Check if the seats are not locked already
        for(ShowSeat seat: showSeats) {
            String status = (String) cacheService.get("seatId-"+seat.getId()+"-userId-"+userId);
            // If status is not null, then the user who has locked the seat is the same user then continue
            if(status != null) {
                return false;
            }
        }

        // Step 2: If all the seats are available, then we will block the seats in redis - seatId - userId
        for(ShowSeat seat: showSeats) {
            cacheService.set("seatId-"+seat.getId()+"-userId-"+userId, "LOCKED");
        }
        System.out.println("Printing cache after blocking seats");
        cacheService.getAllKeysAndValues();
        return true;
    }

    @Override
    @Transactional
    public Optional<Ticket> bookTicket(long showId, List<Long> seatIds, long userId) {
        // Step 1: In redis check if the user has lock for all the seats that they are trying to book
        for(Long seatId: seatIds) {
            String status = (String) cacheService.get("seatId-"+seatId+"-userId-"+userId);
            System.out.println("Booking Ticket - Checking lock for seatId: " + seatId + " userId: " + userId + " status: " + status);

            if(status == null) {
                return Optional.empty();
            }
        }

        System.out.println("All seats are locked by user: " + userId + " proceeding to book the ticket");
        User user = userRepository.findById(userId).get().getUser();
        Show show = showRepository.findById(showId).get();

        // Create a new ticket
        // Go to all the rows of show_seats and update the status to booked and update ticket id in one query
        Ticket ticket = createTicketAndBookSeat(show, user, seatIds);

        System.out.println("Ticket booked successfully with id: " + ticket.getId());
        return Optional.of(ticket);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Ticket createTicketAndBookSeat(Show show, User user, List<Long> seatIds) {

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setShow(show);
        ticket.setStatus(TicketStatus.BOOKED);

        ticket = ticketRepository.save(ticket);

        showSeatRepository.bookShowSeatsBulk(seatIds, ticket);

        return ticket;

    }

    @Override
    public void clearAllSeatLocks() {
        cacheService.deleteAll();
    }
}
