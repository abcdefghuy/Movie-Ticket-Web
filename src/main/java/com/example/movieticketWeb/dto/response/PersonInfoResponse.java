package com.example.movieticketWeb.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfoResponse {
    private Integer id;
    private String userName;
    private String email;
    private String phone;
    private String regions;
    private Date birthday;
    private String gender;
    private String role;
}
