package com.BMS.BookingManagementSystem.repositories;

import com.BMS.BookingManagementSystem.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Ticket,Long> {

}
