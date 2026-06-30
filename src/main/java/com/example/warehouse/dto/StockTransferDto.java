package com.example.warehouse.dto;

import com.example.warehouse.entity.StockTransfer;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class StockTransferDto {

    public record Request(

            @NotNull(message = "Source warehouse ID is required")
            Long sourceWarehouseId,

            @NotNull(message = "Target warehouse ID is required")
            Long targetWarehouseId,

            @NotNull(message = "Product ID is required")
            Long productId,

            @Positive(message = "Quantity must be greater than zero")
            Integer quantity
    ) {
        @AssertTrue(message = "Source and target warehouse must be different")
        public boolean isDifferentWarehouses() {
            return sourceWarehouseId == null || targetWarehouseId == null
                    || !sourceWarehouseId.equals(targetWarehouseId);
        }
    }

    public record Response(
            Long id,
            Long sourceWarehouseId,
            String sourceWarehouseName,
            Long targetWarehouseId,
            String targetWarehouseName,
            Long productId,
            Integer quantity,
            LocalDateTime transferDate
    ) {
        public static Response from(StockTransfer t) {
            return new Response(
                    t.getId(),
                    t.getSourceWarehouse().getId(),
                    t.getSourceWarehouse().getName(),
                    t.getTargetWarehouse().getId(),
                    t.getTargetWarehouse().getName(),
                    t.getProduct().getId(),
                    t.getQuantity(),
                    t.getTransferDate()
            );
        }
    }
}