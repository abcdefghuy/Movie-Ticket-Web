package com.example.movieticketWeb.service;

import java.util.List;
import java.util.Map;

import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.MovieScreenings;

public interface ISeatStatusService {
    List<SeatStatusResponse> getSeatStatusesByScreeningAndRoom(int msID, int roomID);

    void addSeatStatusForScreening(MovieScreenings movieScreening);

    Map<String, List<SeatStatusResponse>> groupSeatsByRow(List<SeatStatusResponse> seatStatusResponses);
}
