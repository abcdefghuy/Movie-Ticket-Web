package com.example.movieticketWeb.service;

import java.util.Date;
import java.util.List;
import javassist.NotFoundException;

import org.springframework.data.domain.Page;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.SeatStatusResponse;

public interface IMovieScreenService {
    Page<MovieScreenResponse> getMovieScreens(Long roomId, int page, int soluong);

    boolean addMovieScreen(MovieScreenRequest movieScreenRequest);

    boolean updateMovieScreen(Long id, MovieScreenRequest movieScreenRequest);

    boolean deleteMovieScreen(Long id);

    MovieScreenResponse getMovieScreenById(Long id) throws NotFoundException;

    Page<MovieScreenResponse> searchMovieScreen(Long roomId, int page, int soluong, Date keyword);

    MovieScreenResponse getScreeningInfo(List<SeatStatusResponse> seatStatusResponses, long msID);
}
