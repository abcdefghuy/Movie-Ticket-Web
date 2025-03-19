package com.example.movieticketWeb.controller.user;

import com.example.movieticketWeb.entity.Person;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserHomeController {
    @GetMapping("/userHome")
    public ModelAndView userHome(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_USER")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("user/home");
    }
}
