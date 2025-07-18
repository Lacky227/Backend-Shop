# Backend-Shop
## Overview

BackendShop is a modular microservices-based e-commerce system designed for scalability, maintainability, and ease of deployment. It utilizes RabbitMQ for asynchronous communication and REST for synchronous requests. The system is containerized and orchestrated using Docker Compose.

### Architecture

1. AuthService: Handles user registration, login, JWT authentication, and token management.

2. NotificationService: Sends subscription emails and manages unsubscribe tokens.

3. ProductService: Manages product catalog and related operations.

4. ApiGateway: Acts as the main entry point, handles routing with Traefik and secures endpoints.

### Inter-Service Communication

REST API for synchronous calls.

RabbitMQ for event-driven communication.

#### Swagger UI Endpoints

- AuthService: http://localhost:8081/swagger-ui/index.html

- NotificationService: http://localhost:8082/swagger-ui/index.html

- ProductService: http://localhost:8083/swagger-ui/index.html

- ApiGateway: http://localhost/swagger-ui/index.html

## Running the Project

### Prerequisites:
- Docker

- Docker Compose

- Startup Instructions

- docker-compose up --build

- Docker Compose Services

- auth-service

- notification-service

- product-service

- gateway (with Traefik)

- RabbitMQ

- PostgreSQL

- MongoDB

### First-Time Setup Notes

Databases are auto-initialized by their respective services.

Ensure .env files are configured properly before startup.

## Environment Variables

Critical environment variables for services:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/luckyshop
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=yourpassword

RABBITMQ_HOST=rabbitmq
RABBITMQ_USERNAME=rabbituser
RABBITMQ_PASSWORD=rabbitpass

SPRING_MAIL_USERNAME=your-email@example.com
SPRING_MAIL_PASSWORD=your-email-password

CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

JWT_SECRET=your-jwt-secret-key
```
## Network Configuration

### Docker Network

Common network: luckyshop-net

### Exposed Ports

API Gateway (Traefik) - 80 (HTTP), 443 (HTTPS)

RabbitMQ Management - 15672

PostgreSQL - 5432

MongoDB - 27017

### API Gateway

- Traefik is configured as the main gateway router

- Routes requests based on domain/subdomain/path

- Uses HTTPS with Let's Encrypt or self-signed certificates

### Useful Links

- Swagger UI (via gateway): http://localhost/swagger-ui/index.html

- RabbitMQ Panel: http://localhost:15672 (user: rabbituser, pass: rabbitpass)

- Traefik Dashboard: http://localhost:8080 (if enabled)

### Security

- JWT tokens are generated with HMAC SHA-256 and secret key

- Tokens are never stored client-side; only passed via Authorization: Bearer <token>

- Secrets (JWT, DB passwords) are defined via environment variables and kept out of version control

- Service Isolation: All services are on the luckyshop-net internal Docker network for better isolation