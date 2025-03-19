package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.service.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60; // 7 ngày

    public void storeRefreshToken(String userEmail, String refreshToken) {
        String key = "refreshToken:" + userEmail;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRY, TimeUnit.SECONDS);
    }

    public boolean isRefreshTokenValid(String userEmail, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refreshToken:" + userEmail);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete("refreshToken:" + userEmail);
    }

    @Override
    public String getRefreshToken(String userEmail) {
        String key = "refreshToken:" + userEmail;
        return redisTemplate.opsForValue().get(key);
    }
}
