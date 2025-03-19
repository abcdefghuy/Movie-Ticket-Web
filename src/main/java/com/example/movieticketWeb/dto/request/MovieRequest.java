package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieRequest {
    private String movieName;
    private String category;
    private String description;
    private String image;
    private String movieDuration;
    private Date releaseDay;
    private float rating;
    private boolean status;
    private String movieTrailer;
}
