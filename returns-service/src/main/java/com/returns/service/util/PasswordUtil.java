/**
 * User: Himal_J
 * Date: 2/5/2025
 * Time: 7:39 AM
 * <p>
 */

package com.returns.service.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
public class PasswordUtil {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String passwordEncoder(String saltKey, String password) throws NoSuchAlgorithmException {
        try {
            log.info("Password Encoder {}", password);
            String passwordWithSalt = saltKey + password;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(passwordWithSalt.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (Exception e) {
            log.error(e);
            throw e;
        }

    }


    public static boolean matches(String rawPassword, String encodedPassword) {
        String sha256Hashed = sha256(rawPassword);
        log.info("SHA-256 raw password: {}", sha256Hashed);
        log.info("BCrypt encoded password: {}", encodedPassword);
        return bCryptPasswordEncoder.matches(sha256Hashed, encodedPassword);
    }

}
