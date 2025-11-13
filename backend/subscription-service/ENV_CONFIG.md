# Configuration des Variables d'Environnement

## Fichier .env (Créer ce fichier dans backend/subscription-service/)

```env
# ===== BASE DE DONNÉES =====
DATABASE_URL=jdbc:postgresql://localhost:5432/urban_transport
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# ===== STRIPE (PAIEMENTS) =====
# Pour le développement, vous pouvez utiliser les clés de test Stripe
# Obtenez-les sur : https://dashboard.stripe.com/test/apikeys
# Laisser vide pour utiliser le mode mock
STRIPE_SECRET_KEY=sk_test_votre_cle_secrete_stripe
STRIPE_WEBHOOK_SECRET=whsec_votre_webhook_secret

# ===== KAFKA (OPTIONNEL) =====
# Si vous n'utilisez pas Kafka, laissez vide
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# ===== EUREKA (SERVICE REGISTRY - OPTIONNEL) =====
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# ===== SCHEDULER =====
SCHEDULER_RENEWAL_ENABLED=true
SCHEDULER_RENEWAL_CRON=0 0 0 * * *

# ===== HOSTNAME =====
HOSTNAME=localhost
```

## Comment utiliser ce fichier

### Option 1 : Variables d'environnement système (Windows)

```powershell
# Dans PowerShell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/urban_transport"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "postgres"
$env:STRIPE_SECRET_KEY = "sk_test_..."
```

### Option 2 : Utiliser le script start-service.ps1

Le script `start-service.ps1` configure automatiquement les variables.

### Option 3 : IntelliJ IDEA

1. Run > Edit Configurations
2. Environment variables > Ajouter les variables

### Option 4 : Ligne de commande Maven

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--DATABASE_URL=jdbc:postgresql://localhost:5432/urban_transport --DATABASE_USERNAME=postgres --DATABASE_PASSWORD=postgres"
```

