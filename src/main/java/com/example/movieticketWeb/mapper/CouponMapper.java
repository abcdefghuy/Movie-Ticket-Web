package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.CouponRequest;
import com.example.movieticketWeb.dto.response.CouponResponse;
import com.example.movieticketWeb.entity.Coupon;
import org.springframework.stereotype.Service;

@Service
public class CouponMapper {
    public Coupon toEntity(CouponRequest dto) {
        return Coupon.builder()
                .couponName(dto.getCouponName())
                .couponType(dto.getCouponType())
                .couponValue(dto.getCouponValue())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }
    public CouponResponse toDTO(Coupon entity) {
        return CouponResponse.builder()
                .couponID(entity.getCouponID())
                .couponName(entity.getCouponName())
                .couponType(entity.getCouponType())
                .couponValue(entity.getCouponValue())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }
}
