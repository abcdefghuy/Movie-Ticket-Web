package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.SeatStatus;
import org.springframework.stereotype.Service;

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
