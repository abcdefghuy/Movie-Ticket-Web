package com.example.movieticketWeb.controller.web;

import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
