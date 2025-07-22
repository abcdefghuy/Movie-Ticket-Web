package com.example.movieticketWeb.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
