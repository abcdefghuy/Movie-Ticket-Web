package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.PopCornRequest;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPopCornService {
    Page<PopcornResponse> getAllPopCorn(int page, int recordPerPage);
    PopcornResponse getPopCornById(Long id);
    boolean addPopCorn(PopCornRequest popCorn);
    boolean updatePopCorn(PopCornRequest popCorn, Long id);
    boolean deletePopCorn(long id);
}
