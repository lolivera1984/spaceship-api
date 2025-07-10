package com.spacefleet.spaceshipapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpaceshipApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpaceshipApiApplication.class, args);
    }
}
