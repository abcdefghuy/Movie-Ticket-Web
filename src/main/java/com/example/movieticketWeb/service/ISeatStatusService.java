package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.MovieScreenings;
import javassist.NotFoundException;

import java.util.List;
import java.util.Map;

public interface ISeatStatusService {
    List<SeatStatusResponse> getSeatStatusesByScreeningAndRoom(int msID, int roomID);

    void addSeatStatusForScreening(MovieScreenings movieScreening);

    MovieScreenResponse getScreeningInfo(List<SeatStatusResponse> seatStatusResponses, long msID) throws NotFoundException;

    Map<String, List<SeatStatusResponse>> groupSeatsByRow(List<SeatStatusResponse> seatStatusResponses);
}
