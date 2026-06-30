package com.example.warehouse.dto;

import com.example.warehouse.entity.WarehouseInventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class WarehouseInventoryDto {

    @Schema(description = "Payload to create a warehouse inventory record")
    public record Request(
            @NotNull(message = "Warehouse id is required")
            @Schema(example = "1")
            Long warehouseId,

            @NotNull(message = "Product id is required")
            @Schema(example = "1")
            Long productId,

            @NotNull(message = "Available quantity is required")
            @PositiveOrZero(message = "Available quantity cannot be negative")
            @Schema(example = "120")
            Integer availableQuantity
    ) {}

    @Schema(description = "Warehouse inventory details returned by the API")
    public record Response(
            Long id,
            Long warehouseId,
            Long productId,
            Integer availableQuantity,
            Long version
    ) {
        public static Response from(WarehouseInventory wi) {
            return new Response(
                    wi.getId(),
                    wi.getWarehouse().getId(),
                    wi.getProduct().getId(),
                    wi.getAvailableQuantity(),
                    wi.getVersion()
            );
        }
    }
}