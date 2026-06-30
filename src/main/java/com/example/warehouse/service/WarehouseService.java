package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseDto;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.exception.ExceptionError;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public WarehouseDto.Response create(WarehouseDto.Request req) {
        logger.info("Creating new warehouse:");
        Warehouse w = new Warehouse();
        w.setName(req.name());
        w.setLocation(req.location());
        w.setCapacity(req.capacity());
        w.setStatus(Warehouse.WarehouseStatus.ACTIVE);
        Warehouse saved = warehouseRepository.save(w);
        logger.info("Warehouse created successfully: id={} name={}", saved.getId(), saved.getName());
        return WarehouseDto.Response.from(saved);
    }

    public WarehouseDto.Response getById(Long id) {
        logger.info("Fetching warehouse: id={}", id);
        return WarehouseDto.Response.from(findEntity(id));
    }

    public List<WarehouseDto.Response> getAll() {
        logger.info("Fetching all warehouses");
        return warehouseRepository.findAll().stream()
                .map(WarehouseDto.Response::from)
                .toList();
    }

    @Transactional
    public WarehouseDto.Response update(Long id, WarehouseDto.Request req) {
        logger.info("Updating warehouse: id={}", id);
        Warehouse w = findEntity(id);
        w.setName(req.name());
        w.setLocation(req.location());
        w.setCapacity(req.capacity());
        logger.info("Warehouse updated successfully: id={}", id);
        return WarehouseDto.Response.from(w);
    }

    @Transactional
    public void deactivate(Long id) {
        logger.info("Deactivating warehouse: id={}", id);
        Warehouse w = findEntity(id);
        w.setStatus(Warehouse.WarehouseStatus.INACTIVE);
        logger.info("Warehouse deactivated successfully: id={}", id);
    }

    @Transactional
    public void activate(Long id) {
        logger.info("Activating warehouse: id={}", id);
        Warehouse w = findEntity(id);
        w.setStatus(Warehouse.WarehouseStatus.ACTIVE);
        logger.info("Warehouse activated successfully: id={}", id);
    }

    public Warehouse findEntity(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionError.WAREHOUSE_NOT_FOUND + id));
    }
}