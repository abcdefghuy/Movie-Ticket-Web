package com.example.movieticketWeb.controller.web;

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
public class ForgetPassController {
    @GetMapping("/forget-pass")
    public ModelAndView forgetpass() {
        return new ModelAndView("web/forgetpassword");
    }
}
