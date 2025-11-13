# Subscription Service

Microservice de gestion des abonnements pour le système de transport urbain.

## Description

Le service Subscription gère :
- Les plans d'abonnement
- Les abonnements des utilisateurs
- Les paiements et renouvellements automatiques
- La génération et validation de QR codes
- L'historique des abonnements

## Technologies

- **Java 17+**
- **Spring Boot 3.4.4**
- **PostgreSQL**
- **Maven**
- **MapStruct** (mapping)
- **Lombok**
- **Stripe** (paiements)
- **ZXing** (QR codes)
- **Kafka** (événements, optionnel)
- **Swagger/OpenAPI** (documentation)

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- PostgreSQL 12+
- Kafka (optionnel, pour les événements)

## Configuration

### Variables d'environnement

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/urban_transport
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# Stripe
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Kafka (optionnel)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Eureka (optionnel)
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Scheduler
SCHEDULER_RENEWAL_ENABLED=true
SCHEDULER_RENEWAL_CRON=0 0 0 * * *
```

## Installation

### 1. Cloner le repository

```bash
git clone <repository-url>
cd Urban-Transport-System
```

### 2. Créer la base de données

Exécuter le script SQL fourni dans `script_sql_subscription.txt` :

```bash
psql -U postgres -d urban_transport -f script_sql_subscription.txt
```

### 3. Compiler le projet

```bash
cd backend
mvn clean install
```

### 4. Lancer le service

```bash
cd subscription-service
mvn spring-boot:run
```

Ou avec un profil spécifique :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Endpoints API

### Subscriptions

- `POST /api/subscriptions` - Créer un abonnement
- `GET /api/subscriptions/{id}` - Obtenir un abonnement
- `GET /api/subscriptions/user/{userId}` - Obtenir les abonnements d'un utilisateur
- `PUT /api/subscriptions/{id}/cancel` - Annuler un abonnement
- `PUT /api/subscriptions/{id}/renew` - Renouveler un abonnement
- `PUT /api/subscriptions/{id}/pause` - Mettre en pause un abonnement
- `PUT /api/subscriptions/{id}/resume` - Reprendre un abonnement
- `GET /api/subscriptions/{id}/qrcode` - Obtenir le QR code
- `POST /api/subscriptions/validate-qrcode` - Valider un QR code

### Plans

- `POST /api/plans` - Créer un plan
- `GET /api/plans` - Obtenir tous les plans
- `GET /api/plans/active` - Obtenir les plans actifs
- `GET /api/plans/{id}` - Obtenir un plan
- `GET /api/plans/code/{code}` - Obtenir un plan par code
- `PUT /api/plans/{id}` - Mettre à jour un plan
- `DELETE /api/plans/{id}` - Désactiver un plan

### Payments

- `POST /api/payments` - Traiter un paiement
- `GET /api/payments/subscription/{subscriptionId}` - Obtenir les paiements d'un abonnement
- `GET /api/payments/{id}` - Obtenir un paiement
- `POST /api/payments/{id}/refund` - Rembourser un paiement
- `POST /api/payments/webhook` - Webhook de paiement

## Documentation Swagger

Une fois le service démarré, accéder à :
- Swagger UI : http://localhost:8085/swagger-ui.html
- API Docs : http://localhost:8085/api-docs

## Tests

### Tests unitaires

```bash
mvn test
```

### Tests d'intégration

```bash
mvn verify
```

## Docker

### Build

```bash
docker build -t subscription-service:latest -f subscription-service/Dockerfile .
```

### Run

```bash
docker run -p 8085:8085 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/urban_transport \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  subscription-service:latest
```

## Architecture

### Structure des packages

```
com.transport.subscription
├── entity/              # Entités JPA
├── repository/          # Repositories
├── dto/                 # DTOs (request/response)
│   ├── request/
│   ├── response/
│   └── mapper/          # MapStruct mappers
├── service/             # Services (interfaces)
│   └── impl/            # Implémentations
├── controller/          # Controllers REST
├── exception/           # Exceptions custom
├── config/              # Configurations
├── scheduler/           # Jobs planifiés
├── event/               # Événements Kafka
└── util/                # Utilitaires
```

## Scheduler

Le service inclut un scheduler pour les renouvellements automatiques :
- Exécution quotidienne à minuit (configurable)
- Traite les abonnements actifs avec auto-renew activé
- Gère les échecs de paiement

## Événements Kafka

Le service publie les événements suivants (si Kafka activé) :
- `subscription-created`
- `subscription-renewed`
- `subscription-cancelled`
- `payment-processed`

## Monitoring

### Health Check

```bash
curl http://localhost:8085/actuator/health
```

### Metrics (Prometheus)

```bash
curl http://localhost:8085/actuator/prometheus
```

## Sécurité

- Les tokens de carte ne sont jamais loggés
- Validation des données d'entrée avec Jakarta Validation
- Gestion sécurisée des secrets via variables d'environnement
- Idempotence pour les paiements

## Contribution

1. Créer une branche depuis `main`
2. Faire les modifications
3. Ajouter des tests
4. Créer une pull request

## Licence

Apache 2.0

