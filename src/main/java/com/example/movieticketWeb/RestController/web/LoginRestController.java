package com.example.movieticketWeb.RestController.web;

import com.example.movieticketWeb.dto.request.LoginUserRequest;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.service.IAESService;
import com.example.movieticketWeb.service.AuthenticationService;
import com.example.movieticketWeb.service.JwtService;
import com.example.movieticketWeb.service.IRefreshTokenService;
import com.example.movieticketWeb.service.impl.RefreshTokenServiceImpl;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
public class LoginRestController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final IRefreshTokenService IRefreshTokenService;
    private final IAESService IAESService;

    public LoginRestController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenServiceImpl refreshTokenService, IAESService IAESService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.IRefreshTokenService = refreshTokenService;
        this.IAESService = IAESService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) throws Exception {
        Person authenticatedUser = authenticationService.authenticate(loginUserRequest);

        //  Tạo Access Token & Refresh Token
        String accessToken = jwtService.generateAccessToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        String role = jwtService.extractUserRole(accessToken);
        // 🛡 Mã hóa Access Token trước khi lưu vào cookie
        String encryptedAccessToken = IAESService.encrypt(accessToken);

        //  Lưu vào Redis
        IRefreshTokenService.storeRefreshToken(authenticatedUser.getEmail(), refreshToken);

        // Đặt Access Token vào Cookie
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", encryptedAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60) // 15 phút
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role", role
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "accessToken", required = false) String accessToken, HttpServletResponse response) throws Exception {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token missing");
        }

        // Lấy email từ accessToken
        String userEmail = jwtService.extractUsername(accessToken);
        String refreshToken = IRefreshTokenService.getRefreshToken(userEmail);
        System.out.println("🔑 Refresh Token: " + refreshToken);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
        }

        if (!IRefreshTokenService.isRefreshTokenValid(userEmail, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
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

        return ResponseEntity.ok(Map.of("message", "Token refreshed"));
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

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
