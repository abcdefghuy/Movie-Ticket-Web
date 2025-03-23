package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRequest {
    private String cinemaName;
    private String address;
    private String location;
    private boolean status;
    private int roomCount;
}
