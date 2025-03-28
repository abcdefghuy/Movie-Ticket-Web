package com.example.movieticketWeb.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CinemaResponse {
    private int cinemaId;
    private String cinemaName;
    private String address;
    private String location;
    private boolean status;
    private int roomCount;
}
