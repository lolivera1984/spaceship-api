package com.spacefleet.spaceshipapi.config.mongo;

import com.mongodb.client.MongoDatabase;
import com.spacefleet.spaceshipapi.model.CollectionName;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "create-collections", order = "000", author = "lolivera")
public class CreateCollectionsChangeLog {

    @Execution
    public void createCollections(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists(CollectionName.USERS.getValue())) {
            mongoTemplate.createCollection(CollectionName.USERS.getValue());
        }
        if (!mongoTemplate.collectionExists(CollectionName.SPACESHIPS.getValue())) {
            mongoTemplate.createCollection(CollectionName.SPACESHIPS.getValue());
        }
    }

    @RollbackExecution
    public void rollback(MongoDatabase db) {
        db.getCollection(CollectionName.USERS.getValue()).drop();
        db.getCollection(CollectionName.SPACESHIPS.getValue()).drop();
    }
}
