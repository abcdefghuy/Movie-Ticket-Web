package com.example.movieticketWeb.controller.admin;

import com.example.movieticketWeb.entity.Person;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/room")
public class RoomController {
    @GetMapping("")
    public ModelAndView list(@RequestParam(name = "cinemaId", required = false) String cinemaId) {
        ModelAndView modelAndView = new ModelAndView("admin/room-list");
        // Nếu có cinemaId, thêm vào model để hiển thị
        if (cinemaId != null) {
            modelAndView.addObject("cinemaId", cinemaId);
        }

        return modelAndView;
    }
    @GetMapping("/editRoom")
    public ModelAndView editRoom(@RequestParam("roomId") Long roomId,
                                 @RequestParam("cinemaId") Long cinemaId) {
        ModelAndView modelAndView = new ModelAndView("admin/room-edit");
        modelAndView.addObject("roomId", roomId);
        modelAndView.addObject("cinemaId", cinemaId);
        return modelAndView;
    }
}
