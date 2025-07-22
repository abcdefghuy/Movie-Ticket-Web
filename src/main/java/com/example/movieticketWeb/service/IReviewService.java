package com.example.movieticketWeb.service;

import org.springframework.data.domain.Page;

import com.example.movieticketWeb.dto.response.ReviewResponse;

public interface IReviewService {
    Page<ReviewResponse> getAllReviews(Long id, int page, int recordPerPage);

    boolean deleteReview(Long id);
}
