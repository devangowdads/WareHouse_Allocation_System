# Warehouse Allocation System – Architecture & Module Flow

## 1. Layered Architecture

The system follows a clean layered architecture, where each layer only communicates with the layer directly below it.

```
Client (Postman / UI)
        |
        v
Controller Layer        <----  API docs (Swagger / OpenAPI)
  REST APIs, request validation
        |
        v
Service Layer
  Allocation, transfer, warehouse business logic
        |
        v
Repository Layer
  JPA repositories, optimistic locking
        |
        v
Database (RDBMS)
```

An **Audit / Exception module** sits alongside the Service and Repository layers. It is responsible for:
- Logging every allocation operation (create, cancel, transfer)
- A global exception handler that converts validation and locking failures into consistent error responses

### Layer Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Exposes REST endpoints (Warehouse, Product, Allocation, Stock Transfer), validates incoming requests, delegates to the service layer |
| Service | Implements business rules — capacity validation, stock checks, automatic warehouse selection, allocation/transfer logic |
| Repository | JPA/Hibernate repositories; applies optimistic locking on `Warehouse_Inventory` via the `version` field |
| Database | Persists Warehouse, Product, Warehouse_Inventory, Allocation, Stock_Transfer tables |
| Audit / Exception | Cross-cutting module for operation logging and centralized error handling |

---

## 2. Allocation Workflow – Module Flow

Sequential steps executed for every allocation request:

1. **Receive allocation request** — Controller accepts `productId`, `quantity` (and optionally `warehouseId`).
2. **Validate product exists** — Service calls the Product repository to confirm the product is valid.
3. **Select warehouse** — If `warehouseId` is provided, use it directly; otherwise the service automatically selects a warehouse based on available stock.
4. **Check capacity and stock** — Service verifies the warehouse has enough `available_quantity` and is within `capacity`. If this check fails, the request is rejected with a `400` error.
5. **Lock inventory record** — Repository layer applies optimistic locking (via the `version` column on `Warehouse_Inventory`) to prevent concurrent over-allocation.
6. **Allocate stock** — `available_quantity` is decremented and the inventory record is updated.
7. **Create allocation record** — A new row is inserted into `Allocation` with status and `allocated_at` timestamp; an audit log entry is written.
8. **Return confirmation response** — Controller returns the allocation confirmation to the client.

### Failure Path
If product validation, capacity check, or stock check fails at any point, the flow short-circuits and returns a `400`/`404` error response instead of proceeding to locking and allocation.

---

## 3. Related Workflows (Same Layered Pattern)

- **Stock Transfer**: Validates source warehouse has sufficient stock and target warehouse has capacity, then atomically updates both `Warehouse_Inventory` records and writes a `Stock_Transfer` record.
- **Warehouse CRUD**: Create/Update/Activate/Deactivate operate directly through Controller → Service → Repository; deactivation/delete is a soft delete (status flag), not a row removal.
- **Search**: Allocation and Stock Transfer search endpoints support filtering by product, warehouse, and date range, with pagination and sorting handled in the Service/Repository layers.
