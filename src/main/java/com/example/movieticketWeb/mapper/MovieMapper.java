package com.example.movieticketWeb.mapper;

import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Movie;

@Service
public class MovieMapper {
    public MovieResponse toDTO(Movie movie) {
        return MovieResponse.builder()
                .movieID(movie.getMovieID())
                .movieName(movie.getMovieName())
                .category(movie.getCategory())
                .description(movie.getDescription())
                .image(movie.getImage())
                .movieDuration(movie.getMovieDuration())
                .releaseDay(movie.getReleaseDay())
                .rating(movie.getRating())
                .status(movie.isStatus())
                .movieTrailer(movie.getMovieTrailer())
                // .movieScreenings(movie.getMovieScreenings())
                // .reviews(movie.getReviews())
                .build();
    }

    public Movie toEntity(MovieRequest movieRequest) {
        return Movie.builder()
                .movieName(movieRequest.getMovieName())
                .category(movieRequest.getCategory())
                .description(movieRequest.getDescription())
                .image(movieRequest.getImage())
                .movieDuration(movieRequest.getMovieDuration())
                .releaseDay(movieRequest.getReleaseDay())
                .rating(movieRequest.getRating())
                .status(movieRequest.isStatus())
                .movieTrailer(movieRequest.getMovieTrailer())
                .build();
    }
}
