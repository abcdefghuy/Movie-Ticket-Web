package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/pop-corn")
public class PopcornController {
    @GetMapping("/pop-corn-list")
    public ModelAndView popCornList() {
        return new ModelAndView("admin/popcorn/popcorn-list");
    }
}
