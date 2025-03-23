package com.example.movieticketWeb.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieRequest {
    @NotBlank(message = "Movie name cannot be empty")
    @Size(min = 2, max = 100, message = "Movie name must be between 2 and 100 characters")
    private String movieName;

    @NotBlank(message = "Category cannot be empty")
    private String category;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 10, message = "Description cannot exceed 10 characters")
    private String description;

    private String image;

    @NotBlank(message = "Movie duration cannot be empty")
    private String movieDuration;

    @NotNull(message = "Release day cannot be empty")
    @PastOrPresent(message = "Release day must be in the past or today")
    private Date releaseDay;

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 10, message = "Rating cannot be greater than 10")
    private float rating;

    private boolean status;

    @NotBlank(message = "Movie trailer URL cannot be empty")
    private String movieTrailer;
}
