package com.spacefleet.spaceshipapi.service;

import com.spacefleet.spaceshipapi.dto.PaginatedResponseDTO;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.exception.NotFoundException;
import com.spacefleet.spaceshipapi.model.Spaceship;
import com.spacefleet.spaceshipapi.repository.SpaceshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpaceshipServiceTest {

    @Mock
    private SpaceshipRepository repository;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private SpaceshipService service;

    private Spaceship spaceship;
    private SpaceshipDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        spaceship = Spaceship.builder()
                .id("1")
                .name("X-Wing")
                .model("T-65")
                .manufacturer("Incom Corporation")
                .build();

        dto = new SpaceshipDTO("1", "X-Wing", "T-65", "Incom Corporation");
    }

    @Test
    @DisplayName("should return all spaceships as DTOs")
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(spaceship));

        List<SpaceshipDTO> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("X-Wing", result.get(0).name());
    }

    @Test
    @DisplayName("should return spaceship by id")
    void testFindById() {
        when(repository.findById("1")).thenReturn(Optional.of(spaceship));

        SpaceshipDTO result = service.findById("1");

        assertEquals("X-Wing", result.name());
    }

    @Test
    @DisplayName("should throw when spaceship not found by id")
    void testFindByIdNotFound() {
        when(repository.findById("2")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById("2"));
    }

    @Test
    @DisplayName("should create spaceship and return DTO")
    void testCreate() {
        when(repository.save(any())).thenReturn(spaceship);

        SpaceshipDTO result = service.create(dto);

        assertEquals("X-Wing", result.name());
    }

    @Test
    @DisplayName("should update existing spaceship")
    void testUpdate() {
        when(repository.findById("1")).thenReturn(Optional.of(spaceship));
        when(repository.save(any())).thenReturn(spaceship);

        SpaceshipDTO updated = service.update("1", new SpaceshipDTO("1", "X-Wing MkII", "T-70", "NewTech"));

        assertEquals("X-Wing MkII", updated.name());
        assertEquals("T-70", updated.model());
    }

    @Test
    @DisplayName("should throw when updating non-existent spaceship")
    void testUpdateNotFound() {
        when(repository.findById("9")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update("9", dto));
    }

    @Test
    @DisplayName("should delete spaceship and push kafka event")
    void testDelete() {
        when(repository.existsById("1")).thenReturn(true);

        service.delete("1");

        verify(repository).deleteById("1");
        verify(kafkaTemplate).send(eq("spaceship.events"), contains("Deleted spaceship"));
    }

    @Test
    @DisplayName("should throw when deleting non-existent spaceship")
    void testDeleteNotFound() {
        when(repository.existsById("9")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete("9"));
    }

    @Test
    @DisplayName("should return paginated list of spaceships")
    void testFindAllPaginated() {
        Page<Spaceship> page = new PageImpl<>(List.of(spaceship), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResponseDTO<SpaceshipDTO> result = service.findAllPaginated(0, 10);

        assertEquals(1, result.totalElements());
        assertEquals("X-Wing", result.content().get(0).name());
    }

    @Test
    @DisplayName("should return paginated list filtered by name")
    void testFindByNamePaginated() {
        Page<Spaceship> page = new PageImpl<>(List.of(spaceship));
        when(repository.findByNameContainingIgnoreCase(eq("X"), any(Pageable.class))).thenReturn(page);

        PaginatedResponseDTO<SpaceshipDTO> result = service.findByNamePaginated("X", 0, 10);

        assertEquals(1, result.totalElements());
    }

    @Test
    @DisplayName("should throw for invalid pagination result")
    void testBuildPaginatedResponseShouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () ->
                service.findAllPaginated(0, 0));
    }
}
