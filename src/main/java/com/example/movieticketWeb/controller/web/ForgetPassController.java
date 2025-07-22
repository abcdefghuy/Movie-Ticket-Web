package com.example.movieticketWeb.controller.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ForgetPassController {
    @GetMapping("/forget-pass")
    public ModelAndView forgetpass() {
        return new ModelAndView("web/forgetpassword");
    }
}
