package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.RoomRequest;
import com.example.movieticketWeb.dto.response.RoomResponse;
import com.example.movieticketWeb.entity.Cinema;
import com.example.movieticketWeb.entity.Room;
import org.springframework.stereotype.Service;

@Service
public class RoomMapper {
    public RoomResponse toDTO(Room room) {
        return RoomResponse.builder()
                .roomID(room.getRoomId())
                .chairNumber(room.getChairNumber())
                .roomName(room.getRoomName())
                .screenType(room.getScreenType())
                .cinemaName(room.getCinema().getCinemaName())
                .status(room.isStatus())
                .cinemaId(room.getCinema().getCinemaId())
                .build();
    }
    public Room toEntity(RoomRequest roomRequest, Cinema cinema) {
        return Room.builder()
                .roomName(roomRequest.getRoomName())
                .chairNumber(roomRequest.getChairNumber())
                .screenType(roomRequest.getScreenType())
                .status(roomRequest.isStatus())
                .cinema(cinema)
                .build();
    }
}
