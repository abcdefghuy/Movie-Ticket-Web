package com.example.movieticketWeb.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponse {
    private int movieID;
    private String movieName;
    private String category;
    private String description;
    private String image;
    private String movieDuration;
    private Date releaseDay;
    private float rating;
    private boolean status;
    private String movieTrailer;
    //  private List<MovieScreeningsResponse> movieScreenings;
    // private List<ReviewResponse> reviews;

}
