package com.example.movieticketWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopcornResponse {
    private int popcornId;
    private String namePopcorn;
    private String typePopcorn;
    private double price;
    private boolean status;
}
