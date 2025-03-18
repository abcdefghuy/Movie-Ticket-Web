package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.service.IAESService;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AESServiceImpl implements IAESService {
    private static final String AES_CIPHER = "AES/CBC/PKCS5Padding"; // ✅ Sử dụng CBC với PKCS5Padding
    private static final String SECRET_KEY = "khawng08ansy0820"; // 16-byte key (bạn có thể thay đổi)
    private static final String IV_STRING =  "asmdnsdijhasd1d2"; // 16-byte IV

    private static final SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    private static final IvParameterSpec iv = new IvParameterSpec(IV_STRING.getBytes());

    public  String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public  String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
}
