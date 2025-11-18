# Subscription Service

Microservice de gestion des abonnements pour le système de transport urbain. Ce service gère les plans d'abonnement, les abonnements utilisateurs, les paiements, les renouvellements automatiques et la génération de QR codes.

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [Documentation API](#documentation-api)
- [Tests](#tests)
- [Docker](#docker)
- [Architecture](#architecture)
- [Sécurité](#sécurité)
- [Monitoring](#monitoring)

## 🎯 Vue d'ensemble

Le **Subscription Service** est un microservice Spring Boot qui gère l'ensemble du cycle de vie des abonnements de transport :

- **Plans d'abonnement** : Création, modification et gestion des plans (mensuel, annuel, etc.)
- **Abonnements** : Création, activation, pause, reprise, annulation et renouvellement
- **Paiements** : Traitement des paiements via Stripe, remboursements, gestion des échecs
- **QR Codes** : Génération et validation de QR codes pour l'accès au transport
- **Renouvellements automatiques** : Scheduler pour gérer les renouvellements périodiques
- **Historique** : Traçabilité complète des changements d'état

## ✨ Fonctionnalités

### Plans d'abonnement
- ✅ Création et modification de plans
- ✅ Désactivation (soft delete) de plans
- ✅ Recherche par ID ou code
- ✅ Filtrage des plans actifs

### Abonnements
- ✅ Création d'abonnement avec paiement initial
- ✅ Gestion des statuts : PENDING, ACTIVE, PAUSED, CANCELLED, EXPIRED
- ✅ Pause et reprise d'abonnement
- ✅ Annulation avec option de remboursement
- ✅ Renouvellement manuel ou automatique
- ✅ Retry de paiement pour abonnements en échec
- ✅ Génération de QR code unique par abonnement
- ✅ Validation de QR code

### Paiements
- ✅ Traitement de paiements via Stripe (production) ou Mock (développement)
- ✅ Support de multiples méthodes de paiement (CARD, BANK_TRANSFER, WALLET, etc.)
- ✅ Gestion des remboursements
- ✅ Idempotence des paiements
- ✅ Webhooks Stripe pour les événements de paiement
- ✅ Historique complet des transactions

### Scheduler
- ✅ Renouvellement automatique quotidien des abonnements
- ✅ Gestion des échecs de paiement
- ✅ Expiration automatique des abonnements

## 🛠 Technologies

- **Java 17+** - Langage de programmation
- **Spring Boot 3.4.4** - Framework principal
- **Spring Data JPA** - Accès aux données
- **PostgreSQL 15+** - Base de données relationnelle
- **Maven** - Gestion des dépendances et build
- **MapStruct** - Mapping DTO/Entity
- **Lombok** - Réduction du code boilerplate
- **Stripe Java SDK** - Intégration paiements
- **ZXing** - Génération de QR codes
- **Swagger/OpenAPI 3** - Documentation API
- **Spring Actuator** - Monitoring et métriques
- **TestContainers** - Tests d'intégration avec containers Docker

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java 17 ou supérieur** ([Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.org/))
- **Maven 3.6+** ([Download Maven](https://maven.apache.org/download.cgi))
- **PostgreSQL 12+** ([Download PostgreSQL](https://www.postgresql.org/download/))
- **Docker & Docker Compose** (optionnel, pour les tests et déploiement) ([Download Docker](https://www.docker.com/get-started))
- **Git** ([Download Git](https://git-scm.com/downloads))

### Vérification des prérequis

```bash
# Vérifier Java
java -version  # Doit afficher version 17 ou supérieure

# Vérifier Maven
mvn -version   # Doit afficher version 3.6 ou supérieure

# Vérifier PostgreSQL
psql --version # Doit afficher version 12 ou supérieure

# Vérifier Docker (optionnel)
docker --version
docker-compose --version
```

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone <repository-url>
cd Urban-Transport-System
```

### 2. Créer la base de données

#### Option A : Avec PostgreSQL local

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE subscription_service;

# Exécuter les migrations Flyway
cd backend/subscription-service
psql -U postgres -d subscription_service -f src/main/resources/db/migration/V1__create_subscription_tables.sql
```

#### Option B : Avec Docker Compose

```bash
cd backend/subscription-service
docker-compose up -d postgres
```

Les migrations Flyway s'exécutent automatiquement au démarrage du service.

### 3. Compiler le projet

```bash
cd backend/subscription-service
mvn clean install
```

## ⚙️ Configuration

### Variables d'environnement

Le service utilise des variables d'environnement pour la configuration. Créez un fichier `.env` ou configurez-les dans votre système :

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/subscription_service
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=akb

# Server Configuration
SERVER_PORT=8085
SPRING_PROFILES_ACTIVE=dev

# Stripe Configuration (Production)
STRIPE_SECRET_KEY=sk_test_...  # Clé secrète Stripe
STRIPE_WEBHOOK_SECRET=whsec_... # Secret webhook Stripe

# Scheduler Configuration
SCHEDULER_RENEWAL_ENABLED=true
SCHEDULER_RENEWAL_CRON=0 0 0 * * *  # Tous les jours à minuit

# Kafka (Optionnel)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Eureka (Optionnel)
EUREKA_SERVER_URL=http://localhost:8761/eureka/
```

### Fichiers de configuration

Le service utilise des profils Spring Boot :

- **`application.yml`** - Configuration par défaut
- **`application-dev.yml`** - Configuration développement (utilise MockPaymentGateway)
- **`application-prod.yml`** - Configuration production (utilise StripePaymentGateway)

### Profils disponibles

- **`dev`** : Mode développement avec MockPaymentGateway (pas besoin de clés Stripe)
- **`prod`** : Mode production avec StripePaymentGateway (nécessite clés Stripe)

## 🏃 Démarrage

### Option 1 : Avec Maven (Recommandé pour développement)

```bash
cd backend/subscription-service

# Mode développement (Mock Payment Gateway)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Mode production (Stripe Payment Gateway)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Option 2 : Avec le JAR compilé

```bash
cd backend/subscription-service

# Compiler
mvn clean package -DskipTests

# Exécuter
java -jar target/subscription-service.jar --spring.profiles.active=dev
```

### Option 3 : Avec Docker Compose

```bash
cd backend/subscription-service

# Démarrer tous les services (PostgreSQL + Subscription Service)
docker-compose up -d

# Voir les logs
docker-compose logs -f subscription-service

# Arrêter
docker-compose down
```

### Option 4 : Build et run Docker manuel

```bash
# Build l'image
docker build -t subscription-service:latest -f backend/subscription-service/Dockerfile .

# Run le container
docker run -p 8085:8085 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/subscription_service \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e SPRING_PROFILES_ACTIVE=dev \
  subscription-service:latest
```

### Vérification du démarrage

Une fois le service démarré, vous pouvez vérifier qu'il fonctionne :

```bash
# Health check
curl http://localhost:8085/actuator/health

# Swagger UI
# Ouvrir dans le navigateur : http://localhost:8085/swagger-ui.html
```

## 📚 Documentation API

La documentation complète des endpoints est disponible dans **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)**.

### Accès rapide

- **Swagger UI** : http://localhost:8085/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8085/api-docs
- **Health Check** : http://localhost:8085/actuator/health

## 🧪 Tests

### Exécuter tous les tests

```bash
cd backend/subscription-service
mvn test
```

### Tests unitaires uniquement

```bash
mvn test -Dtest=*Test
```

### Tests d'intégration uniquement

```bash
mvn test -Dtest=*IntegrationTest
```

### Tests avec couverture

```bash
mvn clean test jacoco:report
# Rapport disponible dans : target/site/jacoco/index.html
```

## 🐳 Docker

### Build de l'image

```bash
# Depuis la racine du projet
docker build -t subscription-service:latest -f backend/subscription-service/Dockerfile .

# Depuis le dossier subscription-service
docker build -t subscription-service:latest -f Dockerfile ../../
```

### Run avec Docker Compose

```bash
cd backend/subscription-service
docker-compose up -d
```

Le `docker-compose.yml` inclut :
- **subscription-service** : Le microservice
- **postgres** : Base de données PostgreSQL

### Variables d'environnement Docker

Vous pouvez surcharger les variables dans `docker-compose.yml` ou via un fichier `.env` :

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=dev
  - DATABASE_URL=jdbc:postgresql://postgres:5432/subscription_service
  - STRIPE_SECRET_KEY=${STRIPE_SECRET_KEY:-}
```

## 🏗 Architecture

### Structure des packages

```
com.transport.subscription
├── controller/          # Controllers REST (API endpoints)
│   ├── PlanController.java
│   ├── SubscriptionController.java
│   └── PaymentController.java
├── service/             # Interfaces de services
│   ├── PlanService.java
│   ├── SubscriptionService.java
│   ├── PaymentService.java
│   └── PaymentGateway.java
├── service/impl/        # Implémentations des services
│   ├── PlanServiceImpl.java
│   ├── SubscriptionServiceImpl.java
│   ├── PaymentServiceImpl.java
│   ├── MockPaymentGateway.java      # Mock pour dev
│   └── StripePaymentGateway.java    # Stripe pour prod
├── entity/              # Entités JPA
│   ├── SubscriptionPlan.java
│   ├── Subscription.java
│   ├── SubscriptionPayment.java
│   ├── SubscriptionHistory.java
│   └── enums/           # Énumérations
├── repository/          # Repositories JPA
│   ├── SubscriptionPlanRepository.java
│   ├── SubscriptionRepository.java
│   ├── SubscriptionPaymentRepository.java
│   └── SubscriptionHistoryRepository.java
├── dto/                 # Data Transfer Objects
│   ├── request/         # DTOs de requête
│   ├── response/        # DTOs de réponse
│   └── mapper/          # MapStruct mappers
├── exception/           # Exceptions personnalisées
│   └── GlobalExceptionHandler.java
├── config/              # Configurations Spring
│   ├── DatabaseConfig.java
│   ├── SchedulerConfig.java
│   └── SwaggerConfig.java
├── scheduler/           # Jobs planifiés
│   └── SubscriptionRenewalScheduler.java
├── event/               # Événements Kafka (optionnel)
└── util/                # Utilitaires
    ├── DateUtil.java
    └── PriceCalculator.java
```

### Flux de données

```
Client (Frontend)
    ↓
REST Controller
    ↓
Service Layer
    ↓
Repository Layer
    ↓
PostgreSQL Database
```

### Gestion des paiements

```
PaymentService
    ↓
PaymentGateway (Interface)
    ├── MockPaymentGateway (dev profile)
    └── StripePaymentGateway (prod profile)
```

## 🔒 Sécurité

### Bonnes pratiques implémentées

- ✅ **Validation des entrées** : Jakarta Validation (JSR 303/380)
- ✅ **Soft delete** : Les plans et abonnements ne sont jamais supprimés physiquement
- ✅ **Idempotence** : Les paiements utilisent des clés d'idempotence
- ✅ **Secrets** : Les clés Stripe sont stockées dans des variables d'environnement
- ✅ **Tokens** : Les tokens de carte ne sont jamais loggés
- ✅ **HTTPS** : Recommandé en production (configuration serveur)

### Configuration Stripe

Pour la production, consultez **[STRIPE_CONFIGURATION_GUIDE.md](./STRIPE_CONFIGURATION_GUIDE.md)**.

## 📊 Monitoring

### Actuator Endpoints

Le service expose plusieurs endpoints de monitoring via Spring Actuator :

```bash
# Health check
GET /actuator/health

# Informations de l'application
GET /actuator/info

# Métriques
GET /actuator/metrics

# Métriques Prometheus
GET /actuator/prometheus
```

### Logs

Les logs sont configurés dans `logback-spring.xml` :

- **Console** : Format simple pour développement
- **Fichier** : Format détaillé avec stack traces (si configuré)

Niveaux de log :
- `INFO` : Par défaut
- `DEBUG` : Pour `com.transport.subscription`
- `WARN` : Pour Hibernate

## 🔄 Scheduler

Le service inclut un scheduler pour les renouvellements automatiques :

- **Fréquence** : Quotidienne à minuit (configurable via `SCHEDULER_RENEWAL_CRON`)
- **Fonctionnalité** : Renouvelle automatiquement les abonnements actifs avec `autoRenewEnabled=true`
- **Gestion d'erreurs** : Les échecs de paiement sont loggés et l'abonnement passe en statut approprié

### Désactiver le scheduler

```yaml
scheduler:
  renewal:
    enabled: false
```

## 📝 Contribution

1. Créer une branche depuis `main` : `git checkout -b feature/ma-feature`
2. Faire les modifications
3. Ajouter des tests unitaires et d'intégration
4. Vérifier que tous les tests passent : `mvn test`
5. Créer une pull request

### Checklist avant commit

- [ ] Code compilé sans erreurs
- [ ] Tous les tests passent
- [ ] Pas de warnings majeurs
- [ ] Documentation mise à jour si nécessaire
- [ ] Code formaté et lisible

## 🐛 Dépannage

### Problèmes courants

#### Erreur de connexion à la base de données

```
Connection refused: connect
```

**Solution** : Vérifier que PostgreSQL est démarré et que les credentials sont corrects.

#### Port déjà utilisé

```
Port 8085 is already in use
```

**Solution** : Changer le port via `SERVER_PORT=8086` ou arrêter le processus utilisant le port.

#### Erreur de migration Flyway

```
Migration failed
```

**Solution** : Vérifier que la base de données est vide ou que les migrations sont à jour.

## 📄 Licence

Apache 2.0

## 📞 Support

Pour toute question ou problème, ouvrir une issue sur le repository.

---

**Dernière mise à jour** : 2025-01-14
