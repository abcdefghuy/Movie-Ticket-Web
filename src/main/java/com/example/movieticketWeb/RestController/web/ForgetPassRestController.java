package com.example.movieticketWeb.RestController.web;

import com.example.movieticketWeb.dto.request.ForgotPasswordRequest;
import com.example.movieticketWeb.dto.request.ResetPasswordRequest;
import com.example.movieticketWeb.exception.EmailServiceException;
import com.example.movieticketWeb.exception.UserNotFoundException;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class ForgetPassRestController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public ForgetPassRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/forget-pass", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest , HttpServletRequest request) {
        try {
            authenticationService.forgotPassword(forgotPasswordRequest);
            HttpSession session = request.getSession();
            session.setAttribute("verification","verified");
            return ResponseEntity.ok(Map.of("message", "Verification code sent to your email."));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Email not found!"));
        } catch (EmailServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to send email!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong!"));
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authenticationService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Email not found!"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong!"));
        }
    }
}
