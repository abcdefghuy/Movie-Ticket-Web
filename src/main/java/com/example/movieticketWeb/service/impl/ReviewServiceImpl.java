package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.response.ReviewResponse;
import com.example.movieticketWeb.mapper.ReviewMapper;
import com.example.movieticketWeb.repository.IReviewRepository;
import com.example.movieticketWeb.service.IMovieService;
import com.example.movieticketWeb.service.IPersonService;
import com.example.movieticketWeb.service.IReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ReviewServiceImpl implements IReviewService {
    private final IReviewRepository reviewRepository;
    private final IMovieService movieService;
    private final IPersonService personService;
    private static final String REVIEW_CACHE_KEY = "allReviews";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ReviewMapper reviewMapper;
    public ReviewServiceImpl(IReviewRepository reviewRepository, IMovieService movieService, IPersonService personService, RedisTemplate<String, Object> redisTemplate, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.movieService = movieService;
        this.personService = personService;
        this.redisTemplate = redisTemplate;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public Page<ReviewResponse> getAllReviews(Long id,int page, int recordPerPage) {
        List<ReviewResponse> reviews = (List<ReviewResponse>) redisTemplate.opsForValue().get(REVIEW_CACHE_KEY);
        if(reviews != null){
            return paginationReview(reviews, page, recordPerPage);
        }
        else{
            List<ReviewResponse> reviewResponseList = reviewRepository.findByMovieId(id).stream()
                    .map(reviewMapper::toDTO)
                    .toList();
            redisTemplate.opsForValue().set(REVIEW_CACHE_KEY, reviewResponseList, Duration.ofHours(1));
            return paginationReview(reviewResponseList, page, recordPerPage);
        }
    }

    private Page<ReviewResponse> paginationReview(List<ReviewResponse> reviews, int page, int recordPerPage) {
        int start = (page - 1) * recordPerPage;
        int end = Math.min(start + recordPerPage, reviews.size());
        return new PageImpl<>(reviews.subList(start, end), PageRequest.of(page - 1, recordPerPage), reviews.size());
    }

    @Override
    public boolean deleteReview(Long id) {
        try {
            reviewRepository.deleteById(id);
            redisTemplate.delete(REVIEW_CACHE_KEY);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
