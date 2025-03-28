package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    private String couponName;
    private String couponType;
    private double couponValue;
    private Date startDate;
    private Date endDate;
}
