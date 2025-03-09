package com.example.movieticketWeb.controller;

import com.example.movieticketWeb.dto.request.VerifyUserRequest;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
public class VerificationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public VerificationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/verify")
    public ModelAndView showVerifyPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("verification") == null) {
            return new ModelAndView("redirect:/login"); // Chuyển hướng đến trang đăng ký
        }
        return new ModelAndView("web/verification");
    }

    @PostMapping(value = "/verify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserRequest verifyUserRequest, BindingResult bindingResult) {
        ResponseEntity<?> errors = SignupController.getResponseEntity(bindingResult);
        if (errors != null) return errors;

        try {
            authenticationService.verifyUser(verifyUserRequest);
            return ResponseEntity.ok().body(Map.of("message", "Account verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Verification code sent"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }


}
