package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.PopCorn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPopCornRepository extends JpaRepository<PopCorn, Long> {
}
