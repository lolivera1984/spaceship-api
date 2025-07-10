package com.spacefleet.spaceshipapi.controller;

import com.spacefleet.spaceshipapi.dto.UserDTO;
import com.spacefleet.spaceshipapi.service.JwtService;
import com.spacefleet.spaceshipapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userCredentials) {
        String username = userCredentials.getUsername();
        String password = userCredentials.getPassword();

        log.info("AuthController, Attempting login for username: {}", username);

        boolean areValid = userService.areCredentialsValid(username, password);
        if (areValid) {
            log.info("AuthController, Login successful for username: {}", username);

            String token = jwtService.generateToken(username);
            log.info("AuthController, token generated for username: {}", username);

            return ResponseEntity.ok(Map.of("token", token));
        } else {
            log.info("AuthController, Login failed for username: {}", username);

            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
