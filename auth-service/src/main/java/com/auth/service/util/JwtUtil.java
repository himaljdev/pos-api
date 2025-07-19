package com.auth.service.util;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Log4j2
public class JwtUtil {

    @Value("${jwt.token.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.token.exp.time}")
    private long EXP_TIME;

    public String generateToken(String username) {
        log.info("Generate token {}", username);
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(getSignInKey())
                .compact();

    }

    public String extractUsername(String token) {
        log.info("Extract username from token {}", token);
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        log.info("Extracting claims from token {}", token);
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        log.info("Get Sign In Key {}", SECRET_KEY);
        return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            log.debug("Validating token...");

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            String usernameFromToken = claims.getSubject();
            Date expiration = claims.getExpiration();

            boolean isTokenValid = usernameFromToken.equals(userDetails.getUsername()) && !expiration.before(new Date());

            if (!isTokenValid) {
                log.warn("Token validation failed. Username or expiration invalid.");
            } else {
                log.debug("Token is valid for user: {}", usernameFromToken);
            }

            return isTokenValid;

        } catch (ExpiredJwtException e) {
            log.warn("Token expired at {}", e.getClaims().getExpiration());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }

}