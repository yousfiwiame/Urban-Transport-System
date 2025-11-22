# Guide de Test de la Communication Kafka entre User-Service et Schedule-Service

## Vue d'ensemble

Ce document décrit comment tester la communication Kafka complète entre `user-service` et `schedule-service`. La communication est asynchrone et basée sur des événements publiés dans des topics Kafka.

## Architecture de Communication

### Flux des Événements

```
user-service (Producer)          Kafka Topics          schedule-service (Consumer)
───────────────────────          ───────────          ────────────────────────
                                                         
User Created Event    ──────────> user-created-events  ──────────> handleUserCreated()
User Updated Event    ──────────> user-updated-events  ──────────> handleUserUpdated()
User Deleted Event    ──────────> user-deleted-events  ──────────> handleUserDeleted()
```

### Topics Kafka

1. **user-created-events** : Événement publié lorsqu'un nouvel utilisateur est créé
2. **user-updated-events** : Événement publié lorsqu'un utilisateur est mis à jour
3. **user-deleted-events** : Événement publié lorsqu'un utilisateur est supprimé

## Prérequis

1. **Services démarrés** :
   - Kafka et Zookeeper (via docker-compose)
   - user-service (port 8081)
   - schedule-service (port 8083)

2. **Outils nécessaires** :
   - `curl` ou Postman pour les requêtes HTTP
   - Accès aux logs des services
   - Optionnel : Kafka CLI tools (`kafka-console-consumer`, `kafka-console-producer`)

## Tests à Effectuer

### Test 1 : Création d'un Utilisateur (User Created Event)

#### Objectif
Vérifier que lorsqu'un utilisateur est créé dans `user-service`, l'événement est correctement publié dans Kafka et consommé par `schedule-service`.

#### Étapes

1. **Vérifier que Kafka est démarré** :
   ```bash
   docker ps | grep kafka
   ```

2. **Vérifier que les services sont démarrés** :
   ```bash
   curl http://localhost:8081/actuator/health
   curl http://localhost:8083/actuator/health
   ```

3. **Créer un nouvel utilisateur** :
   ```bash
   curl -X POST http://localhost:8081/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "test.user@example.com",
       "password": "Test123!@#",
       "firstName": "Test",
       "lastName": "User",
       "phoneNumber": "+212612345678"
     }'
   ```

4. **Vérifier les logs de user-service** :
   Vous devriez voir dans les logs de `user-service` :
   ```
   Publishing user created event: UserCreatedEvent(userId=..., email=test.user@example.com, ...)
   ```

5. **Vérifier les logs de schedule-service** :
   Vous devriez voir dans les logs de `schedule-service` :
   ```
   Received user created event - UserId: ..., Email: test.user@example.com, Partition: ..., Offset: ...
   Successfully processed user created event for user: ...
   ```

#### Résultats Attendus

✅ **user-service** :
- Code HTTP 200 ou 201
- Réponse JSON avec les informations de l'utilisateur créé
- Log indiquant la publication de l'événement Kafka

✅ **schedule-service** :
- Log indiquant la réception de l'événement
- Log indiquant le traitement réussi de l'événement
- Aucune erreur dans les logs

#### Vérification avec Kafka CLI (Optionnel)

Si vous avez accès aux outils Kafka CLI :

```bash
# Consulter les messages dans le topic
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic user-created-events \
  --from-beginning
```

Vous devriez voir un message JSON avec les détails de l'événement.

---

### Test 2 : Mise à Jour d'un Utilisateur (User Updated Event)

#### Objectif
Vérifier que lorsqu'un utilisateur est mis à jour dans `user-service`, l'événement est correctement publié et consommé.

#### Étapes

1. **Obtenir un token d'authentification** (si nécessaire) :
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "test.user@example.com",
       "password": "Test123!@#"
     }'
   ```

2. **Mettre à jour l'utilisateur** :
   ```bash
   curl -X PUT http://localhost:8081/api/users/{userId} \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer {token}" \
     -d '{
       "firstName": "Updated",
       "lastName": "Name"
     }'
   ```

3. **Vérifier les logs** :
   - **user-service** : Log de publication de l'événement `UserUpdatedEvent`
   - **schedule-service** : Log de réception et traitement de l'événement

#### Résultats Attendus

✅ Les deux services doivent loguer l'événement correctement
✅ Aucune erreur dans les logs

---

### Test 3 : Suppression d'un Utilisateur (User Deleted Event)

#### Objectif
Vérifier que lorsqu'un utilisateur est supprimé, l'événement est correctement publié et consommé.

#### Étapes

1. **Supprimer l'utilisateur** :
   ```bash
   curl -X DELETE http://localhost:8081/api/users/{userId} \
     -H "Authorization: Bearer {token}"
   ```

2. **Vérifier les logs** :
   - **user-service** : Log de publication de l'événement `UserDeletedEvent`
   - **schedule-service** : Log de réception et traitement de l'événement

#### Résultats Attendus

✅ Les deux services doivent loguer l'événement correctement
✅ Aucune erreur dans les logs

---

## Vérifications des Logs

### Logs à Surveiller dans user-service

```
INFO  - Publishing user created event: UserCreatedEvent(...)
INFO  - Publishing user updated event: UserUpdatedEvent(...)
INFO  - Publishing user deleted event: UserDeletedEvent(...)
```

### Logs à Surveiller dans schedule-service

```
INFO  - Received user created event - UserId: X, Email: ..., Partition: Y, Offset: Z
INFO  - Successfully processed user created event for user: X

INFO  - Received user updated event - UserId: X, Email: ..., Partition: Y, Offset: Z
INFO  - Successfully processed user updated event for user: X

INFO  - Received user deleted event - UserId: X, Email: ..., Partition: Y, Offset: Z
INFO  - Successfully processed user deleted event for user: X
```

## Tests de Résilience

### Test 4 : Gestion des Erreurs

#### Objectif
Vérifier que les erreurs sont correctement gérées et que les messages sont retentés.

#### Étapes

1. **Simuler une erreur** dans `schedule-service` (temporairement) :
   - Modifier `UserEventConsumer` pour lancer une exception
   - Créer un utilisateur
   - Observer les tentatives de retry dans les logs Kafka

2. **Vérifier les logs d'erreur** :
   ```
   ERROR - Error processing user created event for user: X
   ```

#### Résultats Attendus

✅ Les erreurs sont loguées correctement
✅ Kafka retente automatiquement le traitement du message

---

### Test 5 : Disponibilité de Kafka

#### Objectif
Vérifier le comportement lorsque Kafka est indisponible.

#### Étapes

1. **Arrêter Kafka** :
   ```bash
   docker-compose stop kafka zookeeper
   ```

2. **Tenter de créer un utilisateur** :
   - L'opération devrait échouer ou être mise en queue

3. **Redémarrer Kafka** :
   ```bash
   docker-compose start kafka zookeeper
   ```

4. **Vérifier que les messages en attente sont traités**

#### Résultats Attendus

✅ Les services gèrent gracieusement l'indisponibilité de Kafka
✅ Les messages sont traités une fois Kafka redémarré

---

## Vérification des Topics Kafka

### Lister les Topics

```bash
docker exec -it kafka kafka-topics --list \
  --bootstrap-server localhost:9092
```

Vous devriez voir :
- `user-created-events`
- `user-updated-events`
- `user-deleted-events`

### Vérifier les Détails d'un Topic

```bash
docker exec -it kafka kafka-topics --describe \
  --bootstrap-server localhost:9092 \
  --topic user-created-events
```

### Consulter les Messages d'un Topic

```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic user-created-events \
  --from-beginning \
  --property print.key=true \
  --property print.value=true
```

---

## Monitoring et Métriques

### Actuator Endpoints

Les deux services exposent des endpoints Actuator pour le monitoring :

- **Health Check** :
  ```bash
  curl http://localhost:8081/actuator/health
  curl http://localhost:8083/actuator/health
  ```

- **Métriques Kafka** (si configuré) :
  ```bash
  curl http://localhost:8081/actuator/metrics/kafka.producer
  curl http://localhost:8083/actuator/metrics/kafka.consumer
  ```

---

## Checklist de Validation

Avant de considérer la communication Kafka comme fonctionnelle, vérifiez :

- [ ] ✅ Les topics Kafka sont créés automatiquement
- [ ] ✅ Les événements sont publiés par `user-service`
- [ ] ✅ Les événements sont consommés par `schedule-service`
- [ ] ✅ Les logs montrent la communication complète
- [ ] ✅ Aucune erreur dans les logs des deux services
- [ ] ✅ Les messages sont correctement sérialisés/désérialisés
- [ ] ✅ Les erreurs sont gérées correctement
- [ ] ✅ La résilience fonctionne (retry, etc.)

---

## Dépannage

### Problème : Les événements ne sont pas reçus

**Solutions** :
1. Vérifier que Kafka est démarré : `docker ps | grep kafka`
2. Vérifier les logs de Kafka : `docker logs kafka`
3. Vérifier la configuration du consumer group ID
4. Vérifier que les topics existent

### Problème : Erreurs de désérialisation

**Solutions** :
1. Vérifier que les classes d'événements correspondent entre les services
2. Vérifier la configuration des deserializers
3. Vérifier que `TRUSTED_PACKAGES` inclut les packages nécessaires

### Problème : Messages dupliqués

**Solutions** :
1. Vérifier la configuration `auto-offset-reset`
2. Vérifier que le consumer group ID est correct
3. Vérifier l'idempotence des handlers

---

## Notes Importantes

1. **Synchronisation** : La communication est asynchrone. Il peut y avoir un léger délai entre la publication et la consommation.

2. **Idempotence** : Assurez-vous que les handlers dans `schedule-service` sont idempotents (peuvent être exécutés plusieurs fois sans effet de bord).

3. **Ordre des Messages** : Les messages avec la même clé (userId) sont garantis d'être dans l'ordre dans la même partition.

4. **At-Least-Once Delivery** : Kafka garantit qu'un message sera livré au moins une fois. Gérez les duplications si nécessaire.

---

## Prochaines Étapes

Une fois les tests de base validés, vous pouvez :

1. Implémenter la logique métier dans les handlers (`TODO` dans `UserEventConsumer`)
2. Ajouter des tests unitaires pour les consumers
3. Configurer un Dead Letter Queue pour les messages en erreur
4. Ajouter des métriques de monitoring
5. Implémenter la communication bidirectionnelle (schedule-service → user-service)

---

**Date de création** : 2025-01-22
**Dernière mise à jour** : 2025-01-22

