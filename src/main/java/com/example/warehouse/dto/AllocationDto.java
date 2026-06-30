package com.example.warehouse.dto;

import com.example.warehouse.entity.Allocation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class AllocationDto {

    @Schema(description = "Allocate to a specific warehouse")
    public record ManualRequest(

            @NotNull(message = "Product ID is required")
            Long productId,

            @NotNull(message = "Warehouse ID is required")
            Long warehouseId,

            @Positive(message = "Quantity must be greater than zero")
            Integer quantity
    ) {}

    @Schema(description = "Let the system automatically pick the best warehouse")
    public record AutoRequest(

            @NotNull(message = "Product ID is required")
            Long productId,

            @Positive(message = "Quantity must be greater than zero")
            Integer quantity
    ) {}

    public record Response(
            Long id,
            Long productId,
            String productName,
            Long warehouseId,
            String warehouseName,
            Integer quantity,
            Allocation.AllocationStatus status,
            LocalDateTime allocatedAt
    ) {
        public static Response from(Allocation a) {
            return new Response(
                    a.getId(),
                    a.getProduct().getId(),
                    a.getProduct().getName(),
                    a.getWarehouse().getId(),
                    a.getWarehouse().getName(),
                    a.getQuantity(),
                    a.getStatus(),
                    a.getAllocatedAt()
            );
        }
    }
}