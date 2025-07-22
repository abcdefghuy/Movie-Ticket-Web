package com.example.movieticketWeb.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
