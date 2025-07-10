package com.spacefleet.spaceshipapi.service;

import com.spacefleet.spaceshipapi.model.User;
import com.spacefleet.spaceshipapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    public boolean areCredentialsValid(String username, String password) {
        log.info("UserService, Validating credentials for username: {}", username);

        if (username == null || password == null) {
            return false;
        }

        try {
            return repository.findByUsername(username).stream()
                    .filter(Objects::nonNull)
                    .anyMatch(user -> password.equalsIgnoreCase(user.getPassword()));

        } catch (Exception e) {
            log.error("UserService, Error validating credentials for username {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

}
