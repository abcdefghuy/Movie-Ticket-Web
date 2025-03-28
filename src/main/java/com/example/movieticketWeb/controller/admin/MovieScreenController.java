package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/movie-screen")
public class MovieScreenController {
    @GetMapping()
    public ModelAndView showMovieScreenList(@RequestParam(name = "roomId") Long roomId) {
        return new ModelAndView("admin/movieScreen-list");
    }
    @GetMapping("/editMovieScreening")
    public ModelAndView editMovieScreening(@RequestParam(name = "roomId") Long roomId, @RequestParam(name = "msId") Long msId) {
        return new ModelAndView("admin/movieScreen-edit");
    }
}
