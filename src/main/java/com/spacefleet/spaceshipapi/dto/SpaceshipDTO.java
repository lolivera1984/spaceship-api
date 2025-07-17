package com.spacefleet.spaceshipapi.dto;

import jakarta.validation.constraints.NotBlank;

public record SpaceshipDTO(
        String id,

        @NotBlank(message = "Spaceship Name is required")
        String name,

        String model,

        String manufacturer
) {}
