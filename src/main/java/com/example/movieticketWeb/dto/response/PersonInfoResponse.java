package com.example.movieticketWeb.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@Builder
public class PersonInfoResponse {
    private Integer id;
    private String userName;
    private String email;
    private String phone;
    private String regions;
    private LocalDate birthday;
    private String gender;
}
