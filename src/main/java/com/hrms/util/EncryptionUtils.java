package com.hrms.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {
    public static String encryptWithMD5(String input) throws NoSuchAlgorithmException {
        // 獲取 MD5 的 MessageDigest instance
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 將字串轉換成 byte 陣列並進行 MD5 加密
        byte[] messageDigest = md.digest(input.getBytes());

        // 將 byte 陣列轉換成 16 進制格式的字串
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
