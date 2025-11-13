# 📝 Commandes Manuelles - Subscription Service

## 🚀 Démarrage du Service

### Étape 1 : Aller dans le répertoire du service

```powershell
cd backend/subscription-service
```

### Étape 2 : Configurer JAVA_HOME

```powershell
$javaPath = (Get-Command java).Source
$env:JAVA_HOME = Split-Path (Split-Path $javaPath)
```

### Étape 3 : Configurer les variables d'environnement

**IMPORTANT :** Remplacez `votre_mot_de_passe` par votre mot de passe PostgreSQL réel.

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/subscription_service"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "votre_mot_de_passe"  # ⚠️ Remplacez par votre mot de passe PostgreSQL
$env:STRIPE_SECRET_KEY = ""
```

**Pour trouver votre mot de passe PostgreSQL :**
- Si vous ne vous souvenez pas, vous pouvez le réinitialiser
- Ou vérifier dans votre configuration PostgreSQL

### Étape 4 : Démarrer le service

```powershell
mvn spring-boot:run
```

---

## 🧪 Tester les Endpoints

### Health Check

```powershell
Invoke-WebRequest -Uri "http://localhost:8085/actuator/health"
```

### Créer un Plan

```powershell
$plan = @{
    planCode = "MONTHLY"
    description = "Abonnement mensuel"
    durationDays = 30
    price = 29.99
    currency = "USD"
    isActive = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/api/plans" -Method POST -Body $plan -ContentType "application/json"
```

### Obtenir tous les Plans

```powershell
Invoke-RestMethod -Uri "http://localhost:8085/api/plans" -Method GET
```

### Créer un Abonnement

```powershell
# Remplacez YOUR_PLAN_ID et YOUR_USER_ID par les valeurs réelles
$subscription = @{
    userId = "550e8400-e29b-41d4-a716-446655440000"
    planId = "YOUR_PLAN_ID"
    cardToken = "tok_visa"
    cardExpMonth = 12
    cardExpYear = 2025
    paymentMethod = "CARD"
    autoRenewEnabled = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/api/subscriptions" -Method POST -Body $subscription -ContentType "application/json"
```

### Obtenir un Abonnement

```powershell
Invoke-RestMethod -Uri "http://localhost:8085/api/subscriptions/YOUR_SUBSCRIPTION_ID" -Method GET
```

### Générer QR Code

```powershell
Invoke-RestMethod -Uri "http://localhost:8085/api/subscriptions/YOUR_SUBSCRIPTION_ID/qrcode" -Method GET
```

---

## 🔍 Vérifications

### Vérifier JAVA_HOME

```powershell
echo $env:JAVA_HOME
```

### Vérifier Java

```powershell
java -version
```

### Vérifier Maven

```powershell
mvn -version
```

### Vérifier le répertoire

```powershell
Get-Location
# Doit afficher: ...\backend\subscription-service
```

### Vérifier que pom.xml existe

```powershell
Test-Path pom.xml
# Doit retourner: True
```

---

## 📚 URLs Utiles

Une fois le service démarré :

- **Health Check** : http://localhost:8085/actuator/health
- **Swagger UI** : http://localhost:8085/swagger-ui.html
- **API Docs** : http://localhost:8085/api-docs

---

## 🆘 Dépannage

### Erreur "JAVA_HOME not defined"

```powershell
$javaPath = (Get-Command java).Source
$env:JAVA_HOME = Split-Path (Split-Path $javaPath)
```

### Erreur "No plugin found for prefix 'spring-boot'"

Assurez-vous d'être dans le répertoire `backend/subscription-service` :

```powershell
cd backend/subscription-service
```

### Erreur "Connection refused" (Base de données)

Vérifiez que :
1. PostgreSQL est démarré
2. La base `subscription_service` existe
3. Les credentials sont corrects

```powershell
psql -U postgres -d subscription_service -c "\dt"
```

