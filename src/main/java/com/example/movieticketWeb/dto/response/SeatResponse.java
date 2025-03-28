package com.example.movieticketWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatResponse {
    private int seatId;
    private String seatNumber;
    private boolean isCouple;
    private boolean isBooked;
}
