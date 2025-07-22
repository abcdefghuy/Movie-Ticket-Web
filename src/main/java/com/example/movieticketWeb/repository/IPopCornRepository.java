package com.example.movieticketWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.PopCorn;

@Repository
public interface IPopCornRepository extends JpaRepository<PopCorn, Long> {}
