# Warehouse Allocation System

An enterprise-level Warehouse Allocation System that manages product distribution across multiple warehouses, ensures optimal stock allocation, prevents over-allocation, and maintains allocation history for audit purposes.

## Features

- Warehouse CRUD operations (Create, Update, Activate, Deactivate, Soft Delete)
- Product management
- Allocation of products to a specific warehouse, or automatic warehouse selection based on available stock
- Stock transfer between warehouses
- Capacity validation before allocation
- Prevention of negative / over-allocation of stock
- Allocation history tracking with timestamps (audit logging)
- Search allocations by product, warehouse, and date range
- Pagination and sorting on list/search endpoints
- Optimistic locking for concurrent allocation requests

## Tech Stack

- Language/Framework: _(fill in, e.g. Java 17 + Spring Boot 3 / Node.js + Express)_
- Database: _(fill in, e.g. PostgreSQL / MySQL)_
- ORM: _(fill in, e.g. Spring Data JPA / Hibernate)_
- API Docs: Swagger / OpenAPI
- Testing: _(fill in, e.g. JUnit5 + Mockito)_
- Build Tool: _(fill in, e.g. Maven / Gradle)_

## Architecture

The project follows a clean layered architecture:

```
Controller Layer → Service Layer → Repository Layer → Database
```

See `Architecture_and_Module_Flow.md` for the full architecture diagram and the allocation workflow breakdown.

## Database Schema

| Table | Key Columns |
|---|---|
| Warehouse | id, name, location, capacity, status, created_at |
| Product | id, name, sku, total_stock |
| Warehouse_Inventory | id, warehouse_id (FK), product_id (FK), available_quantity, version |
| Allocation | id, product_id (FK), warehouse_id (FK), quantity, status, allocated_at |
| Stock_Transfer | id, source_warehouse_id (FK), target_warehouse_id (FK), product_id (FK), quantity, transfer_date |

See `warehouse_allocation_schema.sql` (or equivalent schema script) for the full DDL, and the ER diagram for relationships.

## Getting Started

### Prerequisites

- _(fill in, e.g. JDK 17+, Maven 3.8+, PostgreSQL 14+)_
- Git

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd warehouse-allocation-system
   ```

2. Configure the database connection:
   - Update `application.yml` / `.env` with your database URL, username, and password.
   - Create the database, then run the schema script:
     ```bash
     psql -U <user> -d <database> -f warehouse_allocation_schema.sql
     ```

3. Install dependencies and build:
   ```bash
   mvn clean install
   ```
   _(or `npm install`, depending on stack)_

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   _(or `npm start`)_

5. The API will be available at:
   ```
   http://localhost:8080/api/v1
   ```

### API Documentation

Once running, Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

### Postman Collection

Import `Warehouse_Allocation_System.postman_collection.json` into Postman to explore and test all endpoints, including the negative-test scenarios under "Error Scenarios".

## Running Tests

```bash
mvn test
```

Minimum unit test coverage target: **70%**.

## Project Structure

```
src/
├── main/
│   ├── java/.../controller/     # REST controllers
│   ├── java/.../service/        # Business logic
│   ├── java/.../repository/     # Data access layer
│   ├── java/.../entity/         # Domain entities
│   ├── java/.../dto/            # Request/response DTOs
│   ├── java/.../exception/      # Global exception handling
│   └── resources/
│       └── application.yml
└── test/
    └── java/.../                # Unit tests
```

## Key Design Decisions

See `Assumptions_and_Design_Decisions.md` for detailed notes, including:
- Optimistic locking strategy for concurrent allocation requests
- Automatic warehouse selection algorithm
- Soft delete approach for warehouses
- Error handling and audit logging strategy

## Deliverables Checklist

- [x] Complete Source Code
- [x] Database Schema Script
- [x] Swagger Documentation
- [x] Postman Collection
- [x] Unit Test Cases
- [x] README (this file)
- [x] Assumptions & Design Decisions document

## License

_(fill in license, e.g. MIT)_
