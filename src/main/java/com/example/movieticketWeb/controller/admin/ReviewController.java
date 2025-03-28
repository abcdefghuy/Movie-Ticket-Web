package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/review")
public class ReviewController {
    @GetMapping()
    public ModelAndView list(@RequestParam(name="movieId", defaultValue = "") Long id) {
        return new ModelAndView("admin/review");
    }
}
