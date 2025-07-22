package com.example.movieticketWeb.service;

import java.util.List;
import java.util.Map;
import javassist.NotFoundException;

import com.example.movieticketWeb.dto.request.RoomRequest;
import com.example.movieticketWeb.dto.response.RoomResponse;

public interface IRoomService {
    List<RoomResponse> getRoomByCinemaId(Long cinemaId);

    RoomResponse getRoomById(Long id) throws NotFoundException;

    boolean addRoom(RoomRequest roomRequest);

    boolean updateRoom(RoomRequest room, Long roomId);

    boolean deleteRoomById(Long roomId, Long cinemaId);

    List<RoomResponse> searchRoomsByScreenType(String screenType, Long cinemaId);

    Map<String, Object> getRoomsWithCinemaName(Long cinemaId);
}
