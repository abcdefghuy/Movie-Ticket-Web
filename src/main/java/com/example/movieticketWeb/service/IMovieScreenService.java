package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.entity.MovieScreenings;
import javassist.NotFoundException;
import lombok.Data;

import java.util.Date;
import java.util.Map;

public interface IMovieScreenService {
    Map<String, Object> getMovieScreens(Long roomId, int page, int soluong);

    boolean addMovieScreen(MovieScreenRequest movieScreenRequest);

    boolean updateMovieScreen(Long id, MovieScreenRequest movieScreenRequest);

    boolean deleteMovieScreen(Long id);

    MovieScreenResponse getMovieScreenById(Long id) throws NotFoundException;

    Map<String, Object> searchMovieScreen(Long roomId, int page, int soluong, Date keyword);
}
