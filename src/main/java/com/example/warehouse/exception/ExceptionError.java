package com.example.warehouse.exception;

public class ExceptionError {

    public static final String PRODUCT_NOT_FOUND = "Product not found with id: ";
    public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found with id: ";
    public static final String WAREHOUSE_INVENTORY_NOT_FOUND = "No inventory record for product ";
    public static final String ALLOCATION_NOT_FOUND = "Allocation not found with id: ";

    public static final String INSUFFICIENT_STOCK = "Insufficient stock: requested ";
    public static final String CAPACITY_EXCEEDED = "Requested quantity exceeds warehouse capacity. Requested: ";
    public static final String WAREHOUSE_INACTIVE = "Warehouse is not active: ";
    public static final String NO_AVAILABLE_WAREHOUSE =
            "No active warehouse has enough stock for product ";
    public static final String CONCURRENT_ALLOCATION =
            "This inventory record was just updated by another request. Please retry.";
    public static final String SAME_SOURCE_AND_TARGET_WAREHOUSE = "Source and target warehouse must be different";
    public static final String INVENTORY_ALREADY_EXISTS =
            "Inventory record already exists for product ";
    private ExceptionError() {
    }
}