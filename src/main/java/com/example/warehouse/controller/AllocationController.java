package com.example.warehouse.controller;

import com.example.warehouse.dto.AllocationDto;
import com.example.warehouse.service.AllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Allocation Controller", description = "APIs for allocating stock to warehouses and searching allocation history")
@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

	private static final Logger logger = LoggerFactory.getLogger(AllocationController.class);

	@Autowired
	private AllocationService allocationService;

	@Operation(summary = "Allocate stock to a specific warehouse",
			description = "Caller chooses the exact warehouse. Validates capacity, available stock, "
					+ "and uses optimistic locking to safely handle concurrent requests")
	@ApiResponse(responseCode = "201", description = "Allocation completed successfully")
	@ApiResponse(responseCode = "409", description = "Insufficient stock, capacity exceeded, or concurrent modification conflict")
	@PostMapping("/manual")
	public ResponseEntity<AllocationDto.Response> allocateManual(@Valid @RequestBody AllocationDto.ManualRequest request) {
		logger.info("Allocating stock manually:");
		AllocationDto.Response response = allocationService.allocateManual(request);
		logger.info("Stock allocated successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Automatically select a warehouse and allocate stock",
			description = "System picks the active warehouse with the most available stock that "
					+ "can satisfy the requested quantity")
	@ApiResponse(responseCode = "201", description = "Allocation completed successfully")
	@ApiResponse(responseCode = "409", description = "No warehouse has sufficient stock")
	@PostMapping("/auto")
	public ResponseEntity<AllocationDto.Response> allocateAuto(@Valid @RequestBody AllocationDto.AutoRequest request) {
		logger.info("Allocating stock automatically:");
		AllocationDto.Response response = allocationService.allocateAuto(request);
		logger.info("Stock allocated successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Search allocation history",
			description = "Filter by product, warehouse, and date range. Supports pagination and sorting "
					+ "(e.g. ?page=0&size=20&sort=allocatedAt,desc)")
	@GetMapping
	public ResponseEntity<Page<AllocationDto.Response>> search(
			@Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,
			@Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,
			@Parameter(description = "Start of date range (ISO-8601)")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@Parameter(description = "End of date range (ISO-8601)")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,Pageable pageable) {
		logger.info("Searching allocation history");
		return ResponseEntity.ok(allocationService.search(productId, warehouseId, from, to, pageable));
	}
}