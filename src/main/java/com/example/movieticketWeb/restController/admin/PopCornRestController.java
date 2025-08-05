package com.example.movieticketWeb.restController.admin;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.PopCornRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IPopCornService;

@RestController
@RequestMapping("/admin/popcorn's")
public class PopCornRestController {
    private final IPopCornService popCornService;

    public PopCornRestController(IPopCornService popCornService) {
        this.popCornService = popCornService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllPopCorns(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong) {
        Page<PopcornResponse> popCorns = popCornService.getAllPopCorn(page, soluong);
        PagedResponse<PopcornResponse> response = new PagedResponse<>(
                popCorns.getContent(),
                popCorns.getNumber(),
                popCorns.getSize(),
                popCorns.getTotalElements(),
                popCorns.getTotalPages(),
                popCorns.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPopCornById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, popCornService.getPopCornById(id)));
    }

    @PostMapping()
    public ResponseEntity<?> createPopCorn(@RequestBody PopCornRequest popCornRequest) {
        boolean isAdded = popCornService.addPopCorn(popCornRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePopCorn(@PathVariable Long id, @RequestBody PopCornRequest popCornRequest) {
        boolean isUpdated = popCornService.updatePopCorn(popCornRequest, id);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePopCorn(@PathVariable Long id) {
        boolean isDeleted = popCornService.deletePopCorn(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
