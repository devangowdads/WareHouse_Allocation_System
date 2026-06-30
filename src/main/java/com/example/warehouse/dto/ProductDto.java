package com.example.warehouse.dto;

import com.example.warehouse.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductDto {

    public record Request(

            @NotBlank(message = "Product name is required")
            @Schema(example = "Wireless Mouse")
            String name,

            @NotBlank(message = "SKU is required")
            @Schema(example = "WM-1001")
            String sku,

            @PositiveOrZero(message = "Total stock cannot be negative")
            @Schema(example = "0")
            Integer totalStock
    ) {}

    public record Response(
            Long id,
            String name,
            String sku,
            Integer totalStock
    ) {
        public static Response from(Product p) {
            return new Response(p.getId(), p.getName(), p.getSku(), p.getTotalStock());
        }
    }
}