package com.example.movieticketWeb.service;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import com.example.movieticketWeb.dto.request.NewsOrDiscountRequest;
import com.example.movieticketWeb.dto.response.NewsOrDiscountResponse;

public interface INewsService {
    boolean addNews(NewsOrDiscountRequest newsRequest);

    boolean updateNews(Long id, @Valid NewsOrDiscountRequest newsRequest);

    boolean deleteNews(Long id);

    Page<NewsOrDiscountResponse> getAllNews(int page, int recordsPerPage);

    NewsOrDiscountResponse getNewsById(Long id);
}
