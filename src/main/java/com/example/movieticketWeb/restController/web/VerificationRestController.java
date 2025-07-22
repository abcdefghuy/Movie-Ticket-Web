package com.example.movieticketWeb.restController.web;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.aop.ApiException;
import com.example.movieticketWeb.dto.request.VerifyUserRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;

@RestController
public class VerificationRestController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public VerificationRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/verify", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> verifyUser(
            @Valid @RequestBody VerifyUserRequest verifyUserRequest, BindingResult bindingResult) {
        ResponseEntity<?> errors = SignupRestController.getResponseEntity(bindingResult);
        if (errors != null) return errors;

        try {
            authenticationService.verifyUser(verifyUserRequest);
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS));
        } catch (ApiException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS_OTP_SENT));
        } catch (ApiException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode()));
        }
    }
}
