package com.example.movieticketWeb.RestController.user;

import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.service.IMovieService;
import com.example.movieticketWeb.service.IPersonService;
import org.hibernate.engine.spi.Resolution;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if(person == null|| !person.getRole().equalsIgnoreCase("ROLE_USER")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(personService.getInfo(person));
    }


    @GetMapping("/movieList")
    public ResponseEntity<?> movieList(@AuthenticationPrincipal Person person) {
        if(person == null|| !person.getRole().equalsIgnoreCase("ROLE_USER")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
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
