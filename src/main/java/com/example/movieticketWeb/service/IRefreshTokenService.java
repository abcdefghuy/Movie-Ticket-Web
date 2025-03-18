package com.example.movieticketWeb.service;

public interface IRefreshTokenService {
    public void storeRefreshToken(String userEmail, String refreshToken);
    public boolean isRefreshTokenValid(String userEmail, String refreshToken);
    public void deleteRefreshToken(String userEmail);
    public String getRefreshToken(String userEmail);
}
