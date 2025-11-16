# API Documentation - Subscription Service

Documentation complète des endpoints REST pour le développement frontend.

**Base URL** : `http://localhost:8085/api`

**Format** : JSON (Content-Type: `application/json`)

---

## 📋 Table des matières

- [Authentification](#authentification)
- [Codes de statut HTTP](#codes-de-statut-http)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Plans d'abonnement](#plans-dabonnement)
- [Abonnements](#abonnements)
- [Paiements](#paiements)
- [Types et énumérations](#types-et-énumérations)

---

## 🔐 Authentification

> **Note** : Actuellement, le service n'implémente pas d'authentification. Dans un environnement de production, ajoutez JWT ou OAuth2.

Les endpoints nécessitent que l'`userId` soit fourni dans les requêtes (pour les abonnements).

---

## 📊 Codes de statut HTTP

| Code | Description |
|------|-------------|
| `200 OK` | Requête réussie |
| `201 Created` | Ressource créée avec succès |
| `204 No Content` | Requête réussie, pas de contenu à retourner |
| `400 Bad Request` | Données invalides ou manquantes |
| `404 Not Found` | Ressource non trouvée |
| `409 Conflict` | Conflit (ex: abonnement dupliqué) |
| `422 Unprocessable Entity` | Erreur de validation |
| `500 Internal Server Error` | Erreur serveur |

---

## ⚠️ Gestion des erreurs

Toutes les erreurs retournent un objet JSON standardisé :

```json
{
  "timestamp": "2025-01-14T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/subscriptions",
  "errors": [
    {
      "field": "userId",
      "message": "User ID is required"
    }
  ]
}
```

---

## 📦 Plans d'abonnement

### 1. Créer un plan

**Endpoint** : `POST /api/plans`

**Description** : Crée un nouveau plan d'abonnement.

**Request Body** :
```json
{
  "planCode": "MONTHLY_PREMIUM",
  "description": "Abonnement mensuel premium",
  "durationDays": 30,
  "price": 29.99,
  "currency": "EUR",
  "isActive": true
}
```

**Validation** :
- `planCode` : Requis, max 64 caractères, unique
- `description` : Optionnel
- `durationDays` : Requis, > 0
- `price` : Requis, >= 0, format décimal (2 décimales)
- `currency` : Requis, 3 caractères (ISO 4217)
- `isActive` : Optionnel, défaut `true`

**Response** : `201 Created`
```json
{
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM",
  "description": "Abonnement mensuel premium",
  "durationDays": 30,
  "price": 29.99,
  "currency": "EUR",
  "isActive": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T10:30:00Z"
}
```

---

### 2. Obtenir tous les plans

**Endpoint** : `GET /api/plans`

**Description** : Récupère tous les plans (actifs et inactifs).

**Response** : `200 OK`
```json
[
  {
    "planId": "550e8400-e29b-41d4-a716-446655440000",
    "planCode": "MONTHLY_PREMIUM",
    "description": "Abonnement mensuel premium",
    "durationDays": 30,
    "price": 29.99,
    "currency": "EUR",
    "isActive": true,
    "createdAt": "2025-01-14T10:30:00Z",
    "updatedAt": "2025-01-14T10:30:00Z"
  }
]
```

---

### 3. Obtenir les plans actifs

**Endpoint** : `GET /api/plans/active`

**Description** : Récupère uniquement les plans actifs.

**Response** : `200 OK`
```json
[
  {
    "planId": "550e8400-e29b-41d4-a716-446655440000",
    "planCode": "MONTHLY_PREMIUM",
    "description": "Abonnement mensuel premium",
    "durationDays": 30,
    "price": 29.99,
    "currency": "EUR",
    "isActive": true,
    "createdAt": "2025-01-14T10:30:00Z",
    "updatedAt": "2025-01-14T10:30:00Z"
  }
]
```

---

### 4. Obtenir un plan par ID

**Endpoint** : `GET /api/plans/{id}`

**Description** : Récupère un plan spécifique par son ID.

**Path Parameters** :
- `id` : UUID du plan

**Response** : `200 OK`
```json
{
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM",
  "description": "Abonnement mensuel premium",
  "durationDays": 30,
  "price": 29.99,
  "currency": "EUR",
  "isActive": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `404 Not Found` : Plan non trouvé

---

### 5. Obtenir un plan par code

**Endpoint** : `GET /api/plans/code/{code}`

**Description** : Récupère un plan par son code unique.

**Path Parameters** :
- `code` : Code du plan (ex: "MONTHLY_PREMIUM")

**Response** : `200 OK`
```json
{
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM",
  "description": "Abonnement mensuel premium",
  "durationDays": 30,
  "price": 29.99,
  "currency": "EUR",
  "isActive": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `404 Not Found` : Plan non trouvé

---

### 6. Mettre à jour un plan

**Endpoint** : `PUT /api/plans/{id}`

**Description** : Met à jour un plan existant.

**Path Parameters** :
- `id` : UUID du plan

**Request Body** : (identique à la création)
```json
{
  "planCode": "MONTHLY_PREMIUM_V2",
  "description": "Abonnement mensuel premium - Version 2",
  "durationDays": 30,
  "price": 34.99,
  "currency": "EUR",
  "isActive": true
}
```

**Response** : `200 OK`
```json
{
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM_V2",
  "description": "Abonnement mensuel premium - Version 2",
  "durationDays": 30,
  "price": 34.99,
  "currency": "EUR",
  "isActive": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T11:00:00Z"
}
```

**Erreurs** :
- `404 Not Found` : Plan non trouvé

---

### 7. Désactiver un plan (Soft Delete)

**Endpoint** : `DELETE /api/plans/{id}`

**Description** : Désactive un plan (soft delete). Le plan n'est pas supprimé physiquement, mais `isActive` est mis à `false`.

**Path Parameters** :
- `id` : UUID du plan

**Response** : `204 No Content`

**Erreurs** :
- `404 Not Found` : Plan non trouvé

---

## 🎫 Abonnements

### 1. Créer un abonnement

**Endpoint** : `POST /api/subscriptions`

**Description** : Crée un nouvel abonnement et traite le paiement initial. L'abonnement est créé avec le statut `PENDING` et passe à `ACTIVE` si le paiement réussit.

**Request Body** :
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "cardToken": "tok_visa",
  "cardExpMonth": 12,
  "cardExpYear": 2025,
  "paymentMethod": "CARD",
  "autoRenewEnabled": true
}
```

**Validation** :
- `userId` : Requis, UUID
- `planId` : Requis, UUID
- `cardToken` : Requis, max 128 caractères (token Stripe en prod, "tok_visa" en dev)
- `cardExpMonth` : Requis, entre 1 et 12
- `cardExpYear` : Requis, >= 2025
- `paymentMethod` : Requis, voir [PaymentMethod](#paymentmethod)
- `autoRenewEnabled` : Optionnel, défaut `true`

**Response** : `201 Created`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "plan": {
    "planId": "550e8400-e29b-41d4-a716-446655440000",
    "planCode": "MONTHLY_PREMIUM",
    "description": "Abonnement mensuel premium",
    "durationDays": 30,
    "price": 29.99,
    "currency": "EUR",
    "isActive": true,
    "createdAt": "2025-01-14T10:30:00Z",
    "updatedAt": "2025-01-14T10:30:00Z"
  },
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM",
  "currency": "EUR",
  "status": "ACTIVE",
  "startDate": "2025-01-14",
  "endDate": "2025-02-13",
  "nextBillingDate": "2025-02-13",
  "amountPaid": 29.99,
  "autoRenewEnabled": true,
  "hasQrCode": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `400 Bad Request` : Validation échouée
- `404 Not Found` : Plan non trouvé
- `409 Conflict` : Abonnement actif déjà existant pour cet utilisateur et ce plan
- `422 Unprocessable Entity` : Échec du paiement

**Note** : Si le paiement échoue, l'abonnement reste en statut `PENDING`. Utilisez l'endpoint `POST /api/subscriptions/{id}/retry-payment` pour réessayer.

---

### 2. Obtenir un abonnement par ID

**Endpoint** : `GET /api/subscriptions/{id}`

**Description** : Récupère un abonnement spécifique.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "plan": {
    "planId": "550e8400-e29b-41d4-a716-446655440000",
    "planCode": "MONTHLY_PREMIUM",
    "description": "Abonnement mensuel premium",
    "durationDays": 30,
    "price": 29.99,
    "currency": "EUR",
    "isActive": true,
    "createdAt": "2025-01-14T10:30:00Z",
    "updatedAt": "2025-01-14T10:30:00Z"
  },
  "planId": "550e8400-e29b-41d4-a716-446655440000",
  "planCode": "MONTHLY_PREMIUM",
  "currency": "EUR",
  "status": "ACTIVE",
  "startDate": "2025-01-14",
  "endDate": "2025-02-13",
  "nextBillingDate": "2025-02-13",
  "amountPaid": 29.99,
  "autoRenewEnabled": true,
  "hasQrCode": true,
  "createdAt": "2025-01-14T10:30:00Z",
  "updatedAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé

---

### 3. Obtenir les abonnements d'un utilisateur

**Endpoint** : `GET /api/subscriptions/user/{userId}`

**Description** : Récupère tous les abonnements d'un utilisateur (triés par date de création décroissante).

**Path Parameters** :
- `userId` : UUID de l'utilisateur

**Response** : `200 OK`
```json
[
  {
    "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "plan": { ... },
    "status": "ACTIVE",
    "startDate": "2025-01-14",
    "endDate": "2025-02-13",
    ...
  }
]
```

---

### 4. Annuler un abonnement

**Endpoint** : `PUT /api/subscriptions/{id}/cancel`

**Description** : Annule un abonnement. Le statut passe à `CANCELLED`. Option de remboursement disponible.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Request Body** :
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "reason": "User requested cancellation",
  "refundRequested": false
}
```

**Validation** :
- `subscriptionId` : Requis, UUID (doit correspondre au path parameter)
- `reason` : Optionnel, texte libre
- `refundRequested` : Optionnel, défaut `false`

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "status": "CANCELLED",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement déjà annulé ou expiré

---

### 5. Renouveler un abonnement

**Endpoint** : `PUT /api/subscriptions/{id}/renew`

**Description** : Renouvelle un abonnement. Utilise la méthode de paiement stockée ou une nouvelle carte.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Request Body** :
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "useStoredPaymentMethod": true,
  "newCardToken": null,
  "newCardExpMonth": null,
  "newCardExpYear": null
}
```

**Validation** :
- `subscriptionId` : Requis, UUID
- `useStoredPaymentMethod` : Optionnel, défaut `true`
- Si `useStoredPaymentMethod` est `false`, `newCardToken`, `newCardExpMonth`, `newCardExpYear` sont requis

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "status": "ACTIVE",
  "endDate": "2025-03-15",
  "nextBillingDate": "2025-03-15",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement non renouvelable
- `422 Unprocessable Entity` : Échec du paiement

---

### 6. Mettre en pause un abonnement

**Endpoint** : `PUT /api/subscriptions/{id}/pause`

**Description** : Met en pause un abonnement actif. Le statut passe à `PAUSED`.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "status": "PAUSED",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement non pausable (déjà pausé, annulé, expiré)

---

### 7. Reprendre un abonnement

**Endpoint** : `PUT /api/subscriptions/{id}/resume`

**Description** : Reprend un abonnement en pause. Le statut passe à `ACTIVE`.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "status": "ACTIVE",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement non reprenable (pas en pause)

---

### 8. Obtenir le QR code d'un abonnement

**Endpoint** : `GET /api/subscriptions/{id}/qrcode`

**Description** : Récupère le QR code d'un abonnement actif.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "qrCodeData": "660e8400-e29b-41d4-a716-446655440000",
  "qrCodeImageBase64": "iVBORw0KGgoAAAANSUhEUgAA..."
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement non actif

---

### 9. Valider un QR code

**Endpoint** : `POST /api/subscriptions/validate-qrcode?qrCodeData={data}`

**Description** : Valide un QR code et vérifie que l'abonnement est actif.

**Query Parameters** :
- `qrCodeData` : Données du QR code (UUID de l'abonnement)

**Response** : `200 OK`
```json
true
```

ou

```json
false
```

---

### 10. Mettre à jour un abonnement

**Endpoint** : `PUT /api/subscriptions/{id}`

**Description** : Met à jour les informations d'un abonnement (auto-renew, etc.).

**Path Parameters** :
- `id` : UUID de l'abonnement

**Request Body** :
```json
{
  "autoRenewEnabled": false
}
```

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "autoRenewEnabled": false,
  ...
}
```

---

### 11. Réessayer le paiement

**Endpoint** : `POST /api/subscriptions/{id}/retry-payment`

**Description** : Réessaye le paiement pour un abonnement en statut `PENDING`.

**Path Parameters** :
- `id` : UUID de l'abonnement

**Request Body** :
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "amount": 29.99,
  "currency": "EUR",
  "paymentMethod": "CARD",
  "cardToken": "tok_visa",
  "idempotencyKey": "unique-key-12345",
  "paymentType": "INITIAL"
}
```

**Validation** :
- `subscriptionId` : Requis, UUID (doit correspondre au path parameter)
- `amount` : Requis, > 0
- `currency` : Requis, 3 caractères majuscules
- `paymentMethod` : Requis
- `cardToken` : Requis
- `idempotencyKey` : Requis, unique pour éviter les doublons
- `paymentType` : Optionnel, défaut `INITIAL`

**Response** : `200 OK`
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "status": "ACTIVE",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Abonnement non trouvé
- `400 Bad Request` : Abonnement non en statut PENDING
- `422 Unprocessable Entity` : Échec du paiement

---

## 💳 Paiements

### 1. Traiter un paiement

**Endpoint** : `POST /api/payments`

**Description** : Traite un paiement pour un abonnement.

**Request Body** :
```json
{
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "amount": 29.99,
  "currency": "EUR",
  "paymentMethod": "CARD",
  "cardToken": "tok_visa",
  "idempotencyKey": "unique-key-12345",
  "paymentType": "INITIAL"
}
```

**Validation** :
- `subscriptionId` : Requis, UUID
- `amount` : Requis, > 0, format décimal (2 décimales)
- `currency` : Requis, 3 caractères majuscules (ISO 4217)
- `paymentMethod` : Requis, voir [PaymentMethod](#paymentmethod)
- `cardToken` : Requis, max 128 caractères
- `idempotencyKey` : Requis, max 128 caractères, unique
- `paymentType` : Optionnel, défaut `INITIAL`, voir [PaymentType](#paymenttype)

**Response** : `201 Created`
```json
{
  "paymentId": "770e8400-e29b-41d4-a716-446655440000",
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "amount": 29.99,
  "currency": "EUR",
  "paymentStatus": "SUCCEEDED",
  "paymentMethod": "CARD",
  "paymentType": "INITIAL",
  "paymentDate": "2025-01-14T10:30:00Z",
  "failureReason": null,
  "externalTxnId": "ch_1234567890",
  "createdAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `400 Bad Request` : Validation échouée
- `404 Not Found` : Abonnement non trouvé
- `409 Conflict` : Clé d'idempotence déjà utilisée
- `422 Unprocessable Entity` : Échec du paiement

---

### 2. Obtenir les paiements d'un abonnement

**Endpoint** : `GET /api/payments/subscription/{subscriptionId}`

**Description** : Récupère tous les paiements d'un abonnement (triés par date décroissante).

**Path Parameters** :
- `subscriptionId` : UUID de l'abonnement

**Response** : `200 OK`
```json
[
  {
    "paymentId": "770e8400-e29b-41d4-a716-446655440000",
    "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
    "amount": 29.99,
    "currency": "EUR",
    "paymentStatus": "SUCCEEDED",
    "paymentMethod": "CARD",
    "paymentType": "INITIAL",
    "paymentDate": "2025-01-14T10:30:00Z",
    "failureReason": null,
    "externalTxnId": "ch_1234567890",
    "createdAt": "2025-01-14T10:30:00Z"
  }
]
```

---

### 3. Obtenir un paiement par ID

**Endpoint** : `GET /api/payments/{id}`

**Description** : Récupère un paiement spécifique.

**Path Parameters** :
- `id` : UUID du paiement

**Response** : `200 OK`
```json
{
  "paymentId": "770e8400-e29b-41d4-a716-446655440000",
  "subscriptionId": "660e8400-e29b-41d4-a716-446655440000",
  "amount": 29.99,
  "currency": "EUR",
  "paymentStatus": "SUCCEEDED",
  "paymentMethod": "CARD",
  "paymentType": "INITIAL",
  "paymentDate": "2025-01-14T10:30:00Z",
  "failureReason": null,
  "externalTxnId": "ch_1234567890",
  "createdAt": "2025-01-14T10:30:00Z"
}
```

**Erreurs** :
- `404 Not Found` : Paiement non trouvé

---

### 4. Rembourser un paiement

**Endpoint** : `POST /api/payments/{id}/refund?reason={reason}`

**Description** : Rembourse un paiement réussi.

**Path Parameters** :
- `id` : UUID du paiement

**Query Parameters** :
- `reason` : Optionnel, raison du remboursement

**Response** : `200 OK`
```json
{
  "paymentId": "770e8400-e29b-41d4-a716-446655440000",
  "paymentStatus": "REFUNDED",
  ...
}
```

**Erreurs** :
- `404 Not Found` : Paiement non trouvé
- `400 Bad Request` : Paiement non remboursable (déjà remboursé, échoué, etc.)

---

### 5. Webhook de paiement

**Endpoint** : `POST /api/payments/webhook`

**Description** : Endpoint pour recevoir les webhooks Stripe (production).

**Headers** :
- `X-Webhook-Signature` : Signature du webhook (optionnel en dev)

**Request Body** : Payload brut de Stripe

**Response** : `200 OK`
```
Webhook received
```

> **Note** : Cet endpoint est principalement utilisé par Stripe en production. Pour le développement, utilisez le MockPaymentGateway.

---

## 📝 Types et énumérations

### SubscriptionStatus

Statuts possibles d'un abonnement :

- `PENDING` : Abonnement créé, paiement en attente
- `ACTIVE` : Abonnement actif et valide
- `PAUSED` : Abonnement temporairement en pause
- `CANCELLED` : Abonnement annulé
- `EXPIRED` : Abonnement expiré

### PaymentStatus

Statuts possibles d'un paiement :

- `PENDING` : Paiement en attente de traitement
- `SUCCEEDED` : Paiement réussi
- `FAILED` : Paiement échoué
- `REFUNDED` : Paiement remboursé
- `CANCELLED` : Paiement annulé

### PaymentMethod

Méthodes de paiement supportées :

- `CARD` : Carte bancaire (Stripe)
- `BANK_TRANSFER` : Virement bancaire
- `WALLET` : Portefeuille électronique
- `CASH` : Espèces
- `OTHER` : Autre méthode

### PaymentType

Types de paiement :

- `INITIAL` : Paiement initial (création d'abonnement)
- `RENEWAL` : Renouvellement automatique
- `UPGRADE` : Mise à niveau de plan
- `DOWNGRADE` : Rétrogradation de plan
- `ADJUSTMENT` : Ajustement de prix
- `REFUND` : Remboursement

---

## 🔧 Exemples d'utilisation

### Exemple complet : Créer un abonnement

```javascript
// 1. Récupérer les plans actifs
const plansResponse = await fetch('http://localhost:8085/api/plans/active');
const plans = await plansResponse.json();

// 2. Sélectionner un plan
const selectedPlan = plans[0];

// 3. Créer l'abonnement
const subscriptionResponse = await fetch('http://localhost:8085/api/subscriptions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userId: '123e4567-e89b-12d3-a456-426614174000',
    planId: selectedPlan.planId,
    cardToken: 'tok_visa', // En dev, utilisez 'tok_visa'. En prod, token Stripe
    cardExpMonth: 12,
    cardExpYear: 2025,
    paymentMethod: 'CARD',
    autoRenewEnabled: true
  })
});

const subscription = await subscriptionResponse.json();

// 4. Récupérer le QR code
const qrCodeResponse = await fetch(`http://localhost:8085/api/subscriptions/${subscription.subscriptionId}/qrcode`);
const qrCode = await qrCodeResponse.json();

console.log('QR Code:', qrCode.qrCodeImageBase64);
```

### Exemple : Annuler un abonnement

```javascript
const cancelResponse = await fetch(`http://localhost:8085/api/subscriptions/${subscriptionId}/cancel`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    subscriptionId: subscriptionId,
    reason: 'User requested cancellation',
    refundRequested: false
  })
});

const cancelledSubscription = await cancelResponse.json();
```

---

## 📞 Support

Pour toute question sur l'API, consultez :
- **Swagger UI** : http://localhost:8085/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8085/api-docs

---

**Dernière mise à jour** : 2025-01-14

