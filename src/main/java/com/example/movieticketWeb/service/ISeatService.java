package com.example.movieticketWeb.service;

import java.util.List;

import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.entity.Seat;

public interface ISeatService {
    void addSeatsForRoom(Room savedRoom);

    List<Seat> getSeatsByRoomId(int roomId);
}
