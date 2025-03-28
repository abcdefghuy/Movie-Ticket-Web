package com.example.movieticketWeb.service;

import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.entity.Seat;

import java.util.List;

public interface ISeatService {
    void addSeatsForRoom(Room savedRoom);

    List<Seat> getSeatsByRoomId(int roomId);
}
