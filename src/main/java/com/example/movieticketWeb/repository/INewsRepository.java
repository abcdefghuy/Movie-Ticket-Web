package com.example.movieticketWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.NewsOrDiscount;

@Repository
public interface INewsRepository extends JpaRepository<NewsOrDiscount, Long> {}
