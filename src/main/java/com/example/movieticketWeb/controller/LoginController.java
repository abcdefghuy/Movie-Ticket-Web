package com.example.movieticketWeb.controller;

import com.example.movieticketWeb.dto.request.LoginUserRequest;
import com.example.movieticketWeb.dto.response.LoginResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.exception.UserNotFoundException;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.movieticketWeb.controller.SignupController.getResponseEntity;

@RestController
public class LoginController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();


    public LoginController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }
    @GetMapping("/login")
    public ModelAndView signin(Model model) {
        return new ModelAndView("web/signin");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response, BindingResult bindingResult) {
        ResponseEntity<?> errors = getResponseEntity(bindingResult);
        if (errors != null) return errors;
        String email = loginUserRequest.getEmail();
        int attempts = failedAttempts.getOrDefault(email, 0);

        if (attempts >= 5) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Collections.singletonMap("message", "Too many failed attempts. Try again later."));
        }

        try {
            Person authenticatedUser = authenticationService.authenticate(loginUserRequest);
            failedAttempts.remove(email);

            // Tạo JWT và lưu vào HTTP-only Secure Cookie
            String jwtToken = jwtService.generateToken(authenticatedUser);
            Cookie jwtCookie = new Cookie("jwtToken", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(Collections.singletonMap("message", "Login successful"));
        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "User does not exist"));
        }
        catch (RuntimeException e) {
            failedAttempts.put(email, attempts + 1);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid email or password"));
        }
    }
}
