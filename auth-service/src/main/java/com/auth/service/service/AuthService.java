package com.auth.service.service;

import com.auth.service.dto.request.LoginRequestDTO;
import com.auth.service.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public interface AuthService {
    ResponseEntity<ApiResponse<Object>> login(LoginRequestDTO loginRequestDTO, Locale locale) throws NoSuchAlgorithmException;
}
