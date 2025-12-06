package com.BMS.BookingManagementSystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Theatre extends BaseModel {

    private String name;
    private String address;

    @ManyToOne
    private City city;
}
