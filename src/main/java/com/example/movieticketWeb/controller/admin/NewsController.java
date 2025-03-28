package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/news")
public class NewsController {
    @GetMapping("/news-list")
    public ModelAndView listNews() {
        return  new ModelAndView("admin/newsordiscount-list");
    }

    @GetMapping("/editNews")
    public ModelAndView editNews(@RequestParam("newsID") int id) {
        return new ModelAndView("admin/newsordiscount-edit");
    }
    @GetMapping("/addNews")
    public ModelAndView addNews() {
        return new ModelAndView("admin/newsordiscount-add");
    }


}
