package com.spacefleet.spaceshipapi.repository;

import com.spacefleet.spaceshipapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String id);

    List<User> findByUsername(String username);

}