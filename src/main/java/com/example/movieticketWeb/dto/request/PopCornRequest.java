package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopCornRequest {
    private String namePopCorn;
    private String typePopCorn;
    private double price;
    private boolean status;
}
