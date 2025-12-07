package com.BMS.BookingManagementSystem.repositories;

import com.BMS.BookingManagementSystem.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}
