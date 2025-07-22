package com.example.movieticketWeb.restController.admin;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.CinemaRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.CinemaResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.ICinemaService;

@RestController
@RequestMapping("/admin/cinemas")
public class CinemaRestController {
    private final ICinemaService cinemaService;

    public CinemaRestController(ICinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping()
    public ResponseEntity<?> createCinema(@RequestBody CinemaRequest cinemaRequest) {
        boolean isAdded = cinemaService.addCinema(cinemaRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCinema(@PathVariable Long id, @RequestBody CinemaRequest cinemaRequest) {
        boolean isUpdated = cinemaService.updateCinema(id, cinemaRequest);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponse> getCinemaByID(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }

    @GetMapping()
    public ResponseEntity<?> getListCinema(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong) {
        Page<CinemaResponse> cinemas = cinemaService.getCinemas(page, soluong);
        PagedResponse<CinemaResponse> response = new PagedResponse<>(
                cinemas.getContent(),
                cinemas.getNumber(),
                cinemas.getSize(),
                cinemas.getTotalElements(),
                cinemas.getTotalPages(),
                cinemas.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        boolean isDeleted = cinemaService.deleteCinema(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCinema(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int recordsPerPage) {
        Page<CinemaResponse> cinemas;
        if (Objects.equals(keyword, "All") || Objects.equals(keyword, "")) {
            cinemas = cinemaService.getCinemas(page, recordsPerPage);
        } else {
            cinemas = cinemaService.searchCinema(page, recordsPerPage, keyword);
        }
        PagedResponse<CinemaResponse> response = new PagedResponse<>(
                cinemas.getContent(),
                cinemas.getNumber(),
                cinemas.getSize(),
                cinemas.getTotalElements(),
                cinemas.getTotalPages(),
                cinemas.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }
}
