package com.example.movieticketWeb.restController.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.service.IMovieService;

@RestController
public class MovieListRestController {
    private final IMovieService movieService;

    public MovieListRestController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/showing")
    public ResponseEntity<List<MovieResponse>> getShowingMovies() {
        List<MovieResponse> movies = movieService.getMoviesShowing();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponse>> getComingSoonMovies() {
        List<MovieResponse> movies = movieService.getComingSoonMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }
}
