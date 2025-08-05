package com.example.movieticketWeb.restController.admin;

import java.util.Date;
import javassist.NotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IMovieScreenService;

@RestController
@RequestMapping("/admin/movie-screens")
public class MovieScreenRestController {
    private final IMovieScreenService movieScreenService;

    public MovieScreenRestController(IMovieScreenService movieScreenService) {
        this.movieScreenService = movieScreenService;
    }

    @GetMapping()
    public ResponseEntity<?> getMovieScreen(
            @RequestParam(name = "roomId") Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int soluong) {
        Page<MovieScreenResponse> movieScreens = movieScreenService.getMovieScreens(roomId, page, soluong);
        PagedResponse<MovieScreenResponse> response = new PagedResponse<>(
                movieScreens.getContent(),
                movieScreens.getNumber(),
                movieScreens.getSize(),
                movieScreens.getTotalElements(),
                movieScreens.getTotalPages(),
                movieScreens.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @PostMapping()
    public ResponseEntity<?> addMovieScreen(@RequestBody MovieScreenRequest movieScreenRequest) {
        boolean isAdded = movieScreenService.addMovieScreen(movieScreenRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovieScreen(
            @PathVariable Long id, @RequestBody MovieScreenRequest movieScreenRequest) {
        boolean isUpdated = movieScreenService.updateMovieScreen(id, movieScreenRequest);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovieScreen(@PathVariable Long id) {
        boolean isDeleted = movieScreenService.deleteMovieScreen(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieScreenById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, movieScreenService.getMovieScreenById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovieScreen(
            @RequestParam(name = "roomId") Long roomId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int soluong,
            @RequestParam(name = "keyword", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date keyword) {
        Page<MovieScreenResponse> movieScreens;
        if (keyword == null) {
            movieScreens = movieScreenService.getMovieScreens(roomId, page, soluong);
        } else {
            movieScreens = movieScreenService.searchMovieScreen(roomId, page, soluong, keyword);
        }
        PagedResponse<MovieScreenResponse> response = new PagedResponse<>(
                movieScreens.getContent(),
                movieScreens.getNumber(),
                movieScreens.getSize(),
                movieScreens.getTotalElements(),
                movieScreens.getTotalPages(),
                movieScreens.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }
}
