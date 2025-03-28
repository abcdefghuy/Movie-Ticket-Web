package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.PopCornRequest;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import com.example.movieticketWeb.service.IPopCornService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/popcorn's")
public class PopCornRestController {
    private final IPopCornService popCornService;
    public PopCornRestController(IPopCornService popCornService) {
        this.popCornService = popCornService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllPopCorns(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "5") int soluong) {
        Page<PopcornResponse> popCorns = popCornService.getAllPopCorn(page, soluong);
        Map<String, Object> response = Map.of("popCorns", popCorns.getContent(),
                                              "noOfPages", popCorns.getTotalPages(),
                                              "currentPage", page,
                                              "recordsPerPage", soluong,
                                              "noOfRecords", popCorns.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PopcornResponse> getPopCornById(@PathVariable Long id) {
        return ResponseEntity.ok(popCornService.getPopCornById(id));
    }
    @PostMapping()
    public ResponseEntity<?> createPopCorn(@RequestBody PopCornRequest popCornRequest) {
        boolean isAdded = popCornService.addPopCorn(popCornRequest);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message", "Add popCorn successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Add popCorn failed"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePopCorn(@PathVariable Long id, @RequestBody PopCornRequest popCornRequest) {
        boolean isUpdated = popCornService.updatePopCorn(popCornRequest, id);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update popCorn successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Update popCorn failed"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePopCorn(@PathVariable Long id) {
        boolean isDeleted = popCornService.deletePopCorn(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Delete popCorn successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Delete popCorn failed"));
    }
}
