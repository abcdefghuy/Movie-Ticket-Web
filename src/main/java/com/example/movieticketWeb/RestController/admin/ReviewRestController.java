package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.response.ReviewResponse;
import com.example.movieticketWeb.service.IReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/reviews")
public class ReviewRestController {
    private final IReviewService reviewService;

    public ReviewRestController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllReviews(@RequestParam(name="page", defaultValue = "1") int page,
                                                            @RequestParam(name="soluong", defaultValue = "5") int recordPerPage,
                                                             @RequestParam(name="movieId", defaultValue = "") Long id) {
        Page<ReviewResponse> reviews = reviewService.getAllReviews(id,page,recordPerPage);
        Map<String, Object> response = Map.of("reviews", reviews.getContent(),
                "noOfPages", reviews.getTotalPages(),
                "currentPage", page,
                "recordsPerPage",recordPerPage,
                "noOfRecords", reviews.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") Long id) {
        boolean isDeleted = reviewService.deleteReview(id);
        if(isDeleted){
            return ResponseEntity.ok(Map.of("message","Delete review successful"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Delete review failed"));
    }

}
