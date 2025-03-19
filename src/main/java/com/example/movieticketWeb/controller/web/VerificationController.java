package com.example.movieticketWeb.controller.web;

import com.example.movieticketWeb.dto.request.VerifyUserRequest;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class VerificationController {

    @GetMapping("/verify")
    public ModelAndView showVerifyPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("verification") == null) {
            return new ModelAndView("redirect:/login"); // Chuyển hướng đến trang đăng ký
        }
        return new ModelAndView("web/verification");
    }
}
