package com.spacefleet.spaceshipapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacefleet.spaceshipapi.config.TestSecurityConfig;
import com.spacefleet.spaceshipapi.dto.UserDTO;
import com.spacefleet.spaceshipapi.service.JwtService;
import com.spacefleet.spaceshipapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should return token when credentials are valid")
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        UserDTO userDTO = new UserDTO("123", "Leo", "testuser", "secret123");
        Mockito.when(userService.areCredentialsValid("testuser", "secret123")).thenReturn(true);
        Mockito.when(jwtService.generateToken("testuser")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    @DisplayName("should return 401 when credentials are invalid")
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
        UserDTO userDTO = new UserDTO("123", "Leo", "testuser", "wrongpass");
        Mockito.when(userService.areCredentialsValid("testuser", "wrongpass")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}

