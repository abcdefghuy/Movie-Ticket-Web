package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private String content;
    private float evaluate;
    private Long movieID;
    private Long perID;
}
