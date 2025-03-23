package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.CinemaRequest;
import com.example.movieticketWeb.dto.response.CinemaResponse;
import com.example.movieticketWeb.service.ICinemaService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/admin/cinemas")
public class CinemaRestController {
    private final ICinemaService cinemaService;

    public CinemaRestController(ICinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping()
    public ResponseEntity<?> createCinema(@RequestBody CinemaRequest cinemaRequest) {
        boolean isAdded= cinemaService.addCinema(cinemaRequest);
        if(isAdded){
            return ResponseEntity.ok(Map.of("message", "Add cinema successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Add cinema failed"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCinema(@PathVariable Long id, @RequestBody CinemaRequest cinemaRequest) {
        boolean isUpdated= cinemaService.updateCinema(id,cinemaRequest);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update cinema successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update failed"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponse> getCinemaByID(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getListCinema(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "5") int soluong) {
        Page<CinemaResponse> cinemas = cinemaService.getCinemas(page, soluong);
        Map<String, Object> response = new HashMap<>();
        response.put("cinemas", cinemas.getContent());
        response.put("noOfPages", cinemas.getTotalPages());
        response.put("currentPage", page);
        response.put("recordsPerPage", soluong);
        response.put("noOfRecords", cinemas.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        boolean isDeleted = cinemaService.deleteCinema(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Delete cinema successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Delete failed"));
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCinema(   @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                                               @RequestParam(name = "soluong", defaultValue = "5") int recordsPerPage) {
        Page<CinemaResponse> cinemas;
        if(Objects.equals(keyword, "All") || Objects.equals(keyword, ""))
        {
            cinemas = cinemaService.getCinemas(page, recordsPerPage);
        }
        else {
            cinemas = cinemaService.searchCinema(page, recordsPerPage, keyword);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("cinemas", cinemas.getContent());
        response.put("noOfPages", cinemas.getTotalPages());
        response.put("currentPage", page);
        response.put("recordsPerPage", recordsPerPage);
        response.put("keyword", keyword);
        response.put("noOfRecords", cinemas.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
