package com.returns.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.returns.service.dto.response.ApiResponse;
import com.returns.service.model.CashierUser;
import com.returns.service.repository.CashierUserRepository;
import com.returns.service.util.JwtUtil;
import com.returns.service.util.ResponseMessageUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final CashierUserRepository cashierUserRepository;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
                                   CashierUserRepository cashierUserRepository, ObjectMapper objectMapper,
                                   MessageSource messageSource) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.cashierUserRepository = cashierUserRepository;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpServletRequest requestToUse = request;
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                requestToUse = new CachedBodyHttpServletRequest(request);
            }

            final String authHeader = requestToUse.getHeader("Authorization");

            String jwtToken = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);

                try {
                    username = jwtUtil.extractUsername(jwtToken);
                } catch (Exception e) {
                    log.warn("JWT token extraction failed: {}", e.getMessage());
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, 401, ResponseMessageUtil.JWT_INVALID_TOKEN);
                    return;
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    // Username-token match enforcement for requests with a body
                    if (requestToUse.getMethod().equalsIgnoreCase("POST") || requestToUse.getMethod().equalsIgnoreCase("PUT")) {
                        try {
                            String body = requestToUse.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                            if (body != null && !body.isEmpty()) {
                                com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(body);
                                if (jsonNode.has("username")) {
                                    String requestUsername = jsonNode.get("username").asText();
                                    Optional<CashierUser> user = cashierUserRepository.findByUsername(username);
                                    if(user.isPresent()) {
                                        log.info("Get access token from db");
                                        String accessToken = user.get().getToken().getAccessToken();
                                        if(!accessToken.equals(jwtToken)) {
                                            log.warn("Username in token does not match db store token in request body. Token: {}, Body: {}", username, requestUsername);
                                            sendErrorResponse(response, HttpStatus.FORBIDDEN, 403, ResponseMessageUtil.JWT_TOKEN_MISMATCH);
                                            return;
                                        }
                                    }
                                    if (!username.equals(requestUsername)) {
                                        log.warn("Username in token does not match username in request body. Token: {}, Body: {}", username, requestUsername);
                                        sendErrorResponse(response, HttpStatus.FORBIDDEN, 403, ResponseMessageUtil.JWT_USERNAME_MISMATCH);
                                        return;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("Error reading request body for username-token match: {}", e.getMessage());
                            sendErrorResponse(response, HttpStatus.BAD_REQUEST, 400, ResponseMessageUtil.JWT_INTERNAL_ERROR);
                            return;
                        }
                    }
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(requestToUse));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token validation failed for user: {}", username);
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, 401, ResponseMessageUtil.JWT_VALIDATION_FAILED);
                    return;
                }
            }

            filterChain.doFilter(requestToUse, response);
        } catch (Exception ex) {
            log.error("Exception in JWT filter: ", ex);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, 500, ResponseMessageUtil.JWT_INTERNAL_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, int errorCode, String messageKey) throws IOException {
        String resolvedMessage = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .success(false)
                .message(resolvedMessage)
                .errors(Collections.singletonList(resolvedMessage))
                .errorCode(errorCode)
                .responseTime(LocalDateTime.now())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}