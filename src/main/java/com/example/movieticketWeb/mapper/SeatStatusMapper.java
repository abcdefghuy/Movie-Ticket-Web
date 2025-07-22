package com.example.movieticketWeb.mapper;

import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.SeatStatus;

@Service
public class SeatStatusMapper {
    public SeatStatusResponse toDTO(SeatStatus seatStatus) {
        return SeatStatusResponse.builder()
                .status(seatStatus.isStatus())
                .seatId(seatStatus.getSeat().getSeatID())
                .seatNumber(seatStatus.getSeat().getSeatNumber())
                .isCouple(seatStatus.getSeat().isCouple())
                .build();
    }
}
