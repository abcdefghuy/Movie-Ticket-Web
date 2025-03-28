package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface IReviewService {
    Page<ReviewResponse> getAllReviews(Long id, int page, int recordPerPage);

    boolean deleteReview(Long id);
}
