package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;

import java.util.Date;
import java.util.List;

public interface IMovieService {
    List<MovieResponse> getAllMovies();
    MovieResponse getMovieById(int movieID);
    boolean addMovie(MovieRequest movieRequest);
    void updateMovie(int movieID, MovieRequest movieRequest);
    void deleteMovie(int movieID);
    List<MovieResponse> searchMovie(String movieName);

    List<MovieResponse> getMovies(int offset, int soluong);

    int getNoOfRecords();
//    List<MovieResponse> getMovieByCategory(String category);
//    List<MovieResponse> getMovieByStatus(boolean status);
//    List<MovieResponse> getMovieByRating(float rating);
//    List<MovieResponse> getMovieByReleaseDay(Date releaseDay);
//    List<MovieResponse> getMovieByDuration(String movieDuration);
//    List<MovieResponse> getMovieByCategoryAndStatus(String category, boolean status);
//    List<MovieResponse> getMovieByCategoryAndRating(String category, float rating);
//    List<MovieResponse> getMovieByCategoryAndReleaseDay(String category, Date releaseDay);
//    List<MovieResponse> getMovieByCategoryAndDuration(String category, String movieDuration);
//    List<MovieResponse> getMovieByStatusAndRating(boolean status, float rating);
//    List<MovieResponse> getMovieByStatusAndReleaseDay(boolean status, Date releaseDay);
//    List<MovieResponse> getMovieByStatusAndDuration(boolean status, String movieDuration);
//    List<MovieResponse> getMovieByRatingAndReleaseDay(float rating, Date releaseDay);
//    List<MovieResponse> getMovieByRatingAndDuration(float rating, String movieDuration);
//    List<MovieResponse> getMovieByReleaseDayAndDuration(Date releaseDay, String movieDuration);
//    List<MovieResponse> getMovieByCategoryAndStatusAndRating(String category, boolean status, float rating);
//    List<MovieResponse> getMovieByCategoryAndStatusAndReleaseDay(String category, boolean status, Date releaseDay);
//    List<MovieResponse> getMovieByCategoryAndStatusAndDuration(String category, boolean status, String movieDuration);
//    List<MovieResponse> getMovieByCategoryAndRatingAndReleaseDay(String category, float rating, Date releaseDay);
//    List<MovieResponse> getMovieByCategoryAndRatingAndDuration(String category, float rating, String movieDuration);
//    List<MovieResponse> getMovieByCategoryAndReleaseDayAndDuration(String category, Date releaseDay, String movieDuration);
//    List<MovieResponse> getMovieByStatusAndRatingAndReleaseDay(boolean status, float rating, Date releaseDay);
//    List<MovieResponse> getMovieByStatusAndRatingAndDuration(boolean status, float rating, String movieDuration);
}
