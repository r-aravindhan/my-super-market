# Supermarket Billing Application

A microservices-based supermarket billing system built with **Java 25**, **Spring Boot 4.0.3**, **Lombok**, and **Gradle**. The application follows microservice design patterns and clean code principles with comprehensive REST APIs and test coverage.

## Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ Billing Service │────▶│ Inventory Svc   │     │  Pricing Svc    │
│   (Port 8080)   │     │   (Port 8081)   │     │  (Port 8082)    │
│                 │────▶│                 │     │                 │
│   Orchestrator  │     │  Stock Mgmt     │     │  Price Mgmt     │
└────────┬────────┘     └─────────────────┘     └─────────────────┘
         │
         │              ┌─────────────────┐
         └─────────────▶│ Discounts Svc   │
                        │   (Port 8083)   │
                        │                 │
                        │  Promotions     │
                        └─────────────────┘
```

## Microservices

| Service | Port | Description |
|---------|------|-------------|
| **billing-service** | 8080 | Orchestrates checkout flow, creates bills, coordinates other services |
| **inventory-service** | 8081 | Product catalog, stock management, stock check/reservation |
| **pricing-service** | 8082 | Product pricing, price calculation |
| **discounts-service** | 8083 | Discount codes, promotions (percentage, fixed amount, BOGO) |

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.3**
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **Lombok**
- **Gradle 8.x/9.x**
- **Jakarta Validation**
- **ProblemDetail** for error responses

## Prerequisites

- **JDK 25+**
- **Gradle 8.14+** (to build; run `gradle wrapper` first to generate Gradle wrapper scripts)

## Getting Started

### Build All Services

```bash
# With Gradle installed:
gradle build

# Or after generating wrapper (gradle wrapper):
./gradlew build          # macOS/Linux
gradlew.bat build       # Windows
```

### Run All Services

Start each service in a separate terminal (order: inventory → pricing → discounts → billing):

```bash
# Terminal 1 - Inventory Service
./gradlew :inventory-service:bootRun

# Terminal 2 - Pricing Service
./gradlew :pricing-service:bootRun

# Terminal 3 - Discounts Service
./gradlew :discounts-service:bootRun

# Terminal 4 - Billing Service (orchestrator)
./gradlew :billing-service:bootRun
```

### Run Tests

```bash
# All tests
./gradlew test

# Single service tests
./gradlew :inventory-service:test
./gradlew :pricing-service:test
./gradlew :discounts-service:test
./gradlew :billing-service:test
```

## API Documentation

### Billing Service (8080)

**Create Bill**
```http
POST /api/v1/bills
Content-Type: application/json

{
  "items": {
    "SKU001": 2,
    "SKU002": 1
  },
  "discountCode": "SAVE10"
}
```

**Get Bill**
```http
GET /api/v1/bills/{billId}
```

### Inventory Service (8081)

- `POST /api/v1/products` - Create product
- `GET /api/v1/products/{sku}` - Get product
- `GET /api/v1/products?category=Dairy` - List products
- `PUT /api/v1/products/{sku}` - Update product
- `DELETE /api/v1/products/{sku}` - Delete product
- `POST /api/v1/products/stock/check` - Check stock availability
- `POST /api/v1/products/stock/reserve` - Reserve stock
- `POST /api/v1/products/stock/release` - Release reservation

### Pricing Service (8082)

- `POST /api/v1/prices` - Create price
- `GET /api/v1/prices/{sku}` - Get price
- `POST /api/v1/prices/calculate` - Calculate prices for items

### Discounts Service (8083)

- `POST /api/v1/discounts` - Create discount
- `GET /api/v1/discounts/{code}` - Get discount
- `GET /api/v1/discounts/active` - List active discounts
- `POST /api/v1/discounts/apply` - Apply discount to cart

## Sample Flow

1. **Add products** to inventory (pre-seeded via `data.sql`)
2. **Add prices** for products (pre-seeded)
3. **Add discounts** (pre-seeded: SAVE10, FLAT5, MILK2FOR1)
4. **Create a bill** with items and optional discount code:

```bash
curl -X POST http://localhost:8080/api/v1/bills \
  -H "Content-Type: application/json" \
  -d '{"items":{"SKU001":3,"SKU002":2},"discountCode":"SAVE10"}'
```

## Design Patterns & Principles

- **Microservice Architecture**: Each domain (inventory, pricing, discounts) is an independent service
- **RESTful APIs**: Versioned APIs (`/api/v1/`), proper HTTP verbs, meaningful status codes
- **Clean Code**: Single responsibility, DTOs for requests/responses, proper exception handling
- **Database per Service**: Each service has its own H2 database
- **Saga Pattern** (simplified): Billing orchestrates the flow; stock release on failure
- **ProblemDetail**: RFC 7807 error responses for API consumers

## Test Coverage

- **Unit Tests**: Service layer logic with mocked dependencies (Mockito)
- **Integration Tests**: Controller layer with `@SpringBootTest`, `MockMvc`, `@MockBean` for external clients

## Project Structure

**Location:** `C:\Trainings\supermarket-billing\`

```
C:\Trainings\supermarket-billing\
├── build.gradle
├── settings.gradle
├── gradle.properties
├── inventory-service/    (separate folder)
├── pricing-service/      (separate folder)
├── discounts-service/    (separate folder)
└── billing-service/      (separate folder)
```

Each service follows the structure:
```
{service}/
├── src/main/java/com/supermarket/{domain}/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── exception/
│   └── client/  (billing-service only)
└── src/test/java/
```

## License

MIT
