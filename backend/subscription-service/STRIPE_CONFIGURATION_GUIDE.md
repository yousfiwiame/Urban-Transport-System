# 🎯 GUIDE COMPLET - Configuration Stripe pour Production

## 📋 PLAN D'ACTION

### Phase 1 : Développement (MAINTENANT) ✅
- ✅ Utiliser MockPaymentGateway (déjà implémenté)
- ✅ Tester toute la logique métier
- ✅ Développer sans dépendre de Stripe

### Phase 2 : Tests avec Stripe (BIENTÔT)
- 🔧 Configurer clé de test Stripe
- 🔧 Tester les paiements réels en mode test
- 🔧 Gérer les webhooks Stripe

### Phase 3 : Production (PLUS TARD)
- 🚀 Utiliser clé de production Stripe
- 🚀 Configurer les webhooks en production
- 🚀 Mettre en place la réconciliation

---

## 🔧 PHASE 2 : CONFIGURATION STRIPE TEST

### ÉTAPE 1 : Créer un compte Stripe (GRATUIT)

1. Va sur https://dashboard.stripe.com/register
2. Crée un compte (gratuit, pas besoin de carte bancaire)
3. Active le mode TEST

### ÉTAPE 2 : Récupérer les clés API

1. Va sur https://dashboard.stripe.com/test/apikeys
2. Tu verras 2 clés :
   - **Publishable key** : `pk_test_...` (publique, pour le frontend)
   - **Secret key** : `sk_test_...` (secrète, pour le backend)

3. Copie la **Secret key**

### ÉTAPE 3 : Configurer les variables d'environnement

#### Option A : Variables d'environnement système (RECOMMANDÉ)

**Windows PowerShell :**
```powershell
$env:STRIPE_SECRET_KEY="sk_test_VOTRE_CLE_ICI"
$env:STRIPE_WEBHOOK_SECRET="whsec_VOTRE_WEBHOOK_SECRET"
```

**Linux/Mac :**
```bash
export STRIPE_SECRET_KEY="sk_test_VOTRE_CLE_ICI"
export STRIPE_WEBHOOK_SECRET="whsec_VOTRE_WEBHOOK_SECRET"
```

#### Option B : Fichier application-test.yml

Crée `src/main/resources/application-test.yml` :

```yaml
payment:
  stripe:
    secret-key: sk_test_VOTRE_VRAIE_CLE_TEST
    webhook-secret: whsec_VOTRE_WEBHOOK_SECRET

logging:
  level:
    com.transport.subscription: DEBUG
    org.springframework.web: INFO
```

Puis lance avec :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 🔄 AMÉLIORATION : Gestion des erreurs de paiement

### PROBLÈME ACTUEL

Quand le paiement échoue, la subscription est mise à `CANCELLED`, ce qui empêche de réessayer.

### SOLUTION : Garder PENDING en cas d'échec

Le code actuel a déjà été amélioré pour :
- ✅ Créer la subscription en PENDING
- ✅ Tenter le paiement
- ✅ Si succès → ACTIVE
- ✅ Si échec → reste PENDING (au lieu de CANCELLED)

Cela permet de réessayer le paiement plus tard.

---

## 📱 CARTES DE TEST STRIPE

Quand tu testes avec Stripe, utilise ces numéros de carte :

### ✅ Succès
```
Numéro : 4242 4242 4242 4242
CVC : N'importe quel 3 chiffres (ex: 123)
Date : N'importe quelle date future (ex: 12/25)
Code postal : N'importe quel code postal valide
```

### ❌ Carte déclinée
```
Numéro : 4000 0000 0000 0002
```

### ⏳ Paiement nécessitant 3D Secure
```
Numéro : 4000 0027 6000 3184
```

### 💳 Autres cartes de test
- **Carte avec fonds insuffisants** : `4000 0000 0000 9995`
- **Carte expirée** : `4000 0000 0000 0069`
- **Carte invalide** : `4000 0000 0000 0002`

---

## 🔔 WEBHOOKS STRIPE (IMPORTANT pour production)

### Pourquoi les webhooks ?

Stripe t'envoie des notifications quand :
- ✅ Un paiement réussit
- ❌ Un paiement échoue
- 🔄 Un renouvellement est effectué
- 💳 Une carte expire
- 🚫 Un paiement est contesté (chargeback)

### Configuration des webhooks :

#### 1. En local (développement)

```bash
# Installer Stripe CLI
# Windows (via Scoop ou téléchargement)
scoop install stripe

# Linux/Mac
brew install stripe/stripe-cli/stripe

# Écouter les webhooks et les forwarder vers localhost
stripe listen --forward-to localhost:8085/api/payments/webhook
```

#### 2. En production

1. Va sur https://dashboard.stripe.com/webhooks
2. Clique sur "Add endpoint"
3. URL : `https://ton-domaine.com/api/payments/webhook`
4. Événements à écouter :
   - `payment_intent.succeeded`
   - `payment_intent.payment_failed`
   - `charge.refunded`
5. Copie le "Signing secret" (commence par `whsec_`)

---

## 🚀 RÉSUMÉ : ROADMAP COMPLÈTE

### ✅ Phase 1 : Développement (MAINTENANT)
```
1. Utiliser MockPaymentGateway ✅
2. Profile: dev ✅
3. Tester toute la logique ✅
4. Status: ✅ FAIT
```

### 🔧 Phase 2 : Tests Stripe (CETTE SEMAINE)
```
1. Créer compte Stripe
2. Récupérer clé test (sk_test_...)
3. Configurer variable d'environnement STRIPE_SECRET_KEY
4. Tester avec cartes de test Stripe
5. Configurer webhooks en local (optionnel)
```

### 🚀 Phase 3 : Production (AVANT LE LANCEMENT)
```
1. Récupérer clé prod (sk_live_...)
2. Configurer webhooks en production
3. Tester en environnement staging
4. Déployer en production
```

---

## 💡 CONSEIL FINAL

**Le code actuel fonctionne déjà en mode mock !**

- Si `STRIPE_SECRET_KEY` n'est pas configuré → Mode mock automatique
- Si `STRIPE_SECRET_KEY` est configuré → Mode Stripe réel

**Pour tester avec Stripe maintenant :**

1. Crée un compte Stripe (gratuit)
2. Récupère ta clé test
3. Configure la variable d'environnement :
   ```powershell
   $env:STRIPE_SECRET_KEY="sk_test_..."
   ```
4. Relance l'application
5. Teste avec la carte `4242 4242 4242 4242`

**C'est tout ! Le reste fonctionne automatiquement ! 🎉**

---

## 📞 BESOIN D'AIDE ?

Quand tu seras prêt à intégrer Stripe, je pourrai t'aider avec :
- Configuration des webhooks
- Gestion des erreurs Stripe
- Tests de paiement
- Migration vers la production

**Pour l'instant, concentre-toi sur ta logique métier avec le Mock ! 💪**

