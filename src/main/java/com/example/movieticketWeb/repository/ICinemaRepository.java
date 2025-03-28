package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Cinema;
import com.example.movieticketWeb.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ICinemaRepository extends JpaRepository<Cinema, Long> {
    @Query("SELECT m FROM Cinema m WHERE LOWER(m.location) = LOWER(:keyword)")
    Page<Cinema> searchUserByKeyword(String keyword, PageRequest of);

}
