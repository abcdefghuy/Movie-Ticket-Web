package com.example.movieticketWeb.service;

import org.springframework.data.domain.Page;

import com.example.movieticketWeb.dto.request.CouponRequest;
import com.example.movieticketWeb.dto.response.CouponResponse;

public interface ICouponService {
    Page<CouponResponse> getCoupons(int page, int soluong);

    CouponResponse getCouponById(Long id);

    boolean addCoupon(CouponRequest couponRequest);

    boolean updateCoupon(Long id, CouponRequest couponRequest);

    boolean deleteCoupon(Long id);
}
