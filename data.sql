-- =========================================================
-- data.sql
-- Sample/seed data for the Warehouse Allocation System
-- Assumes schema already created (see schema.sql)
-- =========================================================

-- ---------------------------------------------------------
-- WAREHOUSE
-- ---------------------------------------------------------
INSERT INTO warehouse (id, name, location, capacity, status, created_at) VALUES
(1, 'Bengaluru Central Warehouse', 'Bengaluru, Karnataka', 10000, 'ACTIVE', '2026-01-05 09:00:00'),
(2, 'Mumbai West Warehouse', 'Mumbai, Maharashtra', 8000, 'ACTIVE', '2026-01-10 09:00:00'),
(3, 'Delhi North Warehouse', 'Delhi, NCR', 6000, 'ACTIVE', '2026-01-15 09:00:00'),
(4, 'Chennai South Warehouse', 'Chennai, Tamil Nadu', 5000, 'INACTIVE', '2026-02-01 09:00:00');

-- ---------------------------------------------------------
-- PRODUCT
-- ---------------------------------------------------------
INSERT INTO product (id, name, sku, total_stock) VALUES
(1, 'Wireless Mouse', 'SKU-WM-1001', 1500),
(2, 'Mechanical Keyboard', 'SKU-MK-1002', 800),
(3, '27-inch Monitor', 'SKU-MN-1003', 400),
(4, 'USB-C Docking Station', 'SKU-DS-1004', 600),
(5, 'Noise Cancelling Headset', 'SKU-HS-1005', 1000);

-- ---------------------------------------------------------
-- WAREHOUSE_INVENTORY
-- ---------------------------------------------------------
INSERT INTO warehouse_inventory (id, warehouse_id, product_id, available_quantity, version) VALUES
(1, 1, 1, 600, 0),
(2, 1, 2, 300, 0),
(3, 1, 3, 150, 0),
(4, 2, 1, 500, 0),
(5, 2, 4, 250, 0),
(6, 2, 5, 400, 0),
(7, 3, 2, 200, 0),
(8, 3, 3, 100, 0),
(9, 3, 5, 300, 0),
(10, 4, 1, 400, 0);

-- ---------------------------------------------------------
-- ALLOCATION
-- ---------------------------------------------------------
INSERT INTO allocation (id, product_id, warehouse_id, quantity, status, allocated_at) VALUES
(1, 1, 1, 50, 'ALLOCATED', '2026-03-01 10:15:00'),
(2, 2, 1, 20, 'ALLOCATED', '2026-03-02 11:30:00'),
(3, 1, 2, 30, 'ALLOCATED', '2026-03-05 14:00:00'),
(4, 5, 2, 100, 'ALLOCATED', '2026-04-01 09:45:00'),
(5, 3, 3, 10, 'CANCELLED', '2026-04-10 16:20:00');

-- ---------------------------------------------------------
-- STOCK_TRANSFER
-- ---------------------------------------------------------
INSERT INTO stock_transfer (id, source_warehouse_id, target_warehouse_id, product_id, quantity, transfer_date) VALUES
(1, 1, 3, 2, 50, '2026-03-10 12:00:00'),
(2, 2, 1, 5, 75, '2026-04-15 13:30:00'),
(3, 1, 4, 1, 100, '2026-05-02 10:00:00');
