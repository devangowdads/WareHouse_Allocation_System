package com.example.warehouse.service;

import com.example.warehouse.dto.StockTransferDto;
import com.example.warehouse.entity.Product;
import com.example.warehouse.entity.StockTransfer;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.entity.WarehouseInventory;
import com.example.warehouse.exception.ExceptionError;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.StockTransferRepository;
import com.example.warehouse.repository.WarehouseInventoryRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockTransferService {

    private static final Logger logger = LoggerFactory.getLogger(StockTransferService.class);

    private final StockTransferRepository stockTransferRepository;
    private final WarehouseInventoryRepository inventoryRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public StockTransferService(StockTransferRepository stockTransferRepository,
                                 WarehouseInventoryRepository inventoryRepository,
                                 ProductService productService,
                                 WarehouseService warehouseService) {
        this.stockTransferRepository = stockTransferRepository;
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    /**
     * Moves stock from one warehouse to another. Both inventory updates happen
     * inside a single transaction — if either fails, both roll back, so stock
     * is never debited from the source without being credited to the target.
     */
    @Transactional
    public StockTransferDto.Response transfer(StockTransferDto.Request req) {
        logger.info("Transferring stock: productId={} from={} to={} quantity={}",
                req.productId(), req.sourceWarehouseId(), req.targetWarehouseId(), req.quantity());

        Product product = productService.findEntity(req.productId());
        Warehouse source = warehouseService.findEntity(req.sourceWarehouseId());
        Warehouse target = warehouseService.findEntity(req.targetWarehouseId());

        WarehouseInventory sourceInventory = inventoryRepository
                .findByWarehouseIdAndProductId(source.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionError.WAREHOUSE_INVENTORY_NOT_FOUND + product.getId()
                                + " in warehouse " + source.getId()));

        if (sourceInventory.getAvailableQuantity() < req.quantity()) {
            logger.warn("Insufficient stock at source warehouse {}: requested={} available={}",
                    source.getId(), req.quantity(), sourceInventory.getAvailableQuantity());
            throw new ResourceNotFoundException(
                    ExceptionError.INSUFFICIENT_STOCK + req.quantity()
                            + " but only " + sourceInventory.getAvailableQuantity() + " available");
        }

        WarehouseInventory targetInventory = inventoryRepository
                .findByWarehouseIdAndProductId(target.getId(), product.getId())
                .orElseGet(() -> {
                    WarehouseInventory wi = new WarehouseInventory();
                    wi.setWarehouse(target);
                    wi.setProduct(product);
                    wi.setAvailableQuantity(0);
                    return wi;
                });

        sourceInventory.setAvailableQuantity(sourceInventory.getAvailableQuantity() - req.quantity());
        targetInventory.setAvailableQuantity(targetInventory.getAvailableQuantity() + req.quantity());
        inventoryRepository.save(targetInventory);

        StockTransfer transfer = new StockTransfer();
        transfer.setSourceWarehouse(source);
        transfer.setTargetWarehouse(target);
        transfer.setProduct(product);
        transfer.setQuantity(req.quantity());
        StockTransfer saved = stockTransferRepository.save(transfer);

        logger.info("Stock transferred successfully: transferId={} {} units of product {} from warehouse {} to warehouse {}",
                saved.getId(), req.quantity(), product.getId(), source.getId(), target.getId());

        return StockTransferDto.Response.from(saved);
    }
    public List<StockTransferDto.Response> getAll() {
        logger.info("Fetching all stock transfers");
        return stockTransferRepository.findAll().stream()
                .map(StockTransferDto.Response::from)
                .toList();
    }
}