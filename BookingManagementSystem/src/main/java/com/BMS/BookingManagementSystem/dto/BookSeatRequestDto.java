package com.BMS.BookingManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookSeatRequestDto {
    private long showId;
    private long userId;
    private List<Long> seatIds;
}
