package com.example.warehouse.controller;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Controller", description = "APIs for managing products available for allocation")
@RestController
@RequestMapping("/api/products")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Operation(summary = "Create a product", description = "Registers a new product")
	@PostMapping
	public ResponseEntity<ProductDto.Response> create(@Valid @RequestBody ProductDto.Request request) {
		logger.info("Creating new product:");
		ProductDto.Response response = productService.create(request);
		logger.info("Product created successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get a product by ID", description = "Fetches a single product")
	@GetMapping("/{id}")
	public ResponseEntity<ProductDto.Response> getById(@PathVariable Long id) {
		logger.info("Fetching product");
		return ResponseEntity.ok(productService.getById(id));
	}

	@Operation(summary = "List all products", description = "Returns every product")
	@GetMapping
	public ResponseEntity<List<ProductDto.Response>> getAll() {
		logger.info("Fetching all products");
		return ResponseEntity.ok(productService.getAll());
	}
}