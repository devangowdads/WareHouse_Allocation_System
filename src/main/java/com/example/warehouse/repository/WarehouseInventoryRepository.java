package com.example.warehouse.repository;

import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {

    Optional<WarehouseInventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    /**
     * Used by automatic warehouse selection: finds every inventory row for a
     * given product that has at least the requested quantity available,
     * ordered by available quantity descending (most-stocked warehouse first).
     *
     * The warehouse status is passed in as a bound parameter rather than
     * embedded as an enum literal in the JPQL string — embedding fully
     * qualified nested-enum literals (e.g. Outer.Inner.VALUE) in JPQL is
     * fragile and Hibernate-version-dependent; binding the value avoids
     * that entirely.
     */
    @Query("""
            SELECT wi FROM WarehouseInventory wi
            WHERE wi.product.id = :productId
              AND wi.availableQuantity >= :quantity
              AND wi.warehouse.status = :status
            ORDER BY wi.availableQuantity DESC
            """)
    List<WarehouseInventory> findCandidateWarehouses(@Param("productId") Long productId,
                                                        @Param("quantity") Integer quantity,
                                                        @Param("status") Warehouse.WarehouseStatus status);

    /**
     * Re-reads the row with an explicit OPTIMISTIC lock right before the update,
     * so Hibernate checks the @Version column and throws if it changed since
     * it was first read (i.e. another concurrent request already modified it).
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT wi FROM WarehouseInventory wi WHERE wi.id = :id")
    Optional<WarehouseInventory> findByIdForUpdate(@Param("id") Long id);
}