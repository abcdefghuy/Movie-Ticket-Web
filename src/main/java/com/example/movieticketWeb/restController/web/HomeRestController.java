package com.example.movieticketWeb.restController.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.enumeration.City;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IMovieService;

@RestController
public class HomeRestController {
    private final IMovieService movieService;

    public HomeRestController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getRegions() {
        List<String> regions = City.getListCity();
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, regions));
    }

    @GetMapping("/movie-list")
    public ResponseEntity<?> movieList() {
        // Lấy danh sách phim đang chiếu
        List<MovieResponse> moviesShowing = movieService.getMoviesShowing();
        // Lấy danh sách phim sắp chiếu
        List<MovieResponse> moviesComingSoon = movieService.getComingSoonMovies();
        // Đóng gói dữ liệu trả về
        Map<String, Object> response = new HashMap<>();
        response.put("moviesShowing", moviesShowing);
        response.put("moviesComingSoon", moviesComingSoon);
        return ResponseEntity.ok(response);
    }
}
