package com.example.movieticketWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatStatusResponse {
    private int seatId;
    private String seatNumber;
    private boolean isCouple;
    private boolean status;
}
