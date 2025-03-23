package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.CinemaRequest;
import com.example.movieticketWeb.dto.response.CinemaResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ICinemaService {
    boolean addCinema(CinemaRequest cinemaRequest);

    boolean updateCinema(Long id, CinemaRequest cinemaRequest);

    CinemaResponse getCinemaById(Long id);

    Page<CinemaResponse> getCinemas(int page, int soluong);

    boolean deleteCinema(Long id);

    Page<CinemaResponse> searchCinema(int page, int soluong, String searchValue);
}
