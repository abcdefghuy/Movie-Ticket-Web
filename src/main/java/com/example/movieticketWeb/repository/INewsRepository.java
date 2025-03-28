package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.NewsOrDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INewsRepository extends JpaRepository<NewsOrDiscount, Long> {
}
