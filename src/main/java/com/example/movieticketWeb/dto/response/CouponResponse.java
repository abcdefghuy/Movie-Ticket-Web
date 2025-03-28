package com.example.movieticketWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private int couponID;
    private String couponName;
    private String couponType;
    private double couponValue;
    private Date startDate;
    private Date endDate;
}
