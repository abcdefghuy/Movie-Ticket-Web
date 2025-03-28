package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminHomeRestController {
    private final IPersonService IPersonService;
    @Autowired
    public AdminHomeRestController(IPersonService IPersonService) {
        this.IPersonService = IPersonService;
    }
    @GetMapping("/adminInfo")
    public ResponseEntity<?> adminInfoGetAdminInfo(@AuthenticationPrincipal Person person) {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }
        PersonInfoResponse personInfoResponse = IPersonService.getInfo(person);
        return ResponseEntity.ok(personInfoResponse);
    }
}
