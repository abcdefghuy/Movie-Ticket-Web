package com.example.movieticketWeb.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import com.example.movieticketWeb.entity.Res;

public interface IImageService {
    public Res uploadImageToDrive(File file, String fileName) throws GeneralSecurityException, IOException;

    String findUrlByFileName(String fileName);

    InputStream getFileInputStream(String fileId) throws GeneralSecurityException, IOException;
}
