package com.example.movieticketWeb.controller;

import com.example.movieticketWeb.dto.request.RegisterUserRequest;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.enumeration.City;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SignupController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public SignupController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }
    @GetMapping("/signup")
    public ModelAndView showSignupPage() {
        return new ModelAndView("web/signup");
    }
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest registerUserRequest, BindingResult bindingResult
    , HttpServletRequest request ) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        try {
            // Đăng ký người dùng và trả về đối tượng người dùng đã đăng ký
            Person registeredUser = authenticationService.signup(registerUserRequest);
            HttpSession session = request.getSession();
            session.setAttribute("verification",registeredUser);
            // Trả về đối tượng người dùng đã đăng ký với mã trạng thái OK
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            // Nếu có lỗi, trả về mã lỗi 400 với thông báo lỗi
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during registration: " + e.getMessage());
        }
    }

    static ResponseEntity<?> getResponseEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Trả về lỗi dưới dạng JSON
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }

}
