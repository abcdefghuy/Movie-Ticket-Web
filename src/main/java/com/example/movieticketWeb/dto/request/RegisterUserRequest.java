package com.example.movieticketWeb.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    @NotNull(message = "Full name is required")
    @Size(min = 1, message = "Full name is required")
    private String username;

    @NotNull(message = "Email is required")
    @Size(min = 1, message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Gender is required")
    @Min(0) @Max(1)
    private Integer gender; // 0: Male, 1: Female

    @NotNull(message = "Region is required")
    private String region;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

}
