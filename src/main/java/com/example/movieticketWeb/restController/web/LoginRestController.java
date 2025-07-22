package com.example.movieticketWeb.restController.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.aop.ApiException;
import com.example.movieticketWeb.dto.request.LoginUserRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.LoginResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.IAESService;
import com.example.movieticketWeb.service.IRefreshTokenService;
import com.example.movieticketWeb.service.JwtService;
import com.example.movieticketWeb.service.impl.RefreshTokenServiceImpl;

@RestController
public class LoginRestController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final IRefreshTokenService IRefreshTokenService;
    private final IAESService IAESService;

    public LoginRestController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            RefreshTokenServiceImpl refreshTokenService,
            IAESService IAESService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.IRefreshTokenService = refreshTokenService;
        this.IAESService = IAESService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response)
            throws Exception {
        try {
            LoginResponse authenticatedUser = authenticationService.authenticate(loginUserRequest);

            // Đặt Access Token vào Cookie
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", authenticatedUser.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(15 * 60) // 15 phút
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.LOGIN_SUCCESS, authenticatedUser));
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getErrorCode()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "accessToken", required = false) String accessToken, HttpServletResponse response)
            throws Exception {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.ACCESS_TOKEN_MISSING));
        }

        // Lấy email từ accessToken
        String userEmail = jwtService.extractUsername(accessToken);
        String refreshToken = IRefreshTokenService.getRefreshToken(userEmail);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        if (!IRefreshTokenService.isRefreshTokenValid(userEmail, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        //  Tạo Access Token mới
        Person user = authenticationService.loadUserByEmail(userEmail);
        String newAccessToken = jwtService.generateAccessToken(user);
        String encryptedAccessToken = IAESService.encrypt(newAccessToken);

        // Đặt Access Token mới vào Cookie
        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", encryptedAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60) // 15 phút
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS_REFRESH_TOKEN));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @AuthenticationPrincipal Person user) {
        if (user != null) {
            IRefreshTokenService.deleteRefreshToken(user.getEmail());
        }

        ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // Xóa cookie ngay lập tức
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken.toString());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.LOGOUT_SUCCESS));
    }
}
