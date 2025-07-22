package com.example.movieticketWeb.mapper;

import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.request.CinemaRequest;
import com.example.movieticketWeb.dto.response.CinemaResponse;
import com.example.movieticketWeb.entity.Cinema;

@Service
public class CinemaMapper {
    public CinemaResponse toDTO(Cinema cinema) {
        return CinemaResponse.builder()
                .cinemaId(cinema.getCinemaId())
                .cinemaName(cinema.getCinemaName())
                .address(cinema.getAddress())
                .location(cinema.getLocation())
                .status(cinema.isStatus())
                .roomCount(cinema.getRoomCount())
                .build();
    }

    public Cinema toEntity(CinemaRequest cinemaRequest) {
        return Cinema.builder()
                .cinemaName(cinemaRequest.getCinemaName())
                .address(cinemaRequest.getAddress())
                .location(cinemaRequest.getLocation())
                .status(cinemaRequest.isStatus())
                .roomCount(cinemaRequest.getRoomCount())
                .build();
    }
}
