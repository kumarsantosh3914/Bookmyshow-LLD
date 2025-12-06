package com.BMS.BookingManagementSystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Auditorium extends BaseModel {

    private String name;

    private int capacity;

    @ManyToOne
    private Theatre theatre;
}
