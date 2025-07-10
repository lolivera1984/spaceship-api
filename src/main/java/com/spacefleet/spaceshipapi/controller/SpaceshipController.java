package com.spacefleet.spaceshipapi.controller;

import com.spacefleet.spaceshipapi.dto.PaginatedResponseDTO;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.service.SpaceshipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spaceships")
@SecurityRequirement(name = "bearerAuth")
public class SpaceshipController {

    private final SpaceshipService service;

    @Autowired
    public SpaceshipController(SpaceshipService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<SpaceshipDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        //Page<SpaceshipDTO> paginatedSpaceships = service.findAllPaginated(page, size);
        //return ResponseEntity.ok(paginatedSpaceships);

        Page<SpaceshipDTO> pageResult = service.findAllPaginated(page, size);

        PaginatedResponseDTO<SpaceshipDTO> response = new PaginatedResponseDTO<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceshipDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SpaceshipDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @PostMapping
    public ResponseEntity<SpaceshipDTO> create(@RequestBody SpaceshipDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceshipDTO> update(@PathVariable String id, @RequestBody SpaceshipDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
