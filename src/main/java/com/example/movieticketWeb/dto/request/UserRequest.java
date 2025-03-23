package com.example.movieticketWeb.dto.request;

import groovy.transform.builder.Builder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;
    @NotBlank(message = "Role cannot be empty")
    private String role;
    @NotNull(message = "Gender cannot be null")
    private Integer gender;
    @NotNull(message = "Birth date cannot be empty")
    @Past(message = "Birth date must be in the past")
    private Date birthDate;
    @NotBlank(message = "Region cannot be empty")
    private String region;
}
