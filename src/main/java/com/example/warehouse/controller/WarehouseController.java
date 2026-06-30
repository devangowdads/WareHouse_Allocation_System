package com.example.warehouse.controller;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.service.WarehouseService;
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

@Tag(name = "Warehouse Controller", description = "APIs for managing warehouses")
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

	private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

	@Autowired
	private WarehouseService warehouseService;

	@Operation(summary = "Create a warehouse", description = "Registers a new warehouse with a name, location, and capacity")
	@ApiResponse(responseCode = "201", description = "Warehouse created successfully")
	@PostMapping
	public ResponseEntity<WarehouseDto.Response> create(@Valid @RequestBody WarehouseDto.Request request) {
		logger.info("Creating new warehouse:");
		WarehouseDto.Response response = warehouseService.create(request);
		logger.info("Warehouse created successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get a warehouse by ID", description = "Fetches a single warehouse")
	@GetMapping("/{id}")
	public ResponseEntity<WarehouseDto.Response> getById(@PathVariable Long id) {
		logger.info("Fetching warehouse");
		return ResponseEntity.ok(warehouseService.getById(id));
	}

	@Operation(summary = "List all warehouses", description = "Returns every warehouse")
	@GetMapping
	public ResponseEntity<List<WarehouseDto.Response>> getAll() {
		logger.info("Fetching all warehouses");
		return ResponseEntity.ok(warehouseService.getAll());
	}

	@Operation(summary = "Update a warehouse", description = "Updates an existing warehouse's details by ID")
	@PutMapping("/{id}")
	public ResponseEntity<WarehouseDto.Response> update(@PathVariable Long id,
			@Valid @RequestBody WarehouseDto.Request request) {
		logger.info("Updating warehouse");
		WarehouseDto.Response response = warehouseService.update(id, request);
		logger.info("Warehouse updated successfully");
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Deactivate a warehouse (soft delete)",
			description = "Flips the warehouse status to INACTIVE instead of deleting the row, preserving allocation history")
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<Void> deactivate(@PathVariable Long id) {
		logger.info("Deactivating warehouse");
		warehouseService.deactivate(id);
		logger.info("Warehouse deactivated successfully");
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Activate a warehouse", description = "Re-activates a previously deactivated warehouse")
	@PatchMapping("/{id}/activate")
	public ResponseEntity<Void> activate(@PathVariable Long id) {
		logger.info("Activating warehouse");
		warehouseService.activate(id);
		logger.info("Warehouse activated successfully");
		return ResponseEntity.noContent().build();
	}
}