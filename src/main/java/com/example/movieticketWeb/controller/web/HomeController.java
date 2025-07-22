package com.example.movieticketWeb.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.movieticketWeb.entity.Person;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal Person person) {
        return "web/home";
    }

    @GetMapping("/logout")
    public String logout() {
        return "web/home";
    }
}
