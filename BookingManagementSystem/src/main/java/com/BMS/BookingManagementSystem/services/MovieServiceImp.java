package com.BMS.BookingManagementSystem.services;

import com.BMS.BookingManagementSystem.models.Movie;
import com.BMS.BookingManagementSystem.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImp implements  MovieService {

    public MovieRepository movieRepository;

    public MovieServiceImp(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Optional<Movie> findMovieById(long id) {
        return movieRepository.findById(id);
    }
}
