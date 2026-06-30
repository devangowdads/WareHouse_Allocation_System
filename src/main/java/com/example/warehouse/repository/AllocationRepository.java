package com.example.warehouse.repository;

import com.example.warehouse.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Extends JpaSpecificationExecutor so the service layer can build dynamic
 * filters (product, warehouse, date range) combined with Pageable for
 * pagination and sorting, without writing a dozen derived query methods.
 */
public interface AllocationRepository extends JpaRepository<Allocation, Long>,
        JpaSpecificationExecutor<Allocation> {
}