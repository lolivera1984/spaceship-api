package com.spacefleet.spaceshipapi.service;

import com.spacefleet.spaceshipapi.model.User;
import com.spacefleet.spaceshipapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id("1")
                .username("testuser")
                .password("secret123")
                .name("Leo")
                .build();
    }

    @Test
    @DisplayName("should return user by ID")
    void testFindById() {
        when(repository.findById("1")).thenReturn(Optional.of(user));

        Optional<User> result = service.findById("1");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("should return empty if user not found")
    void testFindByIdNotFound() {
        when(repository.findById("2")).thenReturn(Optional.empty());

        Optional<User> result = service.findById("2");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("should validate correct credentials")
    void testAreCredentialsValid_shouldReturnTrue() {
        when(repository.findByUsername("testuser")).thenReturn(List.of(user));

        boolean valid = service.areCredentialsValid("testuser", "secret123");

        assertTrue(valid);
    }

    @Test
    @DisplayName("should return false for wrong password")
    void testAreCredentialsValid_shouldReturnFalseOnWrongPassword() {
        when(repository.findByUsername("testuser")).thenReturn(List.of(user));

        boolean valid = service.areCredentialsValid("testuser", "wrongpass");

        assertFalse(valid);
    }

    @Test
    @DisplayName("should return false when username or password is null")
    void testAreCredentialsValid_withNullInputs() {
        assertFalse(service.areCredentialsValid(null, "123"));
        assertFalse(service.areCredentialsValid("user", null));
    }

    @Test
    @DisplayName("should throw and log when repository fails")
    void testAreCredentialsValid_shouldThrowOnRepositoryException() {
        when(repository.findByUsername("testuser")).thenThrow(new RuntimeException("DB down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.areCredentialsValid("testuser", "secret123"));

        assertEquals("DB down", ex.getMessage());
    }
}
