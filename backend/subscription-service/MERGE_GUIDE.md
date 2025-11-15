# Guide de Merge - Subscription Service

## 🚀 Commandes pour le merge

### Étape 1 : Vérifier l'état actuel

```bash
# Voir votre branche actuelle
git branch

# Voir les fichiers modifiés
git status
```

### Étape 2 : Commit vos changements (si pas déjà fait)

```bash
# Voir les changements
git status

# Ajouter tous les fichiers
git add .

# Commit avec un message clair
git commit -m "feat: add subscription-service microservice

- Add subscription-service module to parent POM
- Configure Spring Cloud 2024.0.0 and Spring Kafka 3.3.0
- Add subscription management, payment processing, and QR code features
- Include comprehensive tests and documentation"
```

### Étape 3 : Mettre à jour avec main

```bash
# Récupérer les dernières modifications de GitHub
git fetch origin

# Mettre à jour votre branche avec main
git merge origin/main
```

**OU si vous préférez rebase** :
```bash
git rebase origin/main
```

### Étape 4 : Résoudre les conflits (si nécessaire)

Si Git vous dit qu'il y a des conflits :

#### Conflit dans `backend/pom.xml`

1. **Ouvrir le fichier** `backend/pom.xml`
2. **Chercher les marqueurs de conflit** :
   ```
   <<<<<<< HEAD
   (votre code)
   =======
   (code du main)
   >>>>>>> origin/main
   ```

3. **Pour les modules** : Garder TOUS les modules
   ```xml
   <modules>
       <module>service-registry</module>
       <module>config-server</module>
       <module>api-gateway</module>
       <module>user-service</module>
       <module>schedule-service</module>
       <module>subscription-service</module>  <!-- VOTRE MODULE -->
   </modules>
   ```

4. **Pour les versions** : Garder VOS versions
   ```xml
   <spring-cloud.version>2024.0.0</spring-cloud.version>  <!-- Votre version -->
   <spring-kafka.version>3.3.0</spring-kafka.version>      <!-- Votre version -->
   ```

5. **Après résolution** :
   ```bash
   git add backend/pom.xml
   git commit -m "chore: resolve merge conflicts in pom.xml"
   ```

### Étape 5 : Tester après le merge

```bash
# Compiler
cd backend
mvn clean compile -pl subscription-service -am

# Tester
cd subscription-service
mvn test

# Vérifier que le service démarre
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Étape 6 : Push vers GitHub

```bash
# Push votre branche
git push origin votre-branche

# Si vous avez fait un rebase, forcer le push (ATTENTION!)
git push --force-with-lease origin votre-branche
```

---

## 📝 Créer une Pull Request sur GitHub

1. **Aller sur GitHub** : https://github.com/votre-repo/Urban-Transport-System
2. **Cliquer sur "Pull requests"**
3. **Cliquer sur "New pull request"**
4. **Sélectionner** :
   - Base: `main`
   - Compare: `votre-branche`
5. **Remplir le titre** : `feat: Add subscription-service microservice`
6. **Remplir la description** :
   ```markdown
   ## Description
   Ajout du microservice subscription-service pour la gestion des abonnements.

   ## Changements
   - ✅ Ajout du module subscription-service dans le POM parent
   - ✅ Configuration Spring Cloud 2024.0.0 et Spring Kafka 3.3.0
   - ✅ Gestion des plans d'abonnement
   - ✅ Gestion des abonnements utilisateurs
   - ✅ Traitement des paiements (Stripe + Mock)
   - ✅ Génération et validation de QR codes
   - ✅ Renouvellements automatiques
   - ✅ Tests unitaires et d'intégration
   - ✅ Documentation complète (README, API_DOCUMENTATION)

   ## Tests
   - [x] Tous les tests passent
   - [x] Service démarre correctement
   - [x] Compilation réussie

   ## Checklist
   - [x] Code compilé sans erreurs
   - [x] Tests passent
   - [x] Documentation à jour
   - [x] Pas de secrets hardcodés
   ```

7. **Créer la Pull Request**

---

## ⚠️ En cas de problème

### Erreur : "Your branch is behind"

```bash
git fetch origin
git merge origin/main
# Résoudre les conflits si nécessaire
git push origin votre-branche
```

### Erreur : "Merge conflict"

Suivre l'étape 4 ci-dessus pour résoudre les conflits.

### Erreur : "Push rejected"

```bash
# Si vous avez fait un rebase
git push --force-with-lease origin votre-branche

# OU mettre à jour d'abord
git pull origin votre-branche
git push origin votre-branche
```

---

## ✅ Checklist finale

- [ ] Tous les changements sont commités
- [ ] Branche mise à jour avec main
- [ ] Conflits résolus (si nécessaire)
- [ ] Tests passent
- [ ] Service compile et démarre
- [ ] Push effectué vers GitHub
- [ ] Pull Request créée

---

**Bonne chance avec le merge ! 🚀**

