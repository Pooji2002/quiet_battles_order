# QUIET BATTLES™ – Order Service

The Order Service is a backend microservice responsible for managing customer orders in the QUIET BATTLES™ application.

It handles order creation, order details, order cancellation, and communication with the Product Service to maintain inventory consistency.

## Technology Stack

- Java 17
- Spring Boot 4.1.2
- Spring Security
- Spring Data JPA
- Spring WebMVC
- MySQL
- JSON Web Token (JWT)
- JJWT 0.12.6
- Bean Validation
- Spring Boot Actuator
- Maven

## Service Overview

| Property | Value |
|---|---|
| Service Name | Order Service |
| Port | 8094 |
| Database | qb_order_db |
| Build Tool | Maven |
| Authentication | JWT |
| Internal Service Communication | X-INTERNAL-SERVICE-KEY |

## Key Features

### 1. Order Creation

- Allows customers to place orders.
- Accepts order details and associated order items.
- Stores order information in the Order database.
- Coordinates with the Product Service to reserve available stock.
- Processes orders only when the required inventory reservation succeeds.

### 2. Order Management

- Retrieves order information.
- Retrieves orders associated with customers.
- Maintains order details and order items.
- Stores selected product sizes and quantities.
- Tracks order status.

### 3. Order Cancellation

- Allows eligible orders to be cancelled.
- Updates the order status.
- Communicates with the Product Service to release reserved stock.
- Prevents repeated cancellation from restoring stock more than once.

### 4. Product Service Integration

The Order Service communicates with the Product Service for inventory operations.

**Order creation flow:**

1. Customer submits an order.
2. Order Service receives the order request.
3. Order Service requests stock reservation from Product Service.
4. Product Service checks and reserves available stock.
5. Order Service saves the order and its items.

**Order cancellation flow:**

1. Customer requests order cancellation.
2. Order Service updates the order status.
3. Order Service requests stock release from Product Service.
4. Product Service restores the reserved stock.

This coordination helps maintain inventory consistency between the services.

### 5. Security

- Uses Spring Security for API protection.
- Supports JWT-based authentication.
- Uses the internal service authentication header `X-INTERNAL-SERVICE-KEY` when communicating with the Product Service.

## Microservice Architecture

The Order Service operates independently and communicates with the frontend through the API Gateway.

**Application flow:**

Frontend → API Gateway (8090) → Order Service (8094) → MySQL

**Inventory integration:**

Order Service (8094) ↔ Product Service (8093)

## Database Configuration

The service uses MySQL with the database:

`qb_order_db`

Create the database before starting the application:

```sql
CREATE DATABASE qb_order_db;
```

Configure the database connection and security settings in the application's configuration file.

Do not commit database passwords, JWT secrets, or internal service keys to GitHub.

## Running the Application Locally

### Prerequisites

- Java 17 or later
- Maven
- MySQL Server
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Pooji2002/quiet_battles_order.git
```

### 2. Open the project

Open the cloned project in Eclipse, IntelliJ IDEA, or VS Code.

### 3. Configure MySQL

Create the `qb_order_db` database and configure the database connection.

Ensure the Product Service is configured and available for inventory operations.

### 4. Run the service

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from your IDE.

The service runs at:

`http://localhost:8094`

## Integration with Other Services

| Component | Port |
|---|---:|
| API Gateway | 8090 |
| Identity Service | 8091 |
| Content Service | 8092 |
| Product Service | 8093 |
| Order Service | 8094 |

The frontend communicates with Order Service APIs through the API Gateway.

The Order Service communicates directly with the Product Service for stock reservation and release operations.

## Project Repository

GitHub: https://github.com/Pooji2002/quiet_battles_order

## Project Context

This service is part of the QUIET BATTLES™ full-stack fashion and lifestyle e-commerce prototype.

**Brand Tagline: WEAR YOUR BATTLES.**