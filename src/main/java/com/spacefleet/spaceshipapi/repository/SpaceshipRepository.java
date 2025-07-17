package com.spacefleet.spaceshipapi.repository;

import com.spacefleet.spaceshipapi.model.Spaceship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceshipRepository extends MongoRepository<Spaceship, String> {

    Page<Spaceship> findAll(Pageable pageable);

    Page<Spaceship> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
