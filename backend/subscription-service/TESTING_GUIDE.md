# 🧪 Guide de Test Complet - Subscription Service

## 📋 Vue d'ensemble

Ce guide vous explique comment tester le service Subscription de A à Z.

---

## 🚀 Démarrage Rapide (5 minutes)

### 1. Configuration de la Base de Données

```bash
# Créer la base de données
psql -U postgres
CREATE DATABASE urban_transport;
\c urban_transport

# Exécuter le script SQL
\i "c:\Users\Akabb\OneDrive\Desktop\MCD\script_sql_subscription.txt"
```

### 2. Configuration des Variables d'Environnement

**Option A : Script PowerShell (Recommandé)**

```powershell
cd backend/subscription-service
.\start-service.ps1
```

**Option B : Variables manuelles**

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/urban_transport"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "postgres"
$env:STRIPE_SECRET_KEY = ""  # Laisser vide pour mode mock
```

### 3. Démarrer le Service

```bash
mvn spring-boot:run
```

### 4. Vérifier que ça fonctionne

Ouvrir : http://localhost:8085/swagger-ui.html

---

## 🧪 Méthodes de Test

### Méthode 1 : Swagger UI (Le plus simple) ⭐

1. Démarrer le service
2. Ouvrir : http://localhost:8085/swagger-ui.html
3. Cliquer sur un endpoint
4. Cliquer sur "Try it out"
5. Remplir les données
6. Cliquer sur "Execute"

**Avantages :**
- Interface graphique
- Documentation intégrée
- Pas besoin d'outils externes

---

### Méthode 2 : Script PowerShell

```powershell
cd backend/subscription-service
.\test-endpoints.ps1
```

Le script teste automatiquement tous les endpoints principaux.

---

### Méthode 3 : Postman

1. Importer la collection : `Subscription_Service.postman_collection.json`
2. Configurer l'environnement :
   - `base_url` : `http://localhost:8085`
   - `user_id` : Un UUID de test
3. Exécuter les requêtes dans l'ordre

**Ordre recommandé :**
1. Health Check
2. Create Plan
3. Get Plans (pour récupérer le plan_id)
4. Create Subscription
5. Get Subscription
6. Get QR Code
7. Get Payments
8. Cancel Subscription

---

### Méthode 4 : curl (Ligne de commande)

#### Health Check

```bash
curl http://localhost:8085/actuator/health
```

#### Créer un Plan

```bash
curl -X POST http://localhost:8085/api/plans \
  -H "Content-Type: application/json" \
  -d '{
    "planCode": "MONTHLY",
    "description": "Abonnement mensuel",
    "durationDays": 30,
    "price": 29.99,
    "currency": "USD",
    "isActive": true
  }'
```

**Notez le `planId` dans la réponse !**

#### Créer un Abonnement

```bash
# Remplacez YOUR_PLAN_ID et YOUR_USER_ID
curl -X POST http://localhost:8085/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "YOUR_USER_ID",
    "planId": "YOUR_PLAN_ID",
    "cardToken": "tok_visa",
    "cardExpMonth": 12,
    "cardExpYear": 2025,
    "paymentMethod": "CARD",
    "autoRenewEnabled": true
  }'
```

**Tokens Stripe de test :**
- `tok_visa` : Carte Visa valide
- `tok_visa_debit` : Carte Visa débit
- `tok_mastercard` : Carte Mastercard
- `tok_chargeDeclined` : Carte refusée (pour tester les échecs)

Plus d'infos : https://stripe.com/docs/testing

---

## 📝 Scénarios de Test Complets

### Scénario 1 : Création d'un Abonnement Complet

1. **Créer un Plan**
   ```json
   POST /api/plans
   {
     "planCode": "MONTHLY_PREMIUM",
     "description": "Plan premium mensuel",
     "durationDays": 30,
     "price": 49.99,
     "currency": "USD",
     "isActive": true
   }
   ```

2. **Créer un Abonnement**
   ```json
   POST /api/subscriptions
   {
     "userId": "550e8400-e29b-41d4-a716-446655440000",
     "planId": "<plan_id_from_step_1>",
     "cardToken": "tok_visa",
     "cardExpMonth": 12,
     "cardExpYear": 2025,
     "paymentMethod": "CARD",
     "autoRenewEnabled": true
   }
   ```

3. **Vérifier l'Abonnement**
   ```bash
   GET /api/subscriptions/<subscription_id>
   ```

4. **Générer le QR Code**
   ```bash
   GET /api/subscriptions/<subscription_id>/qrcode
   ```

5. **Voir les Paiements**
   ```bash
   GET /api/payments/subscription/<subscription_id>
   ```

---

### Scénario 2 : Gestion du Cycle de Vie

1. **Créer un Abonnement** (voir Scénario 1)
2. **Mettre en Pause**
   ```bash
   PUT /api/subscriptions/<subscription_id>/pause
   ```
3. **Reprendre**
   ```bash
   PUT /api/subscriptions/<subscription_id>/resume
   ```
4. **Renouveler**
   ```bash
   PUT /api/subscriptions/<subscription_id>/renew
   {
     "subscriptionId": "<subscription_id>",
     "useStoredPaymentMethod": true
   }
   ```
5. **Annuler**
   ```bash
   PUT /api/subscriptions/<subscription_id>/cancel
   {
     "subscriptionId": "<subscription_id>",
     "reason": "User requested",
     "refundRequested": false
   }
   ```

---

### Scénario 3 : Gestion des Paiements

1. **Traiter un Paiement**
   ```json
   POST /api/payments
   {
     "subscriptionId": "<subscription_id>",
     "amount": 29.99,
     "currency": "USD",
     "paymentMethod": "CARD",
     "cardToken": "tok_visa",
     "idempotencyKey": "unique-key-12345"
   }
   ```

2. **Voir l'Historique des Paiements**
   ```bash
   GET /api/payments/subscription/<subscription_id>
   ```

3. **Rembourser un Paiement**
   ```bash
   POST /api/payments/<payment_id>/refund?reason=User%20request
   ```

---

## ✅ Checklist de Test

### Tests Fonctionnels

- [ ] Health check fonctionne
- [ ] Créer un plan
- [ ] Obtenir tous les plans
- [ ] Obtenir les plans actifs
- [ ] Créer un abonnement
- [ ] Obtenir un abonnement par ID
- [ ] Obtenir les abonnements d'un utilisateur
- [ ] Générer un QR code
- [ ] Valider un QR code
- [ ] Mettre en pause un abonnement
- [ ] Reprendre un abonnement
- [ ] Renouveler un abonnement
- [ ] Annuler un abonnement
- [ ] Traiter un paiement
- [ ] Obtenir les paiements d'un abonnement
- [ ] Rembourser un paiement

### Tests de Validation

- [ ] Créer un plan avec données invalides (vérifier erreur 400)
- [ ] Créer un abonnement avec plan inexistant (vérifier erreur 404)
- [ ] Créer un abonnement dupliqué (vérifier erreur 409)
- [ ] Payer avec un token invalide (vérifier erreur de paiement)

### Tests de Performance

- [ ] Créer 10 plans rapidement
- [ ] Créer 10 abonnements rapidement
- [ ] Obtenir tous les plans (vérifier temps de réponse)

---

## 🐛 Dépannage

### Le service ne démarre pas

**Vérifier :**
1. Java 17+ installé : `java -version`
2. Port 8085 disponible
3. Base de données accessible
4. Logs d'erreur dans la console

### Erreur "Connection refused" (Base de données)

```bash
# Vérifier que PostgreSQL tourne
psql -U postgres -c "SELECT version();"

# Vérifier la connexion
psql -U postgres -d urban_transport
```

### Erreur "Table does not exist"

```bash
# Réexécuter le script SQL
psql -U postgres -d urban_transport -f "script_sql_subscription.txt"
```

### Les paiements échouent

- En mode mock (Stripe non configuré) : Les paiements devraient toujours réussir
- Avec Stripe : Vérifier que vous utilisez les tokens de test Stripe
- Vérifier les logs pour les détails d'erreur

---

## 📊 Vérification des Résultats

### Vérifier dans la Base de Données

```sql
-- Voir tous les plans
SELECT * FROM subscription_plan;

-- Voir tous les abonnements
SELECT * FROM subscription;

-- Voir tous les paiements
SELECT * FROM subscription_payment;

-- Voir l'historique
SELECT * FROM subscription_history ORDER BY event_date DESC;
```

### Vérifier les Logs

Les logs du service montrent :
- Les requêtes reçues
- Les erreurs éventuelles
- Les opérations de paiement
- Les renouvellements automatiques

---

## 🎯 Prochaines Étapes

1. ✅ Tester tous les endpoints
2. 🔄 Intégrer avec les autres services
3. 🔄 Configurer Stripe pour les vrais paiements
4. 🔄 Ajouter des tests automatisés
5. 🔄 Déployer en production

---

## 📚 Ressources

- **Swagger UI** : http://localhost:8085/swagger-ui.html
- **API Docs** : http://localhost:8085/api-docs
- **Health Check** : http://localhost:8085/actuator/health
- **Metrics** : http://localhost:8085/actuator/prometheus

---

## 💡 Astuces

1. **Utiliser Swagger UI** pour une première exploration
2. **Utiliser Postman** pour des tests répétables
3. **Utiliser les scripts** pour des tests automatisés
4. **Vérifier la base de données** pour comprendre l'état des données
5. **Consulter les logs** pour déboguer les problèmes

