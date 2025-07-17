package com.spacefleet.spaceshipapi.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String BASE64_SECRET = "c2VjdXJlc2VjdXJlc2VjdXJlc2VjdXJlc2VjdXJlMw=="; // "securesecuresecuresecuresecure3" (32 bytes)

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        String secureKey = Base64.getEncoder().encodeToString(
                Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()
        );

        ReflectionTestUtils.setField(jwtService, "secret", secureKey);
    }

    @Test
    @DisplayName("should generate a valid JWT token")
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    @DisplayName("should validate a valid token successfully")
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("testuser");
        boolean isValid = jwtService.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("should extract username from token")
    void getUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken("testuser");
        String username = jwtService.getUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("should return false for invalid token")
    void validateToken_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";
        boolean isValid = jwtService.validateToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("should throw exception when secret is not configured")
    void generateToken_shouldThrowExceptionWhenSecretIsMissing() {
        JwtService brokenService = new JwtService();
        Exception exception = assertThrows(IllegalStateException.class, () ->
                ReflectionTestUtils.invokeMethod(brokenService, "getSigningKey")
        );
        assertEquals("JwtService, JWT secret is not configured.", exception.getMessage());
    }
}
