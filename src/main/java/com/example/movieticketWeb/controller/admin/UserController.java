package com.example.movieticketWeb.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class UserController {
    @GetMapping("/user/user-list.html")
    public ModelAndView userList(){
        return new ModelAndView("admin/user-list");
    }
    @GetMapping("/user/user-add.html")
    public ModelAndView userAdd(){
        return new ModelAndView("admin/user-add");
    }
    @GetMapping("/user/{id}/edit")
    public ModelAndView userEdit(@PathVariable Long id){
        ModelAndView modelAndView=new  ModelAndView("admin/user-edit");
        modelAndView.addObject("userId",id);
        return modelAndView;
    }
}
