package com.example.movieticketWeb.restController.admin;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.dto.response.ReviewResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IReviewService;

@RestController
@RequestMapping("/admin/reviews")
public class ReviewRestController {
    private final IReviewService reviewService;

    public ReviewRestController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllReviews(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int recordPerPage,
            @RequestParam(name = "movieId", defaultValue = "") Long id) {
        Page<ReviewResponse> reviews = reviewService.getAllReviews(id, page, recordPerPage);
        PagedResponse<ReviewResponse> response = new PagedResponse<>(
                reviews.getContent(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") Long id) {
        boolean isDeleted = reviewService.deleteReview(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
