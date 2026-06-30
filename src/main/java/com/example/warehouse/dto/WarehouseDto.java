package com.example.warehouse.dto;

import com.example.warehouse.entity.Warehouse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class WarehouseDto {

    @Schema(description = "Payload to create or update a warehouse")
    public record Request(
            @NotBlank(message = "Warehouse name is required")
            @Schema(example = "Bengaluru Central Warehouse")
            String name,

            @NotBlank(message = "Location is required")
            @Schema(example = "Bengaluru, Karnataka")
            String location,

            @Positive(message = "Capacity must be greater than zero")
            @Schema(example = "10000")
            Integer capacity
    ) {}

    @Schema(description = "Warehouse details returned by the API")
    public record Response(
            Long id,
            String name,
            String location,
            Integer capacity,
            Warehouse.WarehouseStatus status,
            LocalDateTime createdAt
    ) {
        public static Response from(Warehouse w) {
            return new Response(w.getId(), w.getName(), w.getLocation(),
                    w.getCapacity(), w.getStatus(), w.getCreatedAt());
        }
    }
}