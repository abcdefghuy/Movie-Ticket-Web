package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.movie.movieID = :movieId")
    List<Review> findByMovieId(Long movieId);
}
