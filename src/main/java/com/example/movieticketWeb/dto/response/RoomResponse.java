package com.example.movieticketWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private int roomID;
    private String roomName;
    private String screenType;
    private int chairNumber;
    private boolean status;
    private int cinemaId;
    private String cinemaName;
}
