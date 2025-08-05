package com.example.movieticketWeb.restController.user;

import com.example.movieticketWeb.dto.response.MovieResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IMovieService;
import com.example.movieticketWeb.service.IPersonService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserHomeRestController {
    private final IPersonService personService;
    private final IMovieService movieService;

    public UserHomeRestController(IPersonService personService, IMovieService movieService) {
        this.personService = personService;
        this.movieService = movieService;
    }

    @GetMapping("/userInfo")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_USER")) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.ACCESS_DENIED, "Unauthorized access"));
        }
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, personService.getInfo(person)));
    }

        @GetMapping("/movieList")
        public ResponseEntity<?> movieList(@AuthenticationPrincipal Person person) {
            if (person == null || !person.getRole().equalsIgnoreCase("ROLE_USER")) {
                return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.ACCESS_DENIED, "Unauthorized access"));
            }
            // Lấy danh sách phim đang chiếu
            List<MovieResponse> moviesShow = movieService.getMoviesShowing();

            // Lấy danh sách phim sắp chiếu
            List<MovieResponse> moviesComingSoon = movieService.getComingSoonMovies();

            // Đóng gói dữ liệu trả về
            Map<String, Object> response = new HashMap<>();
            response.put("moviesShowing", moviesShow);
            response.put("moviesComingSoon", moviesComingSoon);
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
        }
}
