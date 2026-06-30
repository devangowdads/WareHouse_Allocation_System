package com.example.warehouse.controller;

import com.example.warehouse.dto.WarehouseInventoryDto;
import com.example.warehouse.service.WarehouseInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Warehouse Inventory Controller", description = "APIs for managing per-warehouse product stock")
@RestController
@RequestMapping("/api/warehouse-inventory")
public class WarehouseInventoryController {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseInventoryController.class);

    @Autowired
    private WarehouseInventoryService inventoryService;

    @Operation(summary = "Create a warehouse inventory record",
            description = "Sets the available stock quantity for a product in a specific warehouse")
    @ApiResponse(responseCode = "201", description = "Inventory record created successfully")
    @PostMapping
    public ResponseEntity<WarehouseInventoryDto.Response> create(
            @Valid @RequestBody WarehouseInventoryDto.Request request) {
        logger.info("Creating new warehouse inventory record");
        WarehouseInventoryDto.Response response = inventoryService.create(request);
        logger.info("Warehouse inventory created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all warehouse inventory records", description = "Returns every inventory record")
    @GetMapping
    public ResponseEntity<List<WarehouseInventoryDto.Response>> getAll() {
        logger.info("Fetching all warehouse inventory records");
        return ResponseEntity.ok(inventoryService.getAll());
    }
}