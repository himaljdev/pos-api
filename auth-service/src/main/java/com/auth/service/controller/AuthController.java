package com.auth.service.controller;

import com.auth.service.dto.request.LoginRequestDTO;
import com.auth.service.dto.request.validator.LoginRequestValidatorDTO;
import com.auth.service.dto.response.ApiResponse;
import com.auth.service.service.AuthService;
import com.auth.service.validator.OnLogin;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@RestController
@RequestMapping(path = "api/v1/auth")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final Gson gson;

    @PostMapping(path = "/login",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle login request ",notes = "Login request success or failed")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Validated(OnLogin.class) LoginRequestValidatorDTO loginRequestValidatorDTO, Locale locale) throws NoSuchAlgorithmException {
        log.info("Login attempt for username: {}", loginRequestValidatorDTO.getUsername());
        return authService.login(gson.fromJson(gson.toJson(loginRequestValidatorDTO), LoginRequestDTO.class), locale);
    }

}
