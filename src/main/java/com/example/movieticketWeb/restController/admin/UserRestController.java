package com.example.movieticketWeb.restController.admin;

import static com.example.movieticketWeb.restController.web.SignupRestController.getResponseEntity;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IPersonService;

@RestController
@RequestMapping("/admin/users")
public class UserRestController {
    private final IPersonService userService;

    public UserRestController(IPersonService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> getListUser(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong) {
        Page<PersonInfoResponse> users = userService.getUsers(page, soluong);
        PagedResponse<PersonInfoResponse> response = new PagedResponse<>(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest, BindingResult bindingResult) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        boolean isAdded = userService.addUser(userRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id, @Valid @RequestBody UserRequest user, BindingResult bindingResult) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        boolean isUpdated = userService.updateUser(id, user);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int recordsPerPage) {

        Page<PersonInfoResponse> users = userService.searchUser(keyword, page, recordsPerPage);
        PagedResponse<PersonInfoResponse> response = new PagedResponse<>(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }
}
