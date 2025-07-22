package com.example.movieticketWeb.restController.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.dto.request.ForgotPasswordRequest;
import com.example.movieticketWeb.dto.request.ResetPasswordRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.exception.EmailServiceException;
import com.example.movieticketWeb.exception.UserNotFoundException;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;

@RestController
public class ForgetPassRestController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public ForgetPassRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/forget-pass", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {
        try {
            authenticationService.forgotPassword(forgotPasswordRequest);
            HttpSession session = request.getSession();
            session.setAttribute("verification", "verified");
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS_OTP_SENT));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.USER_NOT_FOUND));
        } catch (EmailServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authenticationService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS_PASSWORD_RESET));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
