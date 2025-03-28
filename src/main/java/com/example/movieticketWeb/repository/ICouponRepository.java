package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICouponRepository extends JpaRepository<Coupon, Long> {
}
