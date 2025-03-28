package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequest {
    private String roomName;
    private String screenType;
    private int chairNumber;
    private boolean status;
    private Long cinemaId;
}
