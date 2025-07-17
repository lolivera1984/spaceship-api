package com.spacefleet.spaceshipapi.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        String id,

        @NotBlank(message = "user Name is required")
        String name,

        String username,

        String password
) {}
