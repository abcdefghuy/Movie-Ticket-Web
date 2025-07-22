package com.example.movieticketWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.Review;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.movie.movieID = :movieId")
    List<Review> findByMovieId(Long movieId);
}
