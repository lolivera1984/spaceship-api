package com.spacefleet.spaceshipapi.service;

import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.exception.NotFoundException;
import com.spacefleet.spaceshipapi.model.Spaceship;
import com.spacefleet.spaceshipapi.repository.SpaceshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpaceshipService {

    private final SpaceshipRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "spaceship.events";

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
                .collect(Collectors.toList());
    }

    public Page<SpaceshipDTO> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable)
                .map(this::toDTO);
    }

    @Cacheable("spaceshipById")
    public SpaceshipDTO findById(String id) {
        Spaceship ship = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Spaceship not found with id: " + id));
        return toDTO(ship);
    }

    public List<SpaceshipDTO> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SpaceshipDTO create(SpaceshipDTO dto) {
        Spaceship entity = toEntity(dto);
        Spaceship saved = repository.save(entity);
        //kafkaTemplate.send(TOPIC, "Created spaceship: " + saved.getId());
        return toDTO(saved);
    }

    public SpaceshipDTO update(String id, SpaceshipDTO dto) {
        Optional<Spaceship> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Spaceship not found with id: " + id);
        }
        Spaceship spaceship = optional.get();
        spaceship.setName(dto.getName());
        spaceship.setModel(dto.getModel());
        spaceship.setManufacturer(dto.getManufacturer());
        Spaceship updated = repository.save(spaceship);
        //kafkaTemplate.send(TOPIC, "Updated spaceship: " + updated.getId());
        return toDTO(updated);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Spaceship not found with id: " + id);
        }
        repository.deleteById(id);
        kafkaTemplate.send(TOPIC, "Deleted spaceship: " + id);
    }

    private SpaceshipDTO toDTO(Spaceship spaceship) {
        return SpaceshipDTO.builder()
                .id(spaceship.getId())
                .name(spaceship.getName())
                .model(spaceship.getModel())
                .manufacturer(spaceship.getManufacturer())
                .build();
    }

    private Spaceship toEntity(SpaceshipDTO dto) {
        return Spaceship.builder()
                .id(dto.getId())
                .name(dto.getName())
                .model(dto.getModel())
                .manufacturer(dto.getManufacturer())
                .build();
    }
}
