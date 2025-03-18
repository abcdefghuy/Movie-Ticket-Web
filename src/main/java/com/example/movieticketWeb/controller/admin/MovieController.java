package com.example.movieticketWeb.controller.admin;

import com.example.movieticketWeb.entity.Person;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class MovieController {
    @GetMapping("/Movie.html")
    public ModelAndView movie(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("admin/Movie");
    }
    @GetMapping("/movies/add")
    public ModelAndView addMovie(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("admin/AddMovie");
    }
    @GetMapping("/movies/edit")
    public ModelAndView editMovie(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("admin/EditMovie");
    }
}
