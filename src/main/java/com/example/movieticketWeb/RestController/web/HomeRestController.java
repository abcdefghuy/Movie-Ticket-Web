package com.example.movieticketWeb.RestController.web;

import com.example.movieticketWeb.enumeration.City;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeRestController {

    @GetMapping("/regions")
    public ResponseEntity<List<String>> getRegions() {
        List<String> regions =  City.getListCity();
        System.out.println(regions);
        return ResponseEntity.ok(regions);
    }

}
