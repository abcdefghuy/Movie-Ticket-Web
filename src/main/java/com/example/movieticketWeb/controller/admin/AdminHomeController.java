package com.example.movieticketWeb.controller.admin;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.movieticketWeb.entity.Person;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @GetMapping("/adminHome")
    public ModelAndView adminHome(@AuthenticationPrincipal Person person) throws IOException {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("admin/home"); // Nếu là admin, chuyển đến trang quản lý rạp chiếu
    }
}
