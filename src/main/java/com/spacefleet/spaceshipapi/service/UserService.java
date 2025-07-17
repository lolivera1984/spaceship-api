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

    private static final String MESSAGE_VALIDATING_CREDENTIALS = "UserService, Validating credentials for username: {}";
    private static final String ERROR_MESSAGE_ERROR_VALIDATING_CREDENTIALS = "UserService, Error validating credentials for username {}: {}";


    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    public boolean areCredentialsValid(String username, String password) {
        log.info(MESSAGE_VALIDATING_CREDENTIALS, username);

        if (username == null || password == null) {
            return false;
        }

        try {
            return repository.findByUsername(username).stream()
                    .filter(Objects::nonNull)
                    .anyMatch(user -> password.equalsIgnoreCase(user.getPassword()));

        } catch (Exception e) {
            log.error(ERROR_MESSAGE_ERROR_VALIDATING_CREDENTIALS, username, e.getMessage(), e);
            throw e;
        }
    }

}
