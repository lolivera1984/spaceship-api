package com.spacefleet.spaceshipapi.config.mongo;

import com.spacefleet.spaceshipapi.model.CollectionName;
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

    private static final String COLLECTION_PATH_LOAD_USERS = "data/users.json";
    private static final String COLLECTION_PATH_LOAD_SPACESHIPS = "data/spaceships.json";

    @Execution
    public void insertInitialData(MongoTemplate mongoTemplate) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        InputStream userStream = new ClassPathResource(COLLECTION_PATH_LOAD_USERS).getInputStream();
        List<User> users = mapper.readValue(userStream, new TypeReference<>() {});
        users.forEach(user -> mongoTemplate.save(user, CollectionName.USERS.getValue()));

        InputStream shipStream = new ClassPathResource(COLLECTION_PATH_LOAD_SPACESHIPS).getInputStream();
        List<Spaceship> ships = mapper.readValue(shipStream, new TypeReference<>() {});
        ships.forEach(ship -> mongoTemplate.save(ship, CollectionName.SPACESHIPS.getValue()));
    }

    @RollbackExecution
    public void rollback(MongoDatabase db) {
        db.getCollection(CollectionName.USERS.getValue()).deleteMany(new org.bson.Document());
        db.getCollection(CollectionName.SPACESHIPS.getValue()).deleteMany(new org.bson.Document());
    }
}