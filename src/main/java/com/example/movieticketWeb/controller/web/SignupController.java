package com.example.movieticketWeb.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignupController {

    @GetMapping("/signup")
    public ModelAndView showSignupPage() {
        return new ModelAndView("web/signup");
    }
}
