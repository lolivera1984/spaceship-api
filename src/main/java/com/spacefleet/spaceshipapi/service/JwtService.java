package com.spacefleet.spaceshipapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    private static final long EXPIRATION = 1000 * 60 * 60;
    private static final String ERROR_MESSAGE_JWT_SECRET_NOT_CONFIG = "JwtService, JWT secret is not configured.";
    private static final String ERROR_MESSAGE_ERROR_GENERATING_TOKEN = "JwtService, Error generating token for username {}: {}";
    private static final String ERROR_MESSAGE_ERROR_VALIDATING_TOKEN = "JwtService, Error validating token: {}";

    private Key getSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(ERROR_MESSAGE_JWT_SECRET_NOT_CONFIG);
        }
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String username) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

        } catch (Exception e) {
            log.error(ERROR_MESSAGE_ERROR_GENERATING_TOKEN, username, e.getMessage(), e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error(ERROR_MESSAGE_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}
