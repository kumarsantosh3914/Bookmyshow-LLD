package com.BMS.BookingManagementSystem.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Movie extends BaseModel {

    private String name;
    private String poster;
}
