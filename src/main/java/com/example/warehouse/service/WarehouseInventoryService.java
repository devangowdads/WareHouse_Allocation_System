package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseInventoryDto;
import com.example.warehouse.entity.Product;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.entity.WarehouseInventory;
import com.example.warehouse.exception.ExceptionError;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.WarehouseInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseInventoryService.class);

    private final WarehouseInventoryRepository inventoryRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public WarehouseInventoryService(WarehouseInventoryRepository inventoryRepository,
                                      ProductService productService,
                                      WarehouseService warehouseService) {
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    @Transactional
    public WarehouseInventoryDto.Response create(WarehouseInventoryDto.Request req) {
        logger.info("Creating warehouse inventory: warehouseId={} productId={} quantity={}",
                req.warehouseId(), req.productId(), req.availableQuantity());

        Warehouse warehouse = warehouseService.findEntity(req.warehouseId());
        Product product = productService.findEntity(req.productId());

        inventoryRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .ifPresent(existing -> {
                    throw new ResourceNotFoundException(
                            ExceptionError.INVENTORY_ALREADY_EXISTS + product.getId()
                                    + " in warehouse " + warehouse.getId());
                });

        WarehouseInventory inventory = new WarehouseInventory();
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);
        inventory.setAvailableQuantity(req.availableQuantity());

        WarehouseInventory saved = inventoryRepository.save(inventory);
        logger.info("Warehouse inventory created successfully: id={}", saved.getId());

        return WarehouseInventoryDto.Response.from(saved);
    }

    public List<WarehouseInventoryDto.Response> getAll() {
        logger.info("Fetching all warehouse inventory records");
        return inventoryRepository.findAll().stream()
                .map(WarehouseInventoryDto.Response::from)
                .toList();
    }
}