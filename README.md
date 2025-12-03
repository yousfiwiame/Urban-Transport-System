<img src="https://github.com/user-attachments/assets/c245eed9-a723-4c88-96a7-37c845cbd44b" 
     alt="CityBus Logo" 
     width="100" 
     align="left" />

<h1>CityBus – Urban Transport System</h1>

<br clear="left"/>

A comprehensive microservices-based urban transportation management platform built with Spring Boot and React, featuring real-time vehicle tracking, ticket management, schedule coordination, and user subscriptions.

<p align="center">
  <img src="https://skillicons.dev/icons?i=gcp,kubernetes,terraform,githubactions,docker" alt="Tech Stack" />
</p>

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
  - [System Architecture Diagram](#system-architecture-diagram)
  - [Cloud Infrastructure Diagram](#cloud-infrastructure-diagram)
  - [CI/CD Pipeline Diagram](#cicd-pipeline-diagram)
  - [UML Diagrams](#uml-diagrams)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Clone the Repository](#clone-the-repository)
  - [Required Customizations](#required-customizations)
  - [Configuration](#configuration)
- [Local Development](#local-development)
  - [Backend Services](#backend-services)
  - [Frontend Application](#frontend-application)
- [Docker Deployment](#docker-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [CI/CD Pipeline](#cicd-pipeline)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Quick Reference](#quick-reference)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Urban Transport System is an enterprise-grade, cloud-native platform designed to modernize public transportation management. The system provides comprehensive solutions for:

- **User Management**: Secure authentication and authorization with JWT-based security
- **Ticket Management**: Digital ticketing system with support for single trips and passes
- **Schedule Management**: Real-time schedule updates and route planning
- **Geolocation Services**: Live vehicle tracking and location-based services
- **Subscription Management**: Flexible subscription plans for regular commuters
- **Notification Services**: Real-time notifications via email and push notifications
- **Real-time Updates**: WebSocket-based live data streaming for vehicle positions and schedules

The platform is built on a microservices architecture, ensuring scalability, fault tolerance, and independent service deployment. It leverages modern DevOps practices with containerization, orchestration, and automated CI/CD pipelines.

## Architecture

### System Architecture Diagram

The application diagram illustrates the high-level architecture of the Urban Transport System, showcasing the interaction between frontend, backend services, and infrastructure components.

![Application Architecture](https://github.com/user-attachments/assets/15b0712d-3334-4377-aa94-301ef51fd3f6)


**Description**: The system follows a microservices architecture pattern with the following components:

- **Frontend Layer**: React-based single-page application providing an intuitive user interface
- **API Gateway**: Central entry point for all client requests, handling routing, load balancing, and security
- **Service Registry**: Netflix Eureka-based service discovery for dynamic service registration and discovery
- **Config Server**: Centralized configuration management for all microservices
- **Microservices Layer**: Seven independent services handling specific business domains
- **Data Layer**: Polyglot persistence with PostgreSQL for relational data and MongoDB for geolocation data
- **Messaging Layer**: Apache Kafka for asynchronous event-driven communication
- **Caching Layer**: Redis for distributed caching and session management
- **Monitoring**: Zipkin for distributed tracing and observability

<h3 id="cloud-infrastructure-diagram" style="display: flex; align-items: center;">
  <img src="https://github.com/user-attachments/assets/70930924-a5e1-4ff7-9778-4b4e19717924" width="40" height="40" style="margin-right: 10px;">
  Cloud Infrastructure Diagram
</h3>

<p>
The cloud architecture diagram depicts the <strong>Google Cloud Platform (GCP)</strong> infrastructure setup, including networking, compute resources, and managed services.
</p>

<img width="3809" height="4140" alt="Diagramme Cloud" src="https://github.com/user-attachments/assets/fdec1e5f-8521-469b-9ed9-81977bcb6bcc" />


**Description**: The infrastructure is deployed on **Google Cloud Platform (GCP)** with the following components:

- **Google Kubernetes Engine (GKE)**: Managed Kubernetes cluster for container orchestration
- **Cloud SQL**: Managed PostgreSQL instances for relational databases
- **Memorystore**: Managed Redis instance for caching
- **Cloud Load Balancing**: Global load balancer for high availability

All infrastructure is provisioned using **Terraform**, enabling infrastructure as code practices.

<h3 id="cicd-pipeline-diagram" style="display: flex; align-items: center;">
  <img src="https://github.com/user-attachments/assets/9ec052f2-edd2-4d61-9d6b-115c1fb415fb" width="40" height="40" style="margin-right: 10px;">
  CI/CD Pipeline Diagram
</h3>

<p>
The pipeline architecture diagram illustrates the automated build, test, and deployment workflow.
</p>

![Pipeline Architecture](https://github.com/user-attachments/assets/b5698907-6e40-4504-94d7-0c94aae2bd75)


**Description**: The CI/CD pipeline is implemented using **GitHub Actions** with the following stages:

1. **Source Control**: Code changes trigger automated workflows
2. **Build Stage**: Parallel builds for backend (Maven) and frontend (npm)
3. **Test Stage**: Unit tests, integration tests, and E2E tests with Playwright
4. **Quality Gates**: Code quality checks and security scanning
5. **Containerization**: Docker image building and pushing to GitHub Container Registry
6. **Deployment**: Automated deployment to GKE with rolling updates
7. **Verification**: Health checks and smoke tests post-deployment

The pipeline supports multiple environments (staging, production) with environment-specific configurations.

### UML Diagrams

The UML diagrams provide detailed views of the system's static and dynamic behavior.

**Description**: The UML diagrams include:

- **Use Case Diagram**: Provides a high-level view of how users and external actors interact with the system and its core functionalities.

<img width="4602" height="4122" alt="DiagrammeDesCasUtilisation" src="https://github.com/user-attachments/assets/660d174c-734e-48ad-b569-529395603712" />

- **Class Diagram**: Defines the domain model of each microservice, showing key entities, attributes, and relationships.

![DiagrammeDeClasse](https://github.com/user-attachments/assets/f569d562-732b-4f03-94b0-cb3b896a8608)

- **Sequence Diagrams**: Illustrate the inter-service communication flow for essential use cases across the microservices.Inter-service communication flows for key use cases

<img width="858" height="782" alt="DiagrammeSéquence1" src="https://github.com/user-attachments/assets/b15bbf12-0427-4154-87ef-ec52c716d608" />

<img width="653" height="548" alt="DiagrammeSéquence2" src="https://github.com/user-attachments/assets/bbe83fe8-5f89-4674-aa49-3118a0bf3647" />

<img width="809" height="738" alt="DiagrammeSéquence3" src="https://github.com/user-attachments/assets/3ae27208-19d4-4667-a586-29fafe7a4cf2" />

<img width="756" height="548" alt="DiagrammeSéquence4" src="https://github.com/user-attachments/assets/4fa1b902-98e9-4de0-835b-c47a2fa1ed14" />

These diagrams serve as technical documentation for understanding our system design and implementation details.

## Technology Stack

### Backend
<p>
  <img src="https://skillicons.dev/icons?i=spring,java,maven,kafka,redis" alt="Backend Technologies" />
</p>

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Maven
- **API Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka
- **Configuration**: Spring Cloud Config
- **Security**: Spring Security + JWT
- **Messaging**: Apache Kafka
- **Caching**: Redis
- **Tracing**: Zipkin (OpenTelemetry)

### Frontend
<p>
  <img src="https://skillicons.dev/icons?i=react,ts,vite,tailwind,playwright" alt="Frontend Technologies" />
</p>

- **Framework**: React 18
- **Language**: TypeScript
- **Build Tool**: Vite
- **State Management**: Zustand
- **Data Fetching**: TanStack Query (React Query)
- **Routing**: React Router v6
- **UI Framework**: Tailwind CSS
- **Maps**: Leaflet + React Leaflet
- **Real-time**: STOMP over WebSocket
- **Testing**: Playwright

### Databases
<p>
  <img src="https://skillicons.dev/icons?i=postgres,mongodb,redis" alt="Databases" />
</p>

- **Relational**: PostgreSQL 15
- **Document**: MongoDB 7.0
- **Cache**: Redis 7

### Infrastructure
<p>
  <img src="https://skillicons.dev/icons?i=docker,kubernetes,gcp,terraform,githubactions" alt="Infrastructure" />
</p>

- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Cloud Provider**: Google Cloud Platform (GKE)
- **IaC**: Terraform
- **CI/CD**: GitHub Actions
- **Container Registry**: GitHub Container Registry

## Project Structure

```
Urban-Transport-System/
│
├── backend/                              # Backend microservices (Java Spring Boot)
│   ├── pom.xml                           # Parent Maven POM for all backend microservices
│   ├── api-gateway/                      # API Gateway service (routing, authentication, filtering)
│   ├── config-server/                    # Centralized configuration server (Spring Cloud Config)
│   ├── service-registry/                 # Eureka service registry for service discovery
│   ├── user-service/                     # User management, authentication & roles
│   ├── ticket-service/                   # Ticket purchasing, validation & QR generation
│   ├── schedule-service/                 # Management of bus schedules and routes
│   ├── geolocation-service/              # Real-time vehicle tracking & GPS updates
│   ├── subscription-service/             # Subscription plans & renewal management
│   └── notification-service/             # Email, SMS, and push notification handling
│
├── frontend/                             # React frontend application
│   ├── src/
│   │   ├── components/                   # Reusable UI components
│   │   ├── pages/                        # Main page components
│   │   ├── services/                     # API services (Axios / Fetch wrappers)
│   │   ├── hooks/                        # Custom React hooks
│   │   ├── store/                        # State management (Redux/Zustand)
│   │   └── types/                        # TypeScript interfaces & types
│   ├── e2e/                              # End-to-end Playwright tests
│   └── public/                           # Static files (favicon, index.html)
│
├── config-repo/                          # Centralized configuration files for Config Server
│   ├── api-gateway.yml
│   ├── user-service.yml
│   └── ...                               # Other microservice configuration files
│
├── k8s/                                  # Kubernetes manifests for deployment
│   ├── namespace.yaml                    # Kubernetes namespace definition
│   ├── configmap.yaml                    # Shared configuration for services
│   ├── secrets.yaml                      # Sensitive environment variables
│   ├── service-registry.yaml             # Eureka deployment & service
│   ├── backend-services.yaml             # Combined backend services (optional)
│   ├── user-service.yaml                 # Deployment & service for User microservice
│   ├── api-gateway.yaml                  # API Gateway deployment & service
│   └── frontend.yaml                     # Frontend deployment & service
│
├── terraform/                            # Infrastructure as Code (GCP)
│   ├── provider.tf                       # GCP provider setup
│   ├── gke.tf                            # GKE cluster creation
│   ├── database.tf                       # Cloud SQL PostgreSQL instances
│   ├── redis.tf                          # Memorystore Redis instance
│   ├── variables.tf                      # Shared variables
│   └── outputs.tf                        # Exported Terraform outputs
│
├── .github/
│   └── workflows/                        # CI/CD pipelines (GitHub Actions)
│       ├── ci-cd.yml                     # Full CI/CD pipeline (build + deploy)
│       ├── backend-ci.yml                # Backend build & test pipeline
│       ├── frontend-ci.yml               # Frontend build & test pipeline
│       └── docker-build.yml              # Docker image build & push
│
├── docker-compose.yml                    # Local development setup for services
│
└── README.md                             # Project documentation and setup instructions

```

## Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Node.js 20** or higher
- **Maven 3.8+** for building backend services
- **Docker 24+** and **Docker Compose 2.20+**
- **Kubernetes CLI (kubectl) 1.27+** (for Kubernetes deployment)
- **Git** for version control
- **Terraform 1.5+** (for infrastructure provisioning)
- **Google Cloud SDK** (for GCP deployment)

### Optional Tools
- **IntelliJ IDEA** or **Eclipse** for backend development
- **Visual Studio Code** for frontend development
- **Postman** for API testing
- **pgAdmin** for database management

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yousfiwiame/Urban-Transport-System.git
cd Urban-Transport-System
```

### Required Customizations

Before deploying or running the application, you must customize the following files with your own credentials and settings:

#### 1. Terraform Configuration

**File**: `terraform/terraform.tfvars`

Replace the following values:
- `project_id`: Your GCP project ID
- `region`: Your preferred GCP region
- `db_password`: A strong database password
- `mongodb_connection_string`: Your MongoDB connection string
- `image_prefix`: Your GitHub username or organization

```hcl
project_id                 = "your-gcp-project-id"
region                     = "us-central1"
db_password                = "your-secure-db-password"
mongodb_connection_string  = "your-mongodb-connection-string"
image_prefix               = "your-github-username/urban-transport"
```

#### 2. Kubernetes Secrets

**File**: `k8s/secrets.yaml`

Update the **base64-encoded** secrets with your own values:
```bash
# Generate base64 encoded values
echo -n "your-db-password" | base64
echo -n "your-mongodb-connection-string" | base64
echo -n "your-jwt-secret" | base64
```

Replace in `k8s/secrets.yaml`:
- `DB_PASSWORD`: Base64-encoded database password
- `MONGODB_CONNECTION_STRING`: Base64-encoded MongoDB connection string
- `JWT_SECRET`: Base64-encoded JWT signing secret (use a long random string)

**Important**: For production, never commit secrets to Git. Use the command-line approach shown in the Kubernetes deployment section instead.

#### 3. GitHub Actions CI/CD

**File**: `.github/workflows/ci-cd.yml`

Update the following values:
- Lines 204, 244: `urban-transport-cluster` - Replace with your GKE cluster name if different
- Lines 206, 246: `urban-transport-system-xxxxxx` - Replace with your GCP project ID
- Lines 205, 245: `us-central1` - Replace with your GCP region if different

**GitHub Secrets Required** (Settings → Secrets → Actions):
- `GCP_SA_KEY`: Your Google Cloud service account key JSON
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret (minimum 32 characters)
- `MONGODB_CONNECTION_STRING`: MongoDB connection string
- `GITHUB_TOKEN`: Automatically provided by GitHub

To create a GCP service account key:
```bash
gcloud iam service-accounts create github-actions \
  --display-name="GitHub Actions"

gcloud projects add-iam-policy-binding your-project-id \
  --member="serviceAccount:github-actions@your-project-id.iam.gserviceaccount.com" \
  --role="roles/container.developer"

gcloud iam service-accounts keys create key.json \
  --iam-account=github-actions@your-project-id.iam.gserviceaccount.com
```

#### 4. Docker Compose (Optional)

**File**: `docker-compose.yml`

For production use, update default passwords:
- Lines 12, 32, 54, 75, 96: `POSTGRES_PASSWORD`
- Consider using Docker secrets or environment variables for sensitive data

#### 5. Frontend Environment Variables

**File**: `frontend/.env` (create this file)

```env
VITE_API_GATEWAY_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

For production deployment:
```env
VITE_API_GATEWAY_URL=https://your-api-gateway-domain.com
VITE_WS_URL=wss://your-api-gateway-domain.com/ws
```

### Configuration

#### Backend Configuration

The backend services use Spring Cloud Config for centralized configuration. Configuration files are located in the `config-repo/` directory.

For local development, update the following files:

1. **Database Configuration**: Update database URLs and credentials in each service's configuration file:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/user_db
       username: postgres
       password: postgres
   ```

2. **Kafka Configuration**: Ensure Kafka connection settings match your local setup:
   ```yaml
   spring:
     kafka:
       bootstrap-servers: localhost:9092
   ```

3. **Redis Configuration**: Update Redis connection details:
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
   ```

#### Frontend Configuration

Create a `.env` file in the `frontend/` directory:

```env
VITE_API_GATEWAY_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

## Local Development

### Backend Services

#### Build All Services

```bash
cd backend
mvn clean install
```

#### Run Individual Services

Each service can be started independently:

```bash
# Start Service Registry (Eureka)
cd backend/service-registry
mvn spring-boot:run

# Start Config Server
cd backend/config-server
mvn spring-boot:run

# Start User Service
cd backend/user-service
mvn spring-boot:run

# Start API Gateway
cd backend/api-gateway
mvn spring-boot:run
```

**Note**: Start services in the following order to ensure proper dependency resolution:
1. Service Registry (port 8761)
2. Config Server (port 8888)
3. Business services (8081-8086)
4. API Gateway (port 8080)

#### Run Tests

```bash
# Run all tests
cd backend
mvn test

# Run tests for specific service
cd backend/user-service
mvn test

# Run integration tests
cd backend
mvn verify
```

### Frontend Application

#### Install Dependencies

```bash
cd frontend
npm install
```

#### Start Development Server

```bash
npm run dev
```

The application will be available at `http://localhost:3000`

#### Build for Production

```bash
npm run build
```

#### Run Linter

```bash
npm run lint
```

#### Run E2E Tests

```bash
# Install Playwright browsers (first time only)
npx playwright install

# Run E2E tests
npx playwright test

# Run tests in UI mode
npx playwright test --ui
```

<h3 id="docker-deployment" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=docker" alt="Docker" width="32" height="32" style="margin-right: 10px;">
  Docker Deployment
</h3>

The entire system can be deployed using Docker Compose for local testing or development environments.

### Start All Services

```bash
docker-compose up -d
```

This command will:
- Build Docker images for all services
- Start all databases (PostgreSQL, MongoDB)
- Start infrastructure services (Redis, Kafka, Zookeeper, Zipkin)
- Start all microservices
- Start the frontend application

### View Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f user-service

# View last 100 lines
docker-compose logs --tail=100 -f
```

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Volumes

```bash
docker-compose down -v
```

### Build and Push Docker Images

```bash
# Build all images
docker-compose build

# Build specific service
docker-compose build user-service

# Push to container registry (requires authentication)
docker-compose push
```

### Access Services

Once all services are running:

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Service Registry**: http://localhost:8761
- **Config Server**: http://localhost:8888
- **Zipkin Dashboard**: http://localhost:9411
- **User Service**: http://localhost:8081
- **Ticket Service**: http://localhost:8082
- **Schedule Service**: http://localhost:8083
- **Geolocation Service**: http://localhost:8084
- **Subscription Service**: http://localhost:8085
- **Notification Service**: http://localhost:8086

<h3 id="kubernetes-deployment" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=kubernetes" alt="Kubernetes" width="32" height="32" style="margin-right: 10px;">
  Kubernetes Deployment
</h3>

### Prerequisites

1. A running Kubernetes cluster (local or cloud-based)
2. `kubectl` configured to interact with your cluster
3. Docker images pushed to a container registry

### Deploy to Local Kubernetes (Minikube/Kind)

```bash
# Start Minikube (if using Minikube)
minikube start

# Apply Kubernetes manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/service-registry.yaml
kubectl apply -f k8s/backend-services.yaml
kubectl apply -f k8s/user-service.yaml
kubectl apply -f k8s/api-gateway.yaml
kubectl apply -f k8s/frontend.yaml

# Check deployment status
kubectl get pods -n urban-transport
kubectl get svc -n urban-transport
```

### Deploy to Google Kubernetes Engine (GKE)

<h4 id="kubernetes-deployment" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=terraform" alt="Terraform" width="32" height="32" style="margin-right: 10px;">
  1. Provision Infrastructure with Terraform
</h4>

```bash
cd terraform

# Initialize Terraform
terraform init

# Review planned changes
terraform plan

# Apply infrastructure changes
terraform apply
```

#### 2. Configure kubectl for GKE

```bash
gcloud container clusters get-credentials urban-transport-cluster \
  --region us-central1 \
  --project urban-transport-system-xxxxxx
```

#### 3. Create Kubernetes Secrets

```bash
kubectl create secret generic app-secrets \
  --from-literal=DB_PASSWORD=your_db_password \
  --from-literal=JWT_SECRET=your_jwt_secret \
  --from-literal=MONGODB_CONNECTION_STRING=your_mongo_connection_string \
  --namespace=urban-transport

kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=your_github_username \
  --docker-password=your_github_token \
  --namespace=urban-transport
```

#### 4. Deploy Application

```bash
cd k8s

kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f service-registry.yaml
kubectl apply -f backend-services.yaml
kubectl apply -f user-service.yaml
kubectl apply -f api-gateway.yaml
kubectl apply -f frontend.yaml
```

#### 5. Monitor Deployment

```bash
# Watch pod status
kubectl get pods -n urban-transport -w

# Check service endpoints
kubectl get svc -n urban-transport

# View logs for specific pod
kubectl logs -f <pod-name> -n urban-transport

# Describe pod for troubleshooting
kubectl describe pod <pod-name> -n urban-transport
```

#### 6. Access Application

```bash
# Get LoadBalancer IP for frontend
kubectl get svc frontend -n urban-transport

# Get LoadBalancer IP for API Gateway
kubectl get svc api-gateway -n urban-transport
```

### Scale Deployments

```bash
# Scale user service to 3 replicas
kubectl scale deployment user-service --replicas=3 -n urban-transport

# Auto-scale based on CPU
kubectl autoscale deployment user-service \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n urban-transport
```

### Update Deployment

```bash
# Update image
kubectl set image deployment/user-service \
  user-service=ghcr.io/your-username/user-service:v2.0.0 \
  -n urban-transport

# Check rollout status
kubectl rollout status deployment/user-service -n urban-transport

# Rollback if needed
kubectl rollout undo deployment/user-service -n urban-transport
```

<h2 id="cicd-pipeline" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=githubactions" alt="GitHub Actions" width="32" height="32" style="margin-right: 10px;">
  CI/CD Pipeline
</h2>

The project uses GitHub Actions for continuous integration and deployment.

### Pipeline Stages

1. **Backend Testing**: Runs Maven tests for all services
2. **Backend Build**: Compiles and packages JAR files
3. **Frontend Testing**: Runs linting and builds
4. **E2E Testing**: Executes Playwright tests
5. **Docker Build**: Builds and pushes Docker images to GHCR
6. **Deploy to Staging**: Automatic deployment on `develop` branch
7. **Deploy to Production**: Automatic deployment on `main` branch

### Trigger Pipeline

```bash
# Push to main branch triggers production deployment
git push origin main

# Push to develop branch triggers staging deployment
git push origin develop

# Manual deployment
gh workflow run ci-cd.yml -f environment=production
```

### View Pipeline Status

- Navigate to the **Actions** tab in your GitHub repository
- Monitor build logs and deployment status
- View test reports and artifacts

### Required GitHub Secrets

Configure the following secrets in your repository settings:

- `GCP_SA_KEY`: Google Cloud service account key (JSON)
- `DB_PASSWORD`: Database password for production
- `JWT_SECRET`: JWT signing secret
- `MONGODB_CONNECTION_STRING`: MongoDB connection string
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

## Testing

<h3 id="testing" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=maven" alt="Maven" width="32" height="32" style="margin-right: 10px;">
  Backend Testing
</h3>

```bash
# Run unit tests
cd backend
mvn test

# Run integration tests
mvn verify

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
open backend/user-service/target/site/jacoco/index.html
```

<h3 id="testing" style="display: flex; align-items: center;">
  <img src="https://skillicons.dev/icons?i=playwright" alt="Playwright" width="32" height="32" style="margin-right: 10px;">
  Frontend Testing
</h3>

```bash
cd frontend

# Run linter
npm run lint

# Run E2E tests
npx playwright test

# Run E2E tests with UI
npx playwright test --ui

# Run specific test file
npx playwright test tests/login.spec.ts

# Generate test report
npx playwright show-report
```

### API Testing

Use the provided Postman collection or test endpoints directly:

```bash
# Health check
curl http://localhost:8080/actuator/health

# User registration
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# User login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

## API Documentation

### Service Endpoints

#### User Service (8081)
- `POST /api/users/register` - User registration
- `POST /api/users/login` - User authentication
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

#### Ticket Service (8082)
- `POST /api/tickets/purchase` - Purchase ticket
- `GET /api/tickets` - Get user tickets
- `POST /api/tickets/{id}/validate` - Validate ticket

#### Schedule Service (8083)
- `GET /api/schedules` - Get all schedules
- `GET /api/schedules/{id}` - Get schedule by ID
- `GET /api/routes` - Get all routes

#### Geolocation Service (8084)
- `GET /api/geolocation/vehicles` - Get all vehicle positions
- `GET /api/geolocation/vehicles/{id}` - Get specific vehicle location
- `WS /ws/locations` - WebSocket for real-time updates

#### Subscription Service (8085)
- `POST /api/subscriptions` - Create subscription
- `GET /api/subscriptions` - Get user subscriptions
- `PUT /api/subscriptions/{id}/cancel` - Cancel subscription

#### Notification Service (8086)
- `GET /api/notifications` - Get user notifications
- `PUT /api/notifications/{id}/read` - Mark notification as read

### API Gateway Routes

All client requests should go through the API Gateway at `http://localhost:8080`

The gateway automatically routes requests to appropriate services based on path prefixes.

## Quick Reference

### Essential Commands Summary

#### Local Development
```bash
# Backend
cd backend && mvn clean install          # Build all services
cd backend/service-registry && mvn spring-boot:run   # Start Eureka
cd backend/user-service && mvn spring-boot:run       # Start User Service
cd backend && mvn test                   # Run all tests

# Frontend
cd frontend && npm install               # Install dependencies
npm run dev                              # Start dev server
npm run build                            # Build for production
npm run lint                             # Run linter
npx playwright test                      # Run E2E tests
```

#### Docker
```bash
docker-compose up -d                     # Start all services
docker-compose down                      # Stop all services
docker-compose down -v                   # Stop and remove volumes
docker-compose logs -f <service>         # View logs
docker-compose build                     # Build images
docker-compose ps                        # List running services
```

#### Kubernetes
```bash
# Deploy
kubectl apply -f k8s/                    # Apply all manifests
kubectl get pods -n urban-transport      # Check pod status
kubectl get svc -n urban-transport       # Check services
kubectl logs -f <pod-name> -n urban-transport   # View logs

# Scale
kubectl scale deployment user-service --replicas=3 -n urban-transport

# Update
kubectl set image deployment/user-service user-service=ghcr.io/user/image:v2 -n urban-transport
kubectl rollout status deployment/user-service -n urban-transport
kubectl rollout undo deployment/user-service -n urban-transport
```

#### Terraform
```bash
cd terraform
terraform init                           # Initialize Terraform
terraform plan                           # Preview changes
terraform apply                          # Apply infrastructure
terraform destroy                        # Destroy infrastructure
terraform output                         # View outputs
```

#### GKE Deployment
```bash
# Authenticate
gcloud auth login
gcloud config set project your-project-id

# Configure kubectl
gcloud container clusters get-credentials urban-transport-cluster \
  --region us-central1 --project your-project-id

# Create secrets
kubectl create secret generic app-secrets \
  --from-literal=DB_PASSWORD=your-password \
  --from-literal=JWT_SECRET=your-secret \
  --from-literal=MONGODB_CONNECTION_STRING=your-connection-string \
  --namespace=urban-transport

# Deploy
kubectl apply -f k8s/ -n urban-transport
```

#### Testing
```bash
# Backend
mvn test                                 # Unit tests
mvn verify                               # Integration tests
mvn clean test jacoco:report             # With coverage

# Frontend
npm run lint                             # Linting
npx playwright test                      # E2E tests
npx playwright test --ui                 # E2E with UI
npx playwright show-report               # View report

# API
curl http://localhost:8080/actuator/health   # Health check
```

#### Troubleshooting
```bash
# Docker
docker-compose logs -f --tail=100        # View recent logs
docker system prune -a                   # Clean up Docker

# Kubernetes
kubectl describe pod <pod-name> -n urban-transport    # Pod details
kubectl get events -n urban-transport --sort-by='.lastTimestamp'
kubectl exec -it <pod-name> -n urban-transport -- /bin/sh   # Access pod

# Services
kubectl port-forward svc/api-gateway 8080:8080 -n urban-transport
```

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License. See the LICENSE file for details.

---

**Built with ❤️ by City Bus Team**  
**Contact**: [wiame.yousfi22@gmail.com](mailto:wiame.yousfi22@gmail.com)  
**Repository**: [https://github.com/yousfiwiame/Urban-Transport-System](https://github.com/yousfiwiame/Urban-Transport-System)
