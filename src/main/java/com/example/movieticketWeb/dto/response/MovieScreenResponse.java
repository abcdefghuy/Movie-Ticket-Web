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
public class MovieScreenResponse {
    private int movieScreenId;
    private String movieName;
    private String roomName;
    private int movieId;
    private int roomId;
    private Date startTime;
    private Date endTime;
    private Date screeningDate;
    private boolean status;
    private long availableSeats;
    private String cinemaName;
}
