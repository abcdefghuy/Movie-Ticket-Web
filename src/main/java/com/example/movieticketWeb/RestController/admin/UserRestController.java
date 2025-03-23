package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.service.IPersonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.movieticketWeb.RestController.web.SignupRestController.getResponseEntity;

@RestController
@RequestMapping("/admin/users")
public class UserRestController {
    private final IPersonService userService;
    public UserRestController(IPersonService userService) {
        this.userService = userService;
    }
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getListUser(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "5") int soluong) {
        Page<PersonInfoResponse> users = userService.getUsers(page, soluong);
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("noOfPages", users.getTotalPages());
        response.put("currentPage", page);
        response.put("recordsPerPage", soluong);
        response.put("noOfRecords", users.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest,BindingResult bindingResult) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        boolean isAdded= userService.addUser(userRequest);
        if(isAdded){
            return ResponseEntity.ok(Map.of("message", "Add user successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Add user failed"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest user, BindingResult bindingResult) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        boolean isUpdated= userService.updateUser(id,user);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update user successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update failed"));

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean isDeleted= userService.deleteUser(id);
        if(isDeleted){
            return ResponseEntity.ok("Delete user successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete user failed");
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int recordsPerPage) {

        Page<PersonInfoResponse> users = null;
        users = userService.searchUser(keyword, page, recordsPerPage);
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("noOfPages", users.getTotalElements());
        response.put("currentPage", page);
        response.put("recordsPerPage", recordsPerPage);
        response.put("keyword", keyword);
        response.put("noOfRecords", users.getTotalElements());

        return ResponseEntity.ok(response);
    }

}
