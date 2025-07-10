package com.spacefleet.spaceshipapi.config.mongo;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockConfig {

    @Bean
    public MongockApplicationRunner mongockApplicationRunner(MongoTemplate mongoTemplate, ApplicationContext context) {
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);

        return MongockSpringboot.builder()
                .setDriver(driver)
                .addMigrationScanPackage("com.spacefleet.spaceshipapi.config.mongo")
                .setSpringContext(context)
                .buildApplicationRunner();
    }
}
