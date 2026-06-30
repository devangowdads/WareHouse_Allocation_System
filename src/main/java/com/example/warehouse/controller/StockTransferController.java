package com.example.warehouse.controller;

import com.example.warehouse.dto.StockTransferDto;
import com.example.warehouse.service.StockTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Stock Transfer Controller", description = "APIs for moving stock from one warehouse to another")
@RestController
@RequestMapping("/api/stock-transfers")
public class StockTransferController {

	private static final Logger logger = LoggerFactory.getLogger(StockTransferController.class);

	@Autowired
	private StockTransferService stockTransferService;

	@Operation(summary = "Transfer stock between two warehouses",
			description = "Atomically debits the source warehouse and credits the target warehouse "
					+ "for the given product. Both updates happen in a single transaction")
	@ApiResponse(responseCode = "201", description = "Transfer completed successfully")
	@ApiResponse(responseCode = "409", description = "Insufficient stock at the source warehouse")
	@PostMapping
	public ResponseEntity<StockTransferDto.Response> transfer(@Valid @RequestBody StockTransferDto.Request request) {
		logger.info("Transferring stock:");
		StockTransferDto.Response response = stockTransferService.transfer(request);
		logger.info("Stock transferred successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	@Operation(summary = "List all stock transfers", description = "Returns every stock transfer record")
	@GetMapping
	public ResponseEntity<List<StockTransferDto.Response>> getAll() {
	    logger.info("Fetching all stock transfers");
	    return ResponseEntity.ok(stockTransferService.getAll());
	}
}