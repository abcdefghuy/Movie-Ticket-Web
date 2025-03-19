package com.example.movieticketWeb.service;

public interface IAESService {
    public String encrypt(String data) throws Exception;
    public String decrypt(String encryptedData) throws Exception;
}
