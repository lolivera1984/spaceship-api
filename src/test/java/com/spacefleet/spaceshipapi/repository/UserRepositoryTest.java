package com.spacefleet.spaceshipapi.repository;

import com.spacefleet.spaceshipapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("should save and find user by id")
    void testFindById() {
        User user = User.builder()
                .id("user123")
                .name("Leo")
                .username("testuser")
                .password("secret123")
                .build();
        repository.save(user);

        Optional<User> result = repository.findById("user123");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("should find user(s) by username")
    void testFindByUsername() {
        User user1 = User.builder().name("Leo").username("testuser").password("123").build();
        User user2 = User.builder().name("Clone").username("testuser").password("456").build();
        User user3 = User.builder().name("Other").username("anotheruser").password("789").build();

        repository.saveAll(List.of(user1, user2, user3));

        List<User> result = repository.findByUsername("testuser");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Leo")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Clone")));
    }
}
