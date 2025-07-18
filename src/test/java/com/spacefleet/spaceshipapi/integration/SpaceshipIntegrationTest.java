package com.spacefleet.spaceshipapi.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.model.User;
import com.spacefleet.spaceshipapi.repository.AbstractMongoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class SpaceshipIntegrationTest extends AbstractMongoTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.save(new User("id1", "name1", "admin", "admin123"), "users");
    }

    @Test
    public void testCreateAndFetchSpaceship() throws Exception {
        WebTestClient client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        String loginRequest = """
        {
          "username": "admin",
          "password": "admin123"
        }
        """;

        byte[] responseBody = client.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        Map<String, String> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});
        String token = responseMap.get("token");

        SpaceshipDTO newShip = new SpaceshipDTO(null, "TestShip", "Model-T", "TestCorp");
        client.post()
                .uri("/spaceships")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(newShip))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestShip");

        client.get()
                .uri("/spaceships/search?name=TestShip")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].model").isEqualTo("Model-T");
    }
}
