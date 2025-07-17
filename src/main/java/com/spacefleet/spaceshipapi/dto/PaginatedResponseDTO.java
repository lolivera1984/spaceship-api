package com.spacefleet.spaceshipapi.dto;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
