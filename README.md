# 🚍 Urban Transport Microservices System

A comprehensive microservices-based urban transportation management system built with Spring Boot, designed to handle user management, ticket purchasing, schedule management, real-time geolocation tracking, subscriptions, and notifications.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technology Stack](#technology-stack)
- [Project Status](#project-status)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)

## 🎯 Overview

This project implements a scalable microservices architecture for managing urban public transportation systems. It provides features for passengers to purchase tickets, track buses in real-time, manage subscriptions, and receive notifications about their journeys.

### Key Features

- 🔐 **User Authentication & Authorization** - JWT-based security with role management
- 🎫 **Digital Ticketing** - Purchase, validate, and manage digital tickets with QR codes
- 📅 **Schedule Management** - Real-time bus schedules, routes, and stops
- 📍 **Real-time Tracking** - Live bus geolocation and ETA calculations
- 💳 **Subscription Management** - Monthly passes and subscription plans
- 🔔 **Smart Notifications** - Real-time alerts via email, SMS, and push notifications
- 📊 **Monitoring & Logging** - Centralized logging with ELK stack and metrics with Prometheus/Grafana

## 🏗️ Architecture

The system follows a **microservices architecture** with the following key patterns:

- **API Gateway Pattern** - Single entry point for all client requests
- **Service Registry & Discovery** - Eureka for service registration and discovery
- **Centralized Configuration** - Config Server for external configuration management
- **Database per Service** - Each microservice manages its own database
- **Event-Driven Communication** - Kafka for asynchronous inter-service messaging
- **CQRS Pattern** - Separation of read and write operations where applicable
- **Circuit Breaker Pattern** - Resilience4j for fault tolerance

### Architecture Diagram

```
┌─────────────┐
│   Clients   │
│ (Web/Mobile)│
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│   API Gateway   │ ◄──── Rate Limiting, Auth, Routing
└────────┬────────┘
         │
    ┌────┴────┐
    │ Eureka  │ ◄──── Service Discovery
    └─────────┘
         │
    ┌────┴────────────────────────────────┐
    │                                     │
    ▼                                     ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│    User     │  │   Ticket    │  │  Schedule   │
│   Service   │  │   Service   │  │   Service   │
└──────┬──────┘  └──────┬──────┘  └──────┬──────┘
       │                │                │
       ▼                ▼                ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  PostgreSQL │  │  PostgreSQL │  │  PostgreSQL │
└─────────────┘  └─────────────┘  └─────────────┘

         ┌─────────────┐
         │    Kafka    │ ◄──── Event Bus
         └──────┬──────┘
                │
    ┌───────────┼───────────┐
    ▼           ▼           ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│Geolocation│ │Subscription│ │Notification│
│  Service │ │  Service │ │  Service │
└────┬─────┘ └────┬─────┘ └────┬─────┘
     ▼            ▼            ▼
┌─────────┐  ┌─────────┐  ┌─────────┐
│ MongoDB │  │PostgreSQL│ │PostgreSQL│
└─────────┘  └─────────┘  └─────────┘
```

## 🚀 Microservices

### Infrastructure Services

| Service | Port | Description | Technology |
|---------|------|-------------|------------|
| **Config Server** | 8888 | Centralized configuration management | Spring Cloud Config |
| **Service Registry** | 8761 | Service discovery and registration | Netflix Eureka |
| **API Gateway** | 8080 | Single entry point, routing, security | Spring Cloud Gateway |

### Business Services

| Service | Port | Description | Database |
|---------|------|-------------|----------|
| **User Service** | 8081 | Authentication, authorization, user profiles | PostgreSQL |
| **Ticket Service** | 8082 | Ticket purchase, validation, QR code generation | PostgreSQL |
| **Schedule Service** | 8083 | Routes, schedules, stops, timetables | PostgreSQL |
| **Geolocation Service** | 8084 | Real-time bus tracking, location updates | MongoDB |
| **Subscription Service** | 8085 | Subscription plans, billing, renewals | PostgreSQL |
| **Notification Service** | 8086 | Email, SMS, push notifications | PostgreSQL |

## 🛠️ Technology Stack

### Backend

- **Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Build Tool:** Maven
- **Service Communication:** REST APIs, Apache Kafka
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway
- **Configuration:** Spring Cloud Config
- **Security:** Spring Security, JWT, OAuth2

### Databases

- **Relational:** PostgreSQL (User, Ticket, Schedule, Subscription, Notification services)
- **NoSQL:** MongoDB (Geolocation service)
- **Caching:** Redis
- **Message Broker:** Apache Kafka

### DevOps & Infrastructure

- **Containerization:** Docker, Docker Compose
- **Orchestration:** Kubernetes
- **Infrastructure as Code:** Terraform
- **CI/CD:** GitHub Actions
- **Monitoring:** Prometheus, Grafana
- **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana)
- **Distributed Tracing:** Zipkin/Jaeger

### Frontend

- **Framework:** React.js
- **State Management:** Redux/Context API
- **UI Library:** Material-UI / Tailwind CSS
- **Mobile:** React Native

## 📊 Project Status

### ✅ Completed

- [x] Project structure definition
- [x] Infrastructure services (Config Server, Eureka, API Gateway)
- [x] Domain Driven Design
- [x] UML Diagrams
- [x] C4 Diagrams
- [x] BPMN Diagrams

### 🚧 In Progress

- [ ] **Database Design (MCD)** - Conceptual data model for each service
- [ ] **Inter-service Communication** - Define API contracts and event schemas
- [ ] **Security Implementation** - Complete authentication, authorization, and data encryption
- [ ] **Technology Stack Finalization** - Select specific frameworks and libraries

### 📝 Pending

#### Database Design (Database per Service)
- [ ] Create Entity-Relationship Diagrams (MCD) for each service
- [ ] Define database schemas and migration scripts
- [ ] Establish relationships between bounded contexts
- [ ] Design event store for event sourcing (if applicable)

#### Service Interaction Definition
- [ ] Define REST API contracts (OpenAPI/Swagger specifications)
- [ ] Design event schemas for Kafka topics
- [ ] Implement service-to-service authentication
- [ ] Define retry and timeout strategies
- [ ] Design circuit breaker patterns

#### Security Mechanisms
- [ ] **Authentication & Authorization:**
  - Implement OAuth2/OpenID Connect
  - JWT token generation and validation
  - Role-Based Access Control (RBAC)
  - API key management for external integrations
  
- [ ] **Data Encryption:**
  - TLS/SSL for data in transit
  - Database encryption at rest
  - Sensitive data encryption (passwords, payment info)
  - Secret management (HashiCorp Vault)

- [ ] **API Security:**
  - Rate limiting per user/IP
  - CORS configuration
  - SQL injection prevention
  - XSS protection
  - CSRF tokens

#### Tools & Technologies Definition
- [ ] **Programming Languages:** Java 17, JavaScript/TypeScript
- [ ] **Frameworks:** Spring Boot, React, React Native
- [ ] **Testing:** JUnit 5, Mockito, TestContainers, Cypress
- [ ] **Deployment:** Docker, Kubernetes, AWS/Azure/GCP
- [ ] **Monitoring:** Prometheus, Grafana, ELK Stack

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Node.js 18+** (for frontend)
- **PostgreSQL 14+**
- **MongoDB 6+**
- **Apache Kafka 3.x**
- **Redis 7+**
- **Git**

Optional (for production deployment):
- **Kubernetes** (Minikube for local, EKS/AKS/GKE for cloud)
- **Terraform**
- **kubectl**
- **Helm**

## 📁 Project Structure

```
transport-urbain-microservices/
│
├── backend/
│   │
│   ├── config-server/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/config/
│   │   │   │   │   ├── ConfigServerApplication.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── bootstrap.yml
│   │   │   └── test/
│   │   ├── config-repo/
│   │   │   ├── api-gateway.yml
│   │   │   ├── api-gateway-dev.yml
│   │   │   ├── api-gateway-prod.yml
│   │   │   ├── user-service.yml
│   │   │   ├── user-service-dev.yml
│   │   │   ├── user-service-prod.yml
│   │   │   ├── ticket-service.yml
│   │   │   ├── schedule-service.yml
│   │   │   ├── geolocation-service.yml
│   │   │   ├── subscription-service.yml
│   │   │   └── notification-service.yml
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── service-registry/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/registry/
│   │   │   │   │   ├── ServiceRegistryApplication.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── bootstrap.yml
│   │   │   └── test/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── api-gateway/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/gateway/
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── GatewayConfig.java
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   │   ├── RouteConfig.java
│   │   │   │   │   │   └── RateLimitConfig.java
│   │   │   │   │   ├── filter/
│   │   │   │   │   │   ├── AuthenticationFilter.java
│   │   │   │   │   │   ├── LoggingFilter.java
│   │   │   │   │   │   └── RequestValidationFilter.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   └── GatewayException.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── JwtUtil.java
│   │   │   │   │   │   └── ResponseUtil.java
│   │   │   │   │   └── ApiGatewayApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       └── bootstrap.yml
│   │   │   └── test/
│   │   │       └── java/com/transport/gateway/
│   │   │           ├── filter/
│   │   │           │   └── AuthenticationFilterTest.java
│   │   │           └── integration/
│   │   │               └── GatewayIntegrationTest.java
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── user-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/user/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── UserController.java
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   └── ProfileController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── UserServiceImpl.java
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── AuthServiceImpl.java
│   │   │   │   │   │   ├── JwtService.java
│   │   │   │   │   │   └── JwtServiceImpl.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   │   ├── RoleRepository.java
│   │   │   │   │   │   └── RefreshTokenRepository.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Role.java
│   │   │   │   │   │   ├── Permission.java
│   │   │   │   │   │   ├── RefreshToken.java
│   │   │   │   │   │   └── UserProfile.java
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   │   ├── UpdateProfileRequest.java
│   │   │   │   │   │   │   └── ChangePasswordRequest.java
│   │   │   │   │   │   ├── response/
│   │   │   │   │   │   │   ├── UserResponse.java
│   │   │   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   │   │   ├── ProfileResponse.java
│   │   │   │   │   │   │   └── JwtResponse.java
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       ├── UserMapper.java
│   │   │   │   │   │       └── ProfileMapper.java
│   │   │   │   │   ├── security/
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   │   │   └── PasswordEncoderConfig.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   │   └── RedisConfig.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   ├── UserNotFoundException.java
│   │   │   │   │   │   ├── DuplicateUserException.java
│   │   │   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   │   │   └── TokenExpiredException.java
│   │   │   │   │   ├── event/
│   │   │   │   │   │   ├── UserCreatedEvent.java
│   │   │   │   │   │   ├── UserUpdatedEvent.java
│   │   │   │   │   │   ├── UserDeletedEvent.java
│   │   │   │   │   │   └── producer/
│   │   │   │   │   │       └── UserEventProducer.java
│   │   │   │   │   ├── validation/
│   │   │   │   │   │   ├── EmailValidator.java
│   │   │   │   │   │   ├── PasswordValidator.java
│   │   │   │   │   │   └── PhoneValidator.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── DateUtil.java
│   │   │   │   │   │   └── ValidationUtil.java
│   │   │   │   │   └── UserServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       ├── db/
│   │   │   │       │   └── migration/
│   │   │   │       │       ├── V1__create_users_table.sql
│   │   │   │       │       ├── V2__create_roles_table.sql
│   │   │   │       │       ├── V3__create_permissions_table.sql
│   │   │   │       │       └── V4__create_refresh_tokens_table.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── test/
│   │   │       └── java/com/transport/user/
│   │   │           ├── controller/
│   │   │           │   ├── UserControllerTest.java
│   │   │           │   └── AuthControllerTest.java
│   │   │           ├── service/
│   │   │           │   ├── UserServiceTest.java
│   │   │           │   └── AuthServiceTest.java
│   │   │           ├── repository/
│   │   │           │   └── UserRepositoryTest.java
│   │   │           └── integration/
│   │   │               └── UserIntegrationTest.java
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── ticket-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/ticket/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── TicketController.java
│   │   │   │   │   │   ├── PaymentController.java
│   │   │   │   │   │   └── TransactionController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── TicketService.java
│   │   │   │   │   │   ├── TicketServiceImpl.java
│   │   │   │   │   │   ├── PaymentService.java
│   │   │   │   │   │   ├── PaymentServiceImpl.java
│   │   │   │   │   │   ├── QRCodeService.java
│   │   │   │   │   │   ├── QRCodeServiceImpl.java
│   │   │   │   │   │   ├── ValidationService.java
│   │   │   │   │   │   └── ValidationServiceImpl.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── TicketRepository.java
│   │   │   │   │   │   ├── TransactionRepository.java
│   │   │   │   │   │   └── TicketTypeRepository.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Ticket.java
│   │   │   │   │   │   ├── TicketType.java
│   │   │   │   │   │   ├── Transaction.java
│   │   │   │   │   │   ├── Payment.java
│   │   │   │   │   │   ├── TicketStatus.java (enum)
│   │   │   │   │   │   ├── PaymentStatus.java (enum)
│   │   │   │   │   │   └── PaymentMethod.java (enum)
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── PurchaseTicketRequest.java
│   │   │   │   │   │   │   ├── ValidateTicketRequest.java
│   │   │   │   │   │   │   └── PaymentRequest.java
│   │   │   │   │   │   ├── response/
│   │   │   │   │   │   │   ├── TicketResponse.java
│   │   │   │   │   │   │   ├── PurchaseResponse.java
│   │   │   │   │   │   │   ├── PaymentResponse.java
│   │   │   │   │   │   │   └── TransactionResponse.java
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       ├── TicketMapper.java
│   │   │   │   │   │       └── TransactionMapper.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   │   └── PaymentGatewayConfig.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   ├── TicketNotFoundException.java
│   │   │   │   │   │   ├── InvalidTicketException.java
│   │   │   │   │   │   ├── PaymentFailedException.java
│   │   │   │   │   │   └── InsufficientBalanceException.java
│   │   │   │   │   ├── event/
│   │   │   │   │   │   ├── TicketPurchasedEvent.java
│   │   │   │   │   │   ├── TicketValidatedEvent.java
│   │   │   │   │   │   ├── PaymentProcessedEvent.java
│   │   │   │   │   │   ├── producer/
│   │   │   │   │   │   │   └── TicketEventProducer.java
│   │   │   │   │   │   └── consumer/
│   │   │   │   │   │       └── UserEventConsumer.java
│   │   │   │   │   ├── payment/
│   │   │   │   │   │   ├── gateway/
│   │   │   │   │   │   │   ├── PaymentGateway.java
│   │   │   │   │   │   │   ├── StripeGateway.java
│   │   │   │   │   │   │   └── PayPalGateway.java
│   │   │   │   │   │   └── strategy/
│   │   │   │   │   │       ├── PaymentStrategy.java
│   │   │   │   │   │       └── PaymentStrategyFactory.java
│   │   │   │   │   ├── saga/
│   │   │   │   │   │   ├── TicketPurchaseSaga.java
│   │   │   │   │   │   └── SagaOrchestrator.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── QRCodeGenerator.java
│   │   │   │   │   │   └── PriceCalculator.java
│   │   │   │   │   └── TicketServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── db/migration/
│   │   │   │       │   ├── V1__create_tickets_table.sql
│   │   │   │       │   ├── V2__create_ticket_types_table.sql
│   │   │   │       │   ├── V3__create_transactions_table.sql
│   │   │   │       │   └── V4__create_payments_table.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── test/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── schedule-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/schedule/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── ScheduleController.java
│   │   │   │   │   │   ├── RouteController.java
│   │   │   │   │   │   ├── StopController.java
│   │   │   │   │   │   └── BusController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── ScheduleService.java
│   │   │   │   │   │   ├── ScheduleServiceImpl.java
│   │   │   │   │   │   ├── RouteService.java
│   │   │   │   │   │   ├── RouteServiceImpl.java
│   │   │   │   │   │   ├── StopService.java
│   │   │   │   │   │   ├── StopServiceImpl.java
│   │   │   │   │   │   ├── BusService.java
│   │   │   │   │   │   └── BusServiceImpl.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── ScheduleRepository.java
│   │   │   │   │   │   ├── RouteRepository.java
│   │   │   │   │   │   ├── StopRepository.java
│   │   │   │   │   │   └── BusRepository.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Schedule.java
│   │   │   │   │   │   ├── Route.java
│   │   │   │   │   │   ├── Stop.java
│   │   │   │   │   │   ├── Bus.java
│   │   │   │   │   │   ├── RouteStop.java
│   │   │   │   │   │   ├── DayOfWeek.java (enum)
│   │   │   │   │   │   └── BusStatus.java (enum)
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── CreateScheduleRequest.java
│   │   │   │   │   │   │   ├── UpdateScheduleRequest.java
│   │   │   │   │   │   │   ├── CreateRouteRequest.java
│   │   │   │   │   │   │   ├── CreateStopRequest.java
│   │   │   │   │   │   │   └── SearchScheduleRequest.java
│   │   │   │   │   │   ├── response/
│   │   │   │   │   │   │   ├── ScheduleResponse.java
│   │   │   │   │   │   │   ├── RouteResponse.java
│   │   │   │   │   │   │   ├── StopResponse.java
│   │   │   │   │   │   │   ├── BusResponse.java
│   │   │   │   │   │   │   └── RouteDetailsResponse.java
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       ├── ScheduleMapper.java
│   │   │   │   │   │       ├── RouteMapper.java
│   │   │   │   │   │       └── StopMapper.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   │   └── CacheConfig.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   ├── ScheduleNotFoundException.java
│   │   │   │   │   │   ├── RouteNotFoundException.java
│   │   │   │   │   │   ├── StopNotFoundException.java
│   │   │   │   │   │   └── InvalidScheduleException.java
│   │   │   │   │   ├── event/
│   │   │   │   │   │   ├── ScheduleCreatedEvent.java
│   │   │   │   │   │   ├── ScheduleUpdatedEvent.java
│   │   │   │   │   │   ├── RouteChangedEvent.java
│   │   │   │   │   │   ├── producer/
│   │   │   │   │   │   │   └── ScheduleEventProducer.java
│   │   │   │   │   │   └── consumer/
│   │   │   │   │   │       └── BusLocationConsumer.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── TimeCalculator.java
│   │   │   │   │   │   └── RouteOptimizer.java
│   │   │   │   │   └── ScheduleServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── db/migration/
│   │   │   │       │   ├── V1__create_routes_table.sql
│   │   │   │       │   ├── V2__create_stops_table.sql
│   │   │   │       │   ├── V3__create_schedules_table.sql
│   │   │   │       │   ├── V4__create_buses_table.sql
│   │   │   │       │   └── V5__create_route_stops_table.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── test/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── geolocation-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/geo/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── GeolocationController.java
│   │   │   │   │   │   ├── TrackingController.java
│   │   │   │   │   │   └── WebSocketController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── GeolocationService.java
│   │   │   │   │   │   ├── GeolocationServiceImpl.java
│   │   │   │   │   │   ├── TrackingService.java
│   │   │   │   │   │   ├── TrackingServiceImpl.java
│   │   │   │   │   │   ├── MapService.java
│   │   │   │   │   │   ├── MapServiceImpl.java
│   │   │   │   │   │   ├── DistanceCalculationService.java
│   │   │   │   │   │   └── ETACalculationService.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── BusLocationRepository.java
│   │   │   │   │   │   ├── TrackingHistoryRepository.java
│   │   │   │   │   │   └── GeofenceRepository.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── BusLocation.java
│   │   │   │   │   │   ├── TrackingHistory.java
│   │   │   │   │   │   ├── Geofence.java
│   │   │   │   │   │   ├── Coordinates.java
│   │   │   │   │   │   └── LocationStatus.java (enum)
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── UpdateLocationRequest.java
│   │   │   │   │   │   │   ├── TrackBusRequest.java
│   │   │   │   │   │   │   └── CalculateETARequest.java
│   │   │   │   │   │   ├── response/
│   │   │   │   │   │   │   ├── LocationResponse.java
│   │   │   │   │   │   │   ├── TrackingResponse.java
│   │   │   │   │   │   │   ├── ETAResponse.java
│   │   │   │   │   │   │   └── NearbyBusResponse.java
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       └── LocationMapper.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── MongoConfig.java
│   │   │   │   │   │   ├── WebSocketConfig.java
│   │   │   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   │   └── RedisConfig.java
│   │   │   │   │   ├── websocket/
│   │   │   │   │   │   ├── LocationWebSocketHandler.java
│   │   │   │   │   │   ├── WebSocketSessionManager.java
│   │   │   │   │   │   └── WebSocketMessageBroker.java
│   │   │   │   │   ├── integration/
│   │   │   │   │   │   ├── GoogleMapsClient.java
│   │   │   │   │   │   ├── OpenStreetMapClient.java
│   │   │   │   │   │   └── MapApiClient.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   ├── LocationNotFoundException.java
│   │   │   │   │   │   ├── InvalidCoordinatesException.java
│   │   │   │   │   │   └── MapServiceException.java
│   │   │   │   │   ├── event/
│   │   │   │   │   │   ├── LocationUpdatedEvent.java
│   │   │   │   │   │   ├── BusArrivedEvent.java
│   │   │   │   │   │   ├── GeofenceEnteredEvent.java
│   │   │   │   │   │   ├── producer/
│   │   │   │   │   │   │   └── LocationEventProducer.java
│   │   │   │   │   │   └── consumer/
│   │   │   │   │   │       ├── LocationUpdateConsumer.java
│   │   │   │   │   │       └── ScheduleEventConsumer.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── GeoUtils.java
│   │   │   │   │   │   ├── DistanceCalculator.java
│   │   │   │   │   │   └── CoordinateValidator.java
│   │   │   │   │   └── GeolocationServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── logback-spring.xml
│   │   │   └── test/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── subscription-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/transport/subscription/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── SubscriptionController.java
│   │   │   │   │   │   ├── PlanController.java
│   │   │   │   │   │   └── BillingController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── SubscriptionService.java
│   │   │   │   │   │   ├── SubscriptionServiceImpl.java
│   │   │   │   │   │   ├── PlanService.java
│   │   │   │   │   │   ├── PlanServiceImpl.java
│   │   │   │   │   │   ├── BillingService.java
│   │   │   │   │   │   ├── BillingServiceImpl.java
│   │   │   │   │   │   ├── RenewalService.java
│   │   │   │   │   │   └── RenewalServiceImpl.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── SubscriptionRepository.java
│   │   │   │   │   │   ├── PlanRepository.java
│   │   │   │   │   │   └── BillingHistoryRepository.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Subscription.java
│   │   │   │   │   │   ├── Plan.java
│   │   │   │   │   │   ├── BillingHistory.java
│   │   │   │   │   │   ├── SubscriptionStatus.java (enum)
│   │   │   │   │   │   ├── PlanType.java (enum)
│   │   │   │   │   │   └── BillingCycle.java (enum)
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── CreateSubscriptionRequest.java
│   │   │   │   │   │   │   ├── UpdateSubscriptionRequest.java
│   │   │   │   │   │   │   ├── CancelSubscriptionRequest.java
│   │   │   │   │   │   │   └── RenewSubscriptionRequest.java
│   │   │   │   │   │   ├── response/
│   │   │   │   │   │   │   ├── SubscriptionResponse.java
│   │   │   │   │   │   │   ├── PlanResponse.java
│   │   │   │   │   │   │   └── BillingHistoryResponse.java
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       ├── SubscriptionMapper.java
│   │   │   │   │   │       └── PlanMapper.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   │   └── SchedulerConfig.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   │   ├── SubscriptionNotFoundException.java
│   │   │   │   │   │   ├── PlanNotFoundException.java
│   │   │   │   │   │   ├── SubscriptionExpiredException.java
│   │   │   │   │   │   └── InvalidSubscriptionException.java
│   │   │   │   │   ├── event/
│   │   │   │   │   │   ├── SubscriptionCreatedEvent.java
│   │   │   │   │   │   ├── SubscriptionRenewedEvent.java
│   │   │   │   │   │   ├── SubscriptionCancelledEvent.java
│   │   │   │   │   │   ├── SubscriptionExpiredEvent.java
│   │   │   │   │   │   ├── producer/
│   │   │   │   │   │   │   └── SubscriptionEventProducer.java
│   │   │   │   │   │   └── consumer/
│   │   │   │   │   │       ├── UserEventConsumer.java
│   │   │   │   │   │       └── PaymentEventConsumer.java
│   │   │   │   │   ├── scheduler/
│   │   │   │   │   │   ├── SubscriptionRenewalScheduler.java
│   │   │   │   │   │   └── ExpirationCheckScheduler.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── DateCalculator.java
│   │   │   │   │   │   └── PriceCalculator.java
│   │   │   │   │   └── SubscriptionServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── db/migration/
│   │   │   │       │   ├── V1__create_plans_table.sql
│   │   │   │       │   ├── V2__create_subscriptions_table.sql
│   │   │   │       │   └── V3__create_billing_history_table.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── test/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   └── notification-service/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/transport/notification/
│       │   │   │   ├── controller/
│       │   │   │   │   ├── NotificationController.java
│       │   │   │   │   └── TemplateController.java
│       │   │   │   ├── service/
│       │   │   │   │   ├── NotificationService.java
│       │   │   │   │   ├── NotificationServiceImpl.java
│       │   │   │   │   ├── EmailService.java
│       │   │   │   │   ├── EmailServiceImpl.java
│       │   │   │   │   ├── SmsService.java
│       │   │   │   │   ├── SmsServiceImpl.java
│       │   │   │   │   ├── PushNotificationService.java
│       │   │   │   │   └── PushNotificationServiceImpl.java
│       │   │   │   ├── repository/
│       │   │   │   │   ├── NotificationRepository.java
│       │   │   │   │   ├── NotificationTemplateRepository.java
│       │   │   │   │   └── NotificationPreferenceRepository.java
│       │   │   │   ├── model/
│       │   │   │   │   ├── Notification.java
│       │   │   │   │   ├── NotificationTemplate.java
│       │   │   │   │   ├── NotificationPreference.java
│       │   │   │   │   ├── NotificationType.java (enum)
│       │   │   │   │   ├── NotificationStatus.java (enum)
│       │   │   │   │   └── NotificationChannel.java (enum)
│       │   │   │   ├── dto/
│       │   │   │   │   ├── request/
│       │   │   │   │   │   ├── SendNotificationRequest.java
│       │   │   │   │   │   ├── CreateTemplateRequest.java
│       │   │   │   │   │   └── UpdatePreferenceRequest.java
│       │   │   │   │   ├── response/
│       │   │   │   │   │   ├── NotificationResponse.java
│       │   │   │   │   │   ├── TemplateResponse.java
│       │   │   │   │   │   └── PreferenceResponse.java
│       │   │   │   │   └── mapper/
│       │   │   │   │       └── NotificationMapper.java
│       │   │   │   ├── config/
│       │   │   │   │   ├── MongoConfig.java
│       │   │   │   │   ├── KafkaConsumerConfig.java
│       │   │   │   │   ├── SwaggerConfig.java
│       │   │   │   │   ├── EmailConfig.java
│       │   │   │   │   └── SmsConfig.java
│       │   │   │   ├── consumer/
│       │   │   │   │   ├── TicketEventConsumer.java
│       │   │   │   │   ├── ScheduleEventConsumer.java
│       │   │   │   │   ├── SubscriptionEventConsumer.java
│       │   │   │   │   ├── LocationEventConsumer.java
│       │   │   │   │   └── UserEventConsumer.java
│       │   │   │   ├── email/
│       │   │   │   │   ├── EmailSender.java
│       │   │   │   │   ├── EmailTemplateEngine.java
│       │   │   │   │   └── EmailValidator.java
│       │   │   │   ├── sms/
│       │   │   │   │   ├── SmsSender.java
│       │   │   │   │   ├── TwilioClient.java
│       │   │   │   │   └── SmsFormatter.java
│       │   │   │   ├── push/
│       │   │   │   │   ├── PushNotificationSender.java
│       │   │   │   │   └── FirebaseClient.java
│       │   │   │   ├── exception/
│       │   │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   │   ├── NotificationSendException.java
│       │   │   │   │   ├── TemplateNotFoundException.java
│       │   │   │   │   └── InvalidRecipientException.java
│       │   │   │   ├── util/
│       │   │   │   │   ├── TemplateProcessor.java
│       │   │   │   │   └── NotificationFormatter.java
│       │   │   │   └── NotificationServiceApplication.java
│       │   │   └── resources/
│       │   │       ├── application.yml
│       │   │       ├── templates/
│       │   │       │   ├── email/
│       │   │       │   │   ├── ticket-confirmation.html
│       │   │       │   │   ├── subscription-reminder.html
│       │   │       │   │   ├── delay-notification.html
│       │   │       │   │   └── welcome.html
│       │   │       │   └── sms/
│       │   │       │       ├── ticket-confirmation.txt
│       │   │       │       └── delay-alert.txt
│       │   │       └── logback-spring.xml
│       │   └── test/
│       ├── pom.xml
│       ├── Dockerfile
│       └── README.md
│
├── frontend/
│   │
│   ├── passenger-app/
│   │   ├── public/
│   │   │   ├── index.html
│   │   │   ├── favicon.ico
│   │   │   ├── manifest.json
│   │   │   └── robots.txt
│   │   ├── src/
│   │   │   ├── api/
│   │   │   │   ├── axiosConfig.js
│   │   │   │   ├── authApi.js
│   │   │   │   ├── ticketApi.js
│   │   │   │   ├── scheduleApi.js
│   │   │   │   ├── geolocationApi.js
│   │   │   │   ├── subscriptionApi.js
│   │   │   │   └── notificationApi.js
│   │   │   ├── components/
│   │   │   │   ├── common/
│   │   │   │   │   ├── Header.jsx
│   │   │   │   │   ├── Footer.jsx
│   │   │   │   │   ├── Sidebar.jsx
│   │   │   │   │   ├── Navbar.jsx
│   │   │   │   │   ├── Button.jsx
│   │   │   │   │   ├── Input.jsx
│   │   │   │   │   ├── Card.jsx
│   │   │   │   │   ├── Modal.jsx
│   │   │   │   │   ├── Loading.jsx
│   │   │   │   │   ├── ErrorBoundary.jsx
│   │   │   │   │   └── ProtectedRoute.jsx
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginForm.jsx
│   │   │   │   │   ├── RegisterForm.jsx
│   │   │   │   │   ├── ForgotPassword.jsx
│   │   │   │   │   └── ResetPassword.jsx
│   │   │   │   ├── schedule/
│   │   │   │   │   ├── ScheduleList.jsx
│   │   │   │   │   ├── ScheduleCard.jsx
│   │   │   │   │   ├── RouteMap.jsx
│   │   │   │   │   ├── SearchSchedule.jsx
│   │   │   │   │   └── ScheduleFilter.jsx
│   │   │   │   ├── ticket/
│   │   │   │   │   ├── TicketPurchase.jsx
│   │   │   │   │   ├── TicketList.jsx
│   │   │   │   │   ├── TicketCard.jsx
│   │   │   │   │   ├── TicketQRCode.jsx
│   │   │   │   │   ├── PaymentForm.jsx
│   │   │   │   │   └── TicketHistory.jsx
│   │   │   │   ├── tracking/
│   │   │   │   │   ├── BusTracker.jsx
│   │   │   │   │   ├── MapView.jsx
│   │   │   │   │   ├── BusMarker.jsx
│   │   │   │   │   ├── StopMarker.jsx
│   │   │   │   │   └── ETADisplay.jsx
│   │   │   │   ├── subscription/
│   │   │   │   │   ├── SubscriptionPlans.jsx
│   │   │   │   │   ├── PlanCard.jsx
│   │   │   │   │   ├── MySubscription.jsx
│   │   │   │   │   ├── SubscriptionHistory.jsx
│   │   │   │   │   └── RenewalSettings.jsx
│   │   │   │   └── profile/
│   │   │   │       ├── UserProfile.jsx
│   │   │   │       ├── EditProfile.jsx
│   │   │   │       ├── ChangePassword.jsx
│   │   │   │       └── NotificationSettings.jsx
│   │   │   ├── pages/
│   │   │   │   ├── Home.jsx
│   │   │   │   ├── Login.jsx
│   │   │   │   ├── Register.jsx
│   │   │   │   ├── Dashboard.jsx
│   │   │   │   ├── Schedules.jsx
│   │   │   │   ├── Tickets.jsx
│   │   │   │   ├── BusTracking.jsx
│   │   │   │   ├── Subscriptions.jsx
│   │   │   │   ├── Profile.jsx
│   │   │   │   ├── NotFound.jsx
│   │   │   │   └── Unauthorized.jsx
│   │   │   ├── store/
│   │   │   │   ├── store.js
│   │   │   │   ├── slices/
│   │   │   │   │   ├── authSlice.js
│   │   │   │   │   ├── ticketSlice.js
│   │   │   │   │   ├── scheduleSlice.js
│   │   │   │   │   ├── locationSlice.js
│   │   │   │   │   ├── subscriptionSlice.js
│   │   │   │   │   └── notificationSlice.js
│   │   │   │   └── middleware/
│   │   │   │       ├── authMiddleware.js
│   │   │   │       └── errorMiddleware.js
│   │   │   ├── hooks/
│   │   │   │   ├── useAuth.js
│   │   │   │   ├── useWebSocket.js
│   │   │   │   ├── useGeolocation.js
│   │   │   │   ├── useLocalStorage.js
│   │   │   │   └── useDebounce.js
│   │   │   ├── utils/
│   │   │   │   ├── constants.js
│   │   │   │   ├── validators.js
│   │   │   │   ├── formatters.js
│   │   │   │   ├── dateUtils.js
│   │   │   │   ├── tokenUtils.js
│   │   │   │   └── mapUtils.js
│   │   │   ├── services/
│   │   │   │   ├── authService.js
│   │   │   │   ├── websocketService.js
│   │   │   │   ├── localStorageService.js
│   │   │   │   └── notificationService.js
│   │   │   ├── styles/
│   │   │   │   ├── global.css
│   │   │   │   ├── variables.css
│   │   │   │   ├── components/
│   │   │   │   │   ├── button.css
│   │   │   │   │   ├── card.css
│   │   │   │   │   └── modal.css
│   │   │   │   └── pages/
│   │   │   │       ├── home.css
│   │   │   │       └── dashboard.css
│   │   │   ├── assets/
│   │   │   │   ├── images/
│   │   │   │   │   ├── logo.png
│   │   │   │   │   └── bus-icon.svg
│   │   │   │   └── icons/
│   │   │   ├── config/
│   │   │   │   ├── env.js
│   │   │   │   └── routes.js
│   │   │   ├── App.jsx
│   │   │   ├── App.css
│   │   │   ├── index.js
│   │   │   └── index.css
│   │   ├── .env
│   │   ├── .env.development
│   │   ├── .env.production
│   │   ├── .gitignore
│   │   ├── package.json
│   │   ├── package-lock.json
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── driver-app/
│   │   ├── public/
│   │   ├── src/
│   │   │   ├── api/
│   │   │   │   ├── axiosConfig.js
│   │   │   │   ├── authApi.js
│   │   │   │   ├── locationApi.js
│   │   │   │   └── scheduleApi.js
│   │   │   ├── components/
│   │   │   │   ├── common/
│   │   │   │   │   ├── Header.jsx
│   │   │   │   │   ├── Footer.jsx
│   │   │   │   │   └── Loading.jsx
│   │   │   │   ├── auth/
│   │   │   │   │   └── LoginForm.jsx
│   │   │   │   ├── location/
│   │   │   │   │   ├── LocationTracker.jsx
│   │   │   │   │   ├── ManualLocationUpdate.jsx
│   │   │   │   │   └── LocationStatus.jsx
│   │   │   │   └── route/
│   │   │   │       ├── CurrentRoute.jsx
│   │   │   │       ├── NextStop.jsx
│   │   │   │       └── RouteProgress.jsx
│   │   │   ├── pages/
│   │   │   │   ├── Login.jsx
│   │   │   │   ├── Dashboard.jsx
│   │   │   │   ├── ActiveRoute.jsx
│   │   │   │   └── Profile.jsx
│   │   │   ├── store/
│   │   │   │   ├── store.js
│   │   │   │   └── slices/
│   │   │   │       ├── authSlice.js
│   │   │   │       ├── locationSlice.js
│   │   │   │       └── routeSlice.js
│   │   │   ├── hooks/
│   │   │   │   ├── useAuth.js
│   │   │   │   ├── useGeolocation.js
│   │   │   │   └── useWebSocket.js
│   │   │   ├── services/
│   │   │   │   ├── locationService.js
│   │   │   │   └── websocketService.js
│   │   │   ├── utils/
│   │   │   │   └── gpsUtils.js
│   │   │   ├── App.jsx
│   │   │   └── index.js
│   │   ├── package.json
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   └── admin-dashboard/
│       ├── public/
│       ├── src/
│       │   ├── api/
│       │   │   ├── axiosConfig.js
│       │   │   ├── authApi.js
│       │   │   ├── userApi.js
│       │   │   ├── busApi.js
│       │   │   ├── routeApi.js
│       │   │   ├── scheduleApi.js
│       │   │   ├── ticketApi.js
│       │   │   └── analyticsApi.js
│       │   ├── components/
│       │   │   ├── common/
│       │   │   │   ├── Sidebar.jsx
│       │   │   │   ├── Topbar.jsx
│       │   │   │   ├── Card.jsx
│       │   │   │   ├── Table.jsx
│       │   │   │   ├── Chart.jsx
│       │   │   │   └── Modal.jsx
│       │   │   ├── auth/
│       │   │   │   └── AdminLogin.jsx
│       │   │   ├── users/
│       │   │   │   ├── UserList.jsx
│       │   │   │   ├── UserForm.jsx
│       │   │   │   ├── UserDetails.jsx
│       │   │   │   └── UserFilters.jsx
│       │   │   ├── buses/
│       │   │   │   ├── BusList.jsx
│       │   │   │   ├── BusForm.jsx
│       │   │   │   ├── BusDetails.jsx
│       │   │   │   └── BusStatus.jsx
│       │   │   ├── routes/
│       │   │   │   ├── RouteList.jsx
│       │   │   │   ├── RouteForm.jsx
│       │   │   │   ├── RouteDetails.jsx
│       │   │   │   └── StopManager.jsx
│       │   │   ├── schedules/
│       │   │   │   ├── ScheduleList.jsx
│       │   │   │   ├── ScheduleForm.jsx
│       │   │   │   ├── ScheduleCalendar.jsx
│       │   │   │   └── ScheduleConflicts.jsx
│       │   │   ├── analytics/
│       │   │   │   ├── Dashboard.jsx
│       │   │   │   ├── RevenueChart.jsx
│       │   │   │   ├── UsageStats.jsx
│       │   │   │   ├── PopularRoutes.jsx
│       │   │   │   └── PerformanceMetrics.jsx
│       │   │   └── monitoring/
│       │   │       ├── LiveBusMonitor.jsx
│       │   │       ├── SystemHealth.jsx
│       │   │       └── AlertsPanel.jsx
│       │   ├── pages/
│       │   │   ├── Login.jsx
│       │   │   ├── Dashboard.jsx
│       │   │   ├── Users.jsx
│       │   │   ├── Buses.jsx
│       │   │   ├── Routes.jsx
│       │   │   ├── Schedules.jsx
│       │   │   ├── Tickets.jsx
│       │   │   ├── Analytics.jsx
│       │   │   ├── Monitoring.jsx
│       │   │   └── Settings.jsx
│       │   ├── store/
│       │   │   ├── store.js
│       │   │   └── slices/
│       │   │       ├── authSlice.js
│       │   │       ├── userSlice.js
│       │   │       ├── busSlice.js
│       │   │       ├── routeSlice.js
│       │   │       ├── scheduleSlice.js
│       │   │       └── analyticsSlice.js
│       │   ├── hooks/
│       │   │   ├── useAuth.js
│       │   │   ├── useTable.js
│       │   │   └── useChart.js
│       │   ├── utils/
│       │   │   ├── validators.js
│       │   │   ├── formatters.js
│       │   │   └── chartConfig.js
│       │   ├── styles/
│       │   │   ├── admin.css
│       │   │   └── dashboard.css
│       │   ├── App.jsx
│       │   └── index.js
│       ├── package.json
│       ├── Dockerfile
│       └── README.md
│
├── shared/
│   ├── common-models/
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── java/com/transport/common/
│   │   │           ├── dto/
│   │   │           │   ├── ApiResponse.java
│   │   │           │   ├── ErrorResponse.java
│   │   │           │   ├── PageResponse.java
│   │   │           │   └── ValidationError.java
│   │   │           ├── enums/
│   │   │           │   ├── UserRole.java
│   │   │           │   ├── TransactionStatus.java
│   │   │           │   └── NotificationType.java
│   │   │           └── constants/
│   │   │               ├── KafkaTopics.java
│   │   │               ├── ApiEndpoints.java
│   │   │               └── ErrorCodes.java
│   │   └── pom.xml
│   │
│   ├── common-utils/
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── java/com/transport/utils/
│   │   │           ├── DateUtils.java
│   │   │           ├── StringUtils.java
│   │   │           ├── JsonUtils.java
│   │   │           ├── ValidationUtils.java
│   │   │           └── EncryptionUtils.java
│   │   └── pom.xml
│   │
│   └── api-contracts/
│       ├── src/
│       │   └── main/
│       │       └── java/com/transport/api/
│       │           ├── user/
│       │           │   ├── UserDto.java
│       │           │   └── AuthDto.java
│       │           ├── ticket/
│       │           │   └── TicketDto.java
│       │           ├── schedule/
│       │           │   └── ScheduleDto.java
│       │           └── subscription/
│       │               └── SubscriptionDto.java
│       └── pom.xml
│
├── infrastructure/
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   ├── docker-compose.dev.yml
│   │   ├── docker-compose.prod.yml
│   │   ├── .env.example
│   │   └── nginx/
│   │       ├── nginx.conf
│   │       └── Dockerfile
│   │
│   ├── kubernetes/
│   │   ├── namespaces/
│   │   │   ├── dev-namespace.yaml
│   │   │   └── prod-namespace.yaml
│   │   ├── configmaps/
│   │   │   ├── api-gateway-config.yaml
│   │   │   ├── kafka-config.yaml
│   │   │   └── postgres-config.yaml
│   │   ├── secrets/
│   │   │   ├── db-secrets.yaml
│   │   │   ├── jwt-secrets.yaml
│   │   │   └── api-keys-secrets.yaml
│   │   ├── deployments/
│   │   │   ├── config-server-deployment.yaml
│   │   │   ├── service-registry-deployment.yaml
│   │   │   ├── api-gateway-deployment.yaml
│   │   │   ├── user-service-deployment.yaml
│   │   │   ├── ticket-service-deployment.yaml
│   │   │   ├── schedule-service-deployment.yaml
│   │   │   ├── geolocation-service-deployment.yaml
│   │   │   ├── subscription-service-deployment.yaml
│   │   │   ├── notification-service-deployment.yaml
│   │   │   ├── postgres-deployment.yaml
│   │   │   ├── mongodb-deployment.yaml
│   │   │   ├── kafka-deployment.yaml
│   │   │   ├── zookeeper-deployment.yaml
│   │   │   └── redis-deployment.yaml
│   │   ├── services/
│   │   │   ├── config-server-service.yaml
│   │   │   ├── service-registry-service.yaml
│   │   │   ├── api-gateway-service.yaml
│   │   │   ├── user-service-service.yaml
│   │   │   ├── ticket-service-service.yaml
│   │   │   ├── schedule-service-service.yaml
│   │   │   ├── geolocation-service-service.yaml
│   │   │   ├── subscription-service-service.yaml
│   │   │   ├── notification-service-service.yaml
│   │   │   ├── postgres-service.yaml
│   │   │   ├── mongodb-service.yaml
│   │   │   ├── kafka-service.yaml
│   │   │   └── redis-service.yaml
│   │   ├── ingress/
│   │   │   ├── ingress.yaml
│   │   │   └── ingress-tls.yaml
│   │   ├── persistent-volumes/
│   │   │   ├── postgres-pv.yaml
│   │   │   ├── mongodb-pv.yaml
│   │   │   └── kafka-pv.yaml
│   │   ├── hpa/
│   │   │   ├── user-service-hpa.yaml
│   │   │   ├── ticket-service-hpa.yaml
│   │   │   └── geolocation-service-hpa.yaml
│   │   └── networkpolicies/
│   │       ├── allow-gateway.yaml
│   │       └── allow-internal.yaml
│   │
│   └── terraform/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       ├── providers.tf
│       ├── modules/
│       │   ├── vpc/
│       │   │   ├── main.tf
│       │   │   ├── variables.tf
│       │   │   └── outputs.tf
│       │   ├── eks/
│       │   │   ├── main.tf
│       │   │   ├── variables.tf
│       │   │   └── outputs.tf
│       │   ├── rds/
│       │   │   ├── main.tf
│       │   │   ├── variables.tf
│       │   │   └── outputs.tf
│       │   └── s3/
│       │       ├── main.tf
│       │       ├── variables.tf
│       │       └── outputs.tf
│       ├── environments/
│       │   ├── dev/
│       │   │   ├── terraform.tfvars
│       │   │   └── backend.tf
│       │   └── prod/
│       │       ├── terraform.tfvars
│       │       └── backend.tf
│       └── README.md
│
├── monitoring/
│   ├── prometheus/
│   │   ├── prometheus.yml
│   │   ├── alert-rules.yml
│   │   └── Dockerfile
│   ├── grafana/
│   │   ├── provisioning/
│   │   │   ├── dashboards/
│   │   │   │   ├── dashboard.yml
│   │   │   │   ├── microservices-dashboard.json
│   │   │   │   ├── kafka-dashboard.json
│   │   │   │   └── system-metrics-dashboard.json
│   │   │   └── datasources/
│   │   │       └── datasource.yml
│   │   ├── grafana.ini
│   │   └── Dockerfile
│   ├── elasticsearch/
│   │   ├── elasticsearch.yml
│   │   └── Dockerfile
│   ├── logstash/
│   │   ├── logstash.conf
│   │   ├── pipelines.yml
│   │   └── Dockerfile
│   └── kibana/
│       ├── kibana.yml
│       └── Dockerfile
│
├── documentation/
│   ├── architecture/
│   │   ├── C4-model/
│   │   │   ├── context-diagram.puml
│   │   │   ├── container-diagram.puml
│   │   │   ├── component-diagram.puml
│   │   │   └── code-diagram.puml
│   │   ├── UML/
│   │   │   ├── class-diagrams/
│   │   │   │   ├── user-service-classes.puml
│   │   │   │   ├── ticket-service-classes.puml
│   │   │   │   └── subscription-service-classes.puml
│   │   │   ├── sequence-diagrams/
│   │   │   │   ├── authentication-flow.puml
│   │   │   │   ├── ticket-purchase-flow.puml
│   │   │   │   ├── bus-tracking-flow.puml
│   │   │   │   └── notification-flow.puml
│   │   │   └── deployment-diagram.puml
│   │   ├── BPMN/
│   │   │   ├── ticket-purchase-process.bpmn
│   │   │   ├── subscription-renewal-process.bpmn
│   │   │   └── notification-process.bpmn
│   │   ├── event-storming/
│   │   │   ├── domain-events.md
│   │   │   ├── aggregates.md
│   │   │   └── bounded-contexts.md
│   │   ├── architecture-decision-records/
│   │   │   ├── ADR-001-microservices-architecture.md
│   │   │   ├── ADR-002-kafka-messaging.md
│   │   │   ├── ADR-003-database-per-service.md
│   │   │   └── ADR-004-api-gateway-pattern.md
│   │   └── system-overview.md
│   │
│   ├── api-docs/
│   │   ├── swagger/
│   │   │   ├── user-service-api.yaml
│   │   │   ├── ticket-service-api.yaml
│   │   │   ├── schedule-service-api.yaml
│   │   │   ├── geolocation-service-api.yaml
│   │   │   ├── subscription-service-api.yaml
│   │   │   └── notification-service-api.yaml
│   │   ├── postman/
│   │   │   ├── Transport-System.postman_collection.json
│   │   │   └── environments/
│   │   │       ├── dev.postman_environment.json
│   │   │       └── prod.postman_environment.json
│   │   └── README.md
│   │
│   ├── deployment-guide/
│   │   ├── local-setup.md
│   │   ├── docker-deployment.md
│   │   ├── kubernetes-deployment.md
│   │   ├── cloud-deployment.md
│   │   └── troubleshooting.md
│   │
│   ├── developer-guide/
│   │   ├── getting-started.md
│   │   ├── coding-standards.md
│   │   ├── git-workflow.md
│   │   ├── testing-guidelines.md
│   │   └── contributing.md
│   │
│   └── user-manuals/
│       ├── passenger-manual.md
│       ├── driver-manual.md
│       └── admin-manual.md
│
├── scripts/
│   ├── setup/
│   │   ├── setup-dev-environment.sh
│   │   ├── install-dependencies.sh
│   │   └── setup-databases.sh
│   ├── deployment/
│   │   ├── deploy-all-services.sh
│   │   ├── deploy-single-service.sh
│   │   ├── rollback.sh
│   │   └── scale-services.sh
│   ├── database/
│   │   ├── seed-data.sh
│   │   ├── backup-databases.sh
│   │   ├── restore-databases.sh
│   │   └── migrations.sh
│   ├── testing/
│   │   ├── run-unit-tests.sh
│   │   ├── run-integration-tests.sh
│   │   ├── run-e2e-tests.sh
│   │   └── performance-tests.sh
│   ├── monitoring/
│   │   ├── health-check.sh
│   │   ├── generate-metrics-report.sh
│   │   └── alert-test.sh
│   └── utilities/
│       ├── generate-jwt-secret.sh
│       ├── cleanup-docker.sh
│       └── port-forward-services.sh
│
├── tests/
│   ├── e2e/
│   │   ├── cypress/
│   │   │   ├── e2e/
│   │   │   │   ├── auth.cy.js
│   │   │   │   ├── ticket-purchase.cy.js
│   │   │   │   ├── bus-tracking.cy.js
│   │   │   │   └── subscription.cy.js
│   │   │   ├── fixtures/
│   │   │   │   ├── users.json
│   │   │   │   └── tickets.json
│   │   │   ├── support/
│   │   │   │   ├── commands.js
│   │   │   │   └── e2e.js
│   │   │   └── cypress.config.js
│   │   └── package.json
│   │
│   ├── integration/
│   │   └── src/
│   │       └── test/
│   │           └── java/com/transport/integration/
│   │               ├── UserServiceIntegrationTest.java
│   │               ├── TicketServiceIntegrationTest.java
│   │               ├── ScheduleServiceIntegrationTest.java
│   │               └── KafkaIntegrationTest.java
│   │
│   └── performance/
│       ├── jmeter/
│       │   ├── ticket-purchase-load-test.jmx
│       │   ├── schedule-query-load-test.jmx
│       │   └── bus-tracking-load-test.jmx
│       └── k6/
│           ├── load-test.js
│           └── stress-test.js
│
├── .github/
│   └── workflows/
│       ├── ci-backend.yml
│       ├── ci-frontend.yml
│       ├── cd-dev.yml
│       ├── cd-prod.yml
│       ├── security-scan.yml
│       └── automated-tests.yml
│
├── .gitignore
├── .dockerignore
├── README.md
├── CONTRIBUTING.md
├── LICENSE
└── pom.xml (parent POM)
```
