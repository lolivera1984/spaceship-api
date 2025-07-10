package com.spacefleet.spaceshipapi.config.mongo;

import com.spacefleet.spaceshipapi.model.Spaceship;
import com.spacefleet.spaceshipapi.model.User;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.util.List;

@ChangeUnit(id = "initial-data-loader", order = "001", author = "lolivera")
public class InitialDataChangeLog {

    @Execution
    public void insertInitialData(MongoTemplate mongoTemplate) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Load users
        InputStream userStream = new ClassPathResource("data/users.json").getInputStream();
        List<User> users = mapper.readValue(userStream, new TypeReference<>() {});
        users.forEach(user -> mongoTemplate.save(user, "users"));

        // Load spaceships
        InputStream shipStream = new ClassPathResource("data/spaceships.json").getInputStream();
        List<Spaceship> ships = mapper.readValue(shipStream, new TypeReference<>() {});
        ships.forEach(ship -> mongoTemplate.save(ship, "spaceships"));
    }

    @RollbackExecution
    public void rollback(MongoDatabase db) {
        db.getCollection("users").deleteMany(new org.bson.Document());
        db.getCollection("spaceships").deleteMany(new org.bson.Document());
    }
}