package com.cleanyco.weblab4back.util;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class MD5PasswordEncoder {
    private static MessageDigest md;

    public static final String hash(String message) {
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(message.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
