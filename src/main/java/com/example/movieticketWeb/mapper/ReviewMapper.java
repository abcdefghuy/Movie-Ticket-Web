package com.example.movieticketWeb.mapper;

import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.request.ReviewRequest;
import com.example.movieticketWeb.dto.response.ReviewResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.entity.Review;

@Service
public class ReviewMapper {
    public ReviewResponse toDTO(Review review) {
        return ReviewResponse.builder()
                .reviewID(review.getReviewID())
                .content(review.getContent())
                .evaluate(review.getEvaluate())
                .MovieName(review.getMovie().getMovieName())
                .ReviewerName(review.getPerson().getUsername())
                .build();
    }

    public Review toEntity(ReviewRequest reviewRequest, Movie movie, Person person) {
        return Review.builder()
                .content(reviewRequest.getContent())
                .evaluate(reviewRequest.getEvaluate())
                .movie(movie)
                .person(person)
                .build();
    }
}
