package com.example.movieticketWeb.controller;

import com.example.movieticketWeb.enumeration.City;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "web/home";
    }





    @GetMapping("/regions")
    public ResponseEntity<List<String>> getRegions() {
        List<String> regions =  City.getListCity();
        System.out.println(regions);
        return ResponseEntity.ok(regions);
    }

}
