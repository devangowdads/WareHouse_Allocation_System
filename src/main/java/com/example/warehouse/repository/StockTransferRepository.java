package com.example.warehouse.repository;

import com.example.warehouse.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
}