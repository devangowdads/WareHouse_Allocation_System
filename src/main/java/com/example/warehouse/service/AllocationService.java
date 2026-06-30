package com.example.warehouse.service;

import com.example.warehouse.dto.AllocationDto;
import com.example.warehouse.entity.Allocation;
import com.example.warehouse.entity.Product;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.entity.WarehouseInventory;
import com.example.warehouse.exception.ExceptionError;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.AllocationRepository;
import com.example.warehouse.repository.WarehouseInventoryRepository;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the allocation workflow from the spec:
 *   1. Receive request (product id, quantity)
 *   2. Validate product existence
 *   3. Check warehouse capacity / available stock
 *   4. Lock inventory record (optimistic locking)
 *   5. Allocate stock and update inventory
 *   6. Create allocation record
 *   7. Return allocation confirmation
 *
 * Every business error is thrown as ResourceNotFoundException, with the
 * message built by concatenating an ExceptionError constant with the
 * relevant values — no dedicated exception class per case.
 */
@Service
public class AllocationService {

    private static final Logger logger = LoggerFactory.getLogger(AllocationService.class);

    private final AllocationRepository allocationRepository;
    private final WarehouseInventoryRepository inventoryRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public AllocationService(AllocationRepository allocationRepository,
                              WarehouseInventoryRepository inventoryRepository,
                              ProductService productService,
                              WarehouseService warehouseService) {
        this.allocationRepository = allocationRepository;
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    /** Allocation to a warehouse explicitly chosen by the caller. */
    @Transactional
    public AllocationDto.Response allocateManual(AllocationDto.ManualRequest req) {
        logger.info("Allocating stock manually: productId={} warehouseId={} quantity={}",
                req.productId(), req.warehouseId(), req.quantity());

        Product product = productService.findEntity(req.productId());
        Warehouse warehouse = warehouseService.findEntity(req.warehouseId());

        WarehouseInventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionError.WAREHOUSE_INVENTORY_NOT_FOUND + product.getId()
                                + " in warehouse " + warehouse.getId()));

        AllocationDto.Response response = allocate(product, warehouse, inventory, req.quantity());
        logger.info("Manual allocation completed successfully: allocationId={}", response.id());
        return response;
    }

    /**
     * Automatic warehouse selection: picks the ACTIVE warehouse holding the
     * product with the highest available stock that can satisfy the request
     * (configurable rule — currently "most available stock").
     */
    @Transactional
    public AllocationDto.Response allocateAuto(AllocationDto.AutoRequest req) {
        logger.info("Allocating stock automatically: productId={} quantity={}", req.productId(), req.quantity());

        Product product = productService.findEntity(req.productId());

        List<WarehouseInventory> candidates =
                inventoryRepository.findCandidateWarehouses(
                        product.getId(), req.quantity(), Warehouse.WarehouseStatus.ACTIVE);
        if (candidates.isEmpty()) {
            logger.warn("No active warehouse has enough stock for productId={} quantity={}",
                    req.productId(), req.quantity());
            throw new ResourceNotFoundException(
                    ExceptionError.NO_AVAILABLE_WAREHOUSE + product.getId()
                            + " with quantity " + req.quantity());
        }

        // Candidates are already sorted by available quantity DESC (most-stocked first)
        WarehouseInventory best = candidates.get(0);
        AllocationDto.Response response = allocate(product, best.getWarehouse(), best, req.quantity());
        logger.info("Auto allocation completed successfully: allocationId={} warehouseId={}",
                response.id(), response.warehouseId());
        return response;
    }

    private AllocationDto.Response allocate(Product product, Warehouse warehouse,
                                              WarehouseInventory inventory, Integer quantity) {

        if (warehouse.getStatus() != Warehouse.WarehouseStatus.ACTIVE) {
            logger.warn("Warehouse {} is not active", warehouse.getId());
            throw new ResourceNotFoundException(ExceptionError.WAREHOUSE_INACTIVE + warehouse.getId());
        }

        if (inventory.getAvailableQuantity() < quantity) {
            logger.warn("Insufficient stock in warehouse {}: requested={} available={}",
                    warehouse.getId(), quantity, inventory.getAvailableQuantity());
            throw new ResourceNotFoundException(
                    ExceptionError.INSUFFICIENT_STOCK + quantity
                            + " but only " + inventory.getAvailableQuantity() + " available");
        }

        // Capacity check: total allocated across the warehouse must never exceed its capacity.
        // (Simplified here as a per-inventory-row capacity guard; extend with an aggregate
        // query across all products in the warehouse for full enterprise accuracy.)
        if (quantity > warehouse.getCapacity()) {
            logger.warn("Requested quantity {} exceeds capacity {} for warehouse {}",
                    quantity, warehouse.getCapacity(), warehouse.getId());
            throw new ResourceNotFoundException(
                    ExceptionError.CAPACITY_EXCEEDED + quantity
                            + " (warehouse capacity: " + warehouse.getCapacity() + ")");
        }

        try {
            // Step 4: optimistic lock — re-read with a forced version check right before mutating.
            WarehouseInventory locked = inventoryRepository.findByIdForUpdate(inventory.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ExceptionError.WAREHOUSE_INVENTORY_NOT_FOUND + product.getId()
                                    + " in warehouse " + warehouse.getId()));

            if (locked.getAvailableQuantity() < quantity) {
                logger.warn("Stock changed concurrently for warehouse {}: requested={} nowAvailable={}",
                        warehouse.getId(), quantity, locked.getAvailableQuantity());
                throw new ResourceNotFoundException(
                        ExceptionError.INSUFFICIENT_STOCK + quantity
                                + " but only " + locked.getAvailableQuantity() + " available");
            }

            // Step 5: allocate stock and update inventory
            locked.setAvailableQuantity(locked.getAvailableQuantity() - quantity);

            // Step 6: create allocation record (the audit trail)
            Allocation allocation = new Allocation();
            allocation.setProduct(product);
            allocation.setWarehouse(warehouse);
            allocation.setQuantity(quantity);
            allocation.setStatus(Allocation.AllocationStatus.SUCCESS);
            Allocation saved = allocationRepository.save(allocation);

            logger.info("Allocated {} units of product {} from warehouse {} (allocationId={})",
                    quantity, product.getId(), warehouse.getId(), saved.getId());

            // Step 7: return confirmation
            return AllocationDto.Response.from(saved);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException ex) {
            logger.warn("Optimistic lock conflict allocating product {} from warehouse {}",
                    product.getId(), warehouse.getId());
            throw new ResourceNotFoundException(ExceptionError.CONCURRENT_ALLOCATION);
        }
    }

    /** Search allocations by product, warehouse, and date range with pagination/sorting. */
    public Page<AllocationDto.Response> search(Long productId, Long warehouseId,
                                                  LocalDateTime from, LocalDateTime to,
                                                  Pageable pageable) {
        logger.info("Searching allocation history: productId={} warehouseId={} from={} to={}",
                productId, warehouseId, from, to);

        List<Specification<Allocation>> conditions = new ArrayList<>();

        if (productId != null) {
            conditions.add((root, query, cb) -> cb.equal(root.get("product").get("id"), productId));
        }
        if (warehouseId != null) {
            conditions.add((root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId));
        }
        if (from != null) {
            conditions.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("allocatedAt"), from));
        }
        if (to != null) {
            conditions.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("allocatedAt"), to));
        }

        Specification<Allocation> spec = Specification.allOf(conditions);

        return allocationRepository.findAll(spec, pageable)
                .map(AllocationDto.Response::from);
    }
}