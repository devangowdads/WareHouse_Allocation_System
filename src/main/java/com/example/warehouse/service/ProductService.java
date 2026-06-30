package com.example.warehouse.service;

import com.example.warehouse.dto.ProductDto;
import com.example.warehouse.entity.Product;
import com.example.warehouse.exception.ExceptionError;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductDto.Response create(ProductDto.Request req) {
        logger.info("Creating new product:");
        Product p = new Product();
        p.setName(req.name());
        p.setSku(req.sku());
        p.setTotalStock(req.totalStock() == null ? 0 : req.totalStock());
        Product saved = productRepository.save(p);
        logger.info("Product created successfully: id={} sku={}", saved.getId(), saved.getSku());
        return ProductDto.Response.from(saved);
    }

    public ProductDto.Response getById(Long id) {
        logger.info("Fetching product: id={}", id);
        return ProductDto.Response.from(findEntity(id));
    }

    public List<ProductDto.Response> getAll() {
        logger.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(ProductDto.Response::from)
                .toList();
    }

    public Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionError.PRODUCT_NOT_FOUND + id));
    }
}