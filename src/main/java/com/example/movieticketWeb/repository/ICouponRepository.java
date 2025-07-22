package com.example.movieticketWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.Coupon;

@Repository
public interface ICouponRepository extends JpaRepository<Coupon, Long> {}
