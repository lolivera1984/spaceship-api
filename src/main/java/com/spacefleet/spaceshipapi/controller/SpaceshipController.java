package com.spacefleet.spaceshipapi.controller;

import com.spacefleet.spaceshipapi.dto.PaginatedResponseDTO;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.service.SpaceshipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spaceships")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class SpaceshipController {

    private final SpaceshipService service;

    private static final String DEFAULT_PAGE_VALUE = "0";
    private static final String DEFAULT_PAGE_SIZE_VALUE = "10";


    @Autowired
    public SpaceshipController(SpaceshipService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<SpaceshipDTO>> getAll(
            @RequestParam(defaultValue = DEFAULT_PAGE_VALUE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_VALUE) int size) {
        return ResponseEntity.ok(service.findAllPaginated(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceshipDTO> getById(@PathVariable @NotBlank @Size(min = 1, max = 50) String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<SpaceshipDTO>> searchByName(
            @RequestParam @NotBlank @Size(max = 100) String name,
            @RequestParam(defaultValue = DEFAULT_PAGE_VALUE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_VALUE) @Min(1) int size) {
        return ResponseEntity.ok(service.findByNamePaginated(name, page, size));
    }

    @PostMapping
    public ResponseEntity<SpaceshipDTO> create(@RequestBody @Valid SpaceshipDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceshipDTO> update(
            @PathVariable String id,
            @RequestBody @Valid SpaceshipDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotBlank String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
