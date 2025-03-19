package com.example.movieticketWeb.controller.admin;

import com.example.movieticketWeb.entity.Person;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @GetMapping("/adminHome")
    public ModelAndView adminHome( @AuthenticationPrincipal Person person) throws IOException {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return new ModelAndView("web/signin");
        }
        return new ModelAndView("admin/home"); // Nếu là admin, chuyển đến trang quản lý rạp chiếu
    }
}
