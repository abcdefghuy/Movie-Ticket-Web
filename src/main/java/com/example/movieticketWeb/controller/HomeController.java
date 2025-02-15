package com.example.movieticketWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "web/home";
    }
    @GetMapping("/signin")
    public String signin() {
        return "web/signin";
    }
    @GetMapping("/signup")
    public String signup() {
        return "web/signup";
    }
    @GetMapping("/forgetpass")
    public String forgetpass() {
        return "web/forgetpassword";
    }
    @GetMapping("/changepass")
    public String changepass() {
        return "web/changepassword";
    }
}
