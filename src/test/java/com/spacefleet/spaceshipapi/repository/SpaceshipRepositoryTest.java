package com.spacefleet.spaceshipapi.repository;

import com.spacefleet.spaceshipapi.model.Spaceship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class SpaceshipRepositoryTest extends AbstractMongoTest {

    @Autowired
    private SpaceshipRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }
    @Test
    @DisplayName("should save and find spaceship by name (case-insensitive)")
    void testFindByNameContainingIgnoreCase() {
        repository.deleteAll();
        Spaceship s1 = Spaceship.builder().name("X-Wing").model("T-65").manufacturer("Incom").build();
        Spaceship s2 = Spaceship.builder().name("Y-Wing").model("BTL-A4").manufacturer("Koensayr").build();
        Spaceship s3 = Spaceship.builder().name("TIE Fighter").model("Twin Ion Engine").manufacturer("Sienar").build();

        repository.saveAll(List.of(s1, s2, s3));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Spaceship> result = repository.findByNameContainingIgnoreCase("wing", pageable);

        assertEquals(2, result.getTotalElements());
        List<String> names = result.getContent().stream().map(Spaceship::getName).toList();
        assertTrue(names.contains("X-Wing"));
        assertTrue(names.contains("Y-Wing"));
    }

    @Test
    @DisplayName("should return paginated results")
    void testFindAllPagination() {
        repository.deleteAll();
        for (int i = 1; i <= 5; i++) {
            repository.save(Spaceship.builder()
                    .name("Ship " + i)
                    .model("Model " + i)
                    .manufacturer("Maker " + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));

        Page<Spaceship> page = repository.findAll(pageable);

        assertEquals(2, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
    }
}
