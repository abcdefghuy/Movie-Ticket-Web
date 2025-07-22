package com.example.movieticketWeb.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest {
    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "INVALID_EMAIL_FORMAT")
    private String email;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String password;
}
