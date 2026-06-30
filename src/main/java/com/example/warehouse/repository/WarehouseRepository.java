package com.example.warehouse.repository;

import com.example.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByStatus(Warehouse.WarehouseStatus status);
}
