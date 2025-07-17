package com.spacefleet.spaceshipapi.service;

import com.spacefleet.spaceshipapi.dto.PaginatedResponseDTO;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.exception.NotFoundException;
import com.spacefleet.spaceshipapi.model.Spaceship;
import com.spacefleet.spaceshipapi.repository.SpaceshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpaceshipService {

    private final SpaceshipRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "spaceship.events";
    private static final String ERROR_MESSAGE_RESULT_CANT_BE_NULL = "Result Page can not be null";
    private static final String ERROR_MESSAGE_INVALID_PAGINATION_PARAMS = "Invalid pagination params";
    private static final String ERROR_MESSAGE_SPACESHIP_NOT_FOUND_WITH_ID = "Spaceship not found with id: ";
    private static final String ERROR_MESSAGE_SPACESHIP_FAILED_PUSH_KAFKA = "Failed to send deletion event to Kafka for spaceship with ID {}: {}";
    private static final String MESSAGE_DELETED_SPACESHIP_WITH_ID = "Deleted spaceship: ";

    @Autowired
    public SpaceshipService(SpaceshipRepository repository
            , KafkaTemplate<String, String> kafkaTemplate
            ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<SpaceshipDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PaginatedResponseDTO<SpaceshipDTO> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Spaceship> pageResult = repository.findAll(pageable);
        return buildPaginatedResponse(pageResult);
    }

    private PaginatedResponseDTO<SpaceshipDTO> buildPaginatedResponse(Page<Spaceship> spaceshipPage) {
        if (spaceshipPage == null) {
            throw new IllegalArgumentException(ERROR_MESSAGE_RESULT_CANT_BE_NULL);
        }
        if (spaceshipPage.getNumber() < 0 || spaceshipPage.getSize() <= 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_PAGINATION_PARAMS);
        }

        List<SpaceshipDTO> content = spaceshipPage
                .getContent()
                .stream()
                .map(this::toDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                spaceshipPage.getNumber(),
                spaceshipPage.getSize(),
                spaceshipPage.getTotalElements(),
                spaceshipPage.getTotalPages()
        );
    }

    @Cacheable("spaceshipById")
    public SpaceshipDTO findById(String id) {
        Spaceship ship = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ERROR_MESSAGE_SPACESHIP_NOT_FOUND_WITH_ID + id));
        return toDTO(ship);
    }

    public PaginatedResponseDTO<SpaceshipDTO> findByNamePaginated(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Spaceship> pageResult = repository.findByNameContainingIgnoreCase(name, pageable);
        return buildPaginatedResponse(pageResult);
    }

    public SpaceshipDTO create(SpaceshipDTO dto) {
        Spaceship entity = toEntity(dto);
        Spaceship saved = repository.save(entity);
        return toDTO(saved);
    }

    public SpaceshipDTO update(String id, SpaceshipDTO dto) {
        Optional<Spaceship> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException(ERROR_MESSAGE_SPACESHIP_NOT_FOUND_WITH_ID + id);
        }
        Spaceship spaceship = optional.get();
        spaceship.setName(dto.name());
        spaceship.setModel(dto.model());
        spaceship.setManufacturer(dto.manufacturer());
        Spaceship updated = repository.save(spaceship);
        return toDTO(updated);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(ERROR_MESSAGE_SPACESHIP_NOT_FOUND_WITH_ID + id);
        }
        repository.deleteById(id);

        try {
            kafkaTemplate.send(TOPIC, MESSAGE_DELETED_SPACESHIP_WITH_ID + id);
        } catch (Exception ex) {
            log.error(ERROR_MESSAGE_SPACESHIP_FAILED_PUSH_KAFKA, id, ex.getMessage(), ex);
        }
    }

    private SpaceshipDTO toDTO(Spaceship spaceship) {
        return new SpaceshipDTO(
                spaceship.getId(),
                spaceship.getName(),
                spaceship.getModel(),
                spaceship.getManufacturer());
    }

    private Spaceship toEntity(SpaceshipDTO dto) {
        return Spaceship.builder()
                .id(dto.id())
                .name(dto.name())
                .model(dto.model())
                .manufacturer(dto.manufacturer())
                .build();
    }
}
