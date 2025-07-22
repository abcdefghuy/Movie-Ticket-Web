package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/cinema")
public class CinemaController {
    @GetMapping("/cinema-list")
    public ModelAndView list() {
        return new ModelAndView("admin/cinema-list");
    }

    @RequestMapping("/editCinema")
    public String editCinema() {
        return "admin/cinema-edit";
    }
}
