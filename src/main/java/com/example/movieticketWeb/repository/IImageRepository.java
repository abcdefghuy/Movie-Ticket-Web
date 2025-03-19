package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m.image FROM Movie m WHERE m.image = :fileName")
    String findUrlByFileName(@Param("fileName") String fileName);
}
