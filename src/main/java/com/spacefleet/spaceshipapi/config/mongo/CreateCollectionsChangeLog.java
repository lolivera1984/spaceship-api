package com.spacefleet.spaceshipapi.config.mongo;

import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "create-collections", order = "000", author = "lolivera")
public class CreateCollectionsChangeLog {

    @Execution
    public void createCollections(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("users")) {
            mongoTemplate.createCollection("users");
        }
        if (!mongoTemplate.collectionExists("spaceships")) {
            mongoTemplate.createCollection("spaceships");
        }
    }

    @RollbackExecution
    public void rollback(MongoDatabase db) {
        db.getCollection("users").drop();
        db.getCollection("spaceships").drop();
    }
}
