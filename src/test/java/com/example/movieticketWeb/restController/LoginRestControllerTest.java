package com.example.movieticketWeb.restController;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.movieticketWeb.aop.ApiException;
import com.example.movieticketWeb.dto.request.LoginUserRequest;
import com.example.movieticketWeb.dto.request.RegisterUserRequest;
import com.example.movieticketWeb.dto.response.LoginResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.IRefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(LoginRestControllerTest.MockedBeans.class)
public class LoginRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @TestConfiguration
    static class MockedBeans {
        @Bean
        public AuthenticationService authenticationService() {
            return Mockito.mock(AuthenticationService.class);
        }

        @Bean
        public IRefreshTokenService refreshTokenService() {
            return Mockito.mock(IRefreshTokenService.class);
        }
    }

    private LoginUserRequest loginUserRequest;
    private LoginResponse loginResponse;
    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void initData() {
        loginUserRequest = new LoginUserRequest("nguyensanghuypy@gmail.com", "Huydeptrai1@");
        loginResponse = new LoginResponse(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZ3V5ZW5zYW5naHV5cHlAZ21haWwuY29tIiwiaWF0IjoxNzQ4MDg2MTI5LCJleHAiOjE3NDgwODk3Mjl9.E5LbDkliyFMTWDGmTYrjJTvLzkQvW-oaK_cxDJ6sUQU");
        //        registerUserRequest = new RegisterUserRequest("nguyensanghuypy@gmail.com","Huydeptrai1@");
    }

    @BeforeEach
    void setup() {
        Mockito.reset(authenticationService);
    }

    @Test
    //
    void login_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(loginUserRequest);
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenReturn(loginResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1004))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Login successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(true));
    }

    @Test
    void login_invalidEmailFormat_failure() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(new LoginUserRequest("invalid-email", "password"));
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenReturn(loginResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(3002))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Please provide a valid email address."))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false));
    }

    @Test
    void login_withInvalidCredentials_failure() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        ApiException exception = new ApiException(ErrorCode.INVALID_CREDENTIALS);
        String content = objectMapper.writeValueAsString(new LoginUserRequest("nguyensanghuypy@gmail.com", "password"));
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenThrow(exception);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Invalid email or password"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false));
    }

    @Test
    void login_withUserNotFound_failure() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        ApiException exception = new ApiException(ErrorCode.USER_NOT_FOUND);
        String content = objectMapper.writeValueAsString(new LoginUserRequest("nguyensanghuyp@gmail.com", "password"));
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenThrow(exception);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(4000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false));
    }

    @Test
    void login_withUserNotVerified_failure() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        ApiException exception = new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        String content = objectMapper.writeValueAsString(new LoginUserRequest("nguyensanghuypy@gmail.com", "password"));
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenThrow(exception);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2001))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Account not verified. Please verify your account."))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false));
    }

    @Test
    void login_invalidAuthenticate_failure() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(loginUserRequest);
        ApiException apiException = new ApiException(ErrorCode.INVALID_CREDENTIALS);
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenThrow(apiException);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Invalid email or password"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false));
    }

    @Test
    void logout_validUser_success() throws Exception {
        Person mockUser = new Person();
        mockUser.setEmail("user@example.com");

        var principal = new UsernamePasswordAuthenticationToken(
                mockUser, // 👈 đây là đối tượng Person
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Mockito.doNothing().when(refreshTokenService).deleteRefreshToken(mockUser.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/logout").principal(principal)) // truyền Person vào principal
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1011))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Logout successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(true));
    }

    @Test
    void logout_nullUser_success() throws Exception {
        // không truyền principal, đồng nghĩa user = null
        mockMvc.perform(MockMvcRequestBuilders.post("/api/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1011))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Logout successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(true));
    }
    //
    //    @Test
    //    void createUser_success() throws Exception {
    //        // GIVEN
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        String content = objectMapper.writeValueAsString(loginUserRequest);
    //        //        Mockito.doNothing().when(authenticationService).signup(registerUserRequest);
    //
    //        // WHEN, THEN
    //        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
    //                        .contentType(MediaType.APPLICATION_JSON_VALUE)
    //                        .content(content))
    //                .andExpect(MockMvcResultMatchers.status().isOk())
    //                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1006))
    //                .andExpect(MockMvcResultMatchers.jsonPath("message").value("OTP has been sent to your email
    // address"));
    //    }
}
