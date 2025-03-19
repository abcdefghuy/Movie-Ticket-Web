package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT COUNT(m) FROM Movie m")
    int countMovies();
}
