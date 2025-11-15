# Étapes de Merge - Commandes à exécuter

## 📋 Étape 1 : Ajouter tous les fichiers

```bash
# Ajouter tous les fichiers modifiés et nouveaux
git add .

# Vérifier ce qui sera commité
git status
```

## 📝 Étape 2 : Commit les changements

```bash
git commit -m "feat: add subscription-service microservice

- Add subscription-service module to parent POM
- Configure Spring Cloud 2024.0.0 and Spring Kafka 3.3.0
- Add subscription management, payment processing, and QR code features
- Include comprehensive tests and documentation
- Add API documentation and merge guides"
```

## 🔄 Étape 3 : Mettre à jour avec main

```bash
# Récupérer les dernières modifications
git fetch origin

# Merger avec main
git merge origin/main
```

**Si conflits** : Suivre les instructions dans MERGE_GUIDE.md

## ✅ Étape 4 : Vérifier après merge

```bash
# Vérifier l'état
git status

# Tester la compilation (optionnel mais recommandé)
cd backend
mvn clean compile -pl subscription-service -am
```

## 🚀 Étape 5 : Push vers GitHub

```bash
# Push votre branche
git push origin subscription-service
```

## 📝 Étape 6 : Créer Pull Request sur GitHub

1. Aller sur GitHub
2. Cliquer sur "Pull requests" > "New pull request"
3. Base: `main`, Compare: `subscription-service`
4. Remplir la description
5. Créer la PR

---

**Exécutez ces commandes dans l'ordre !**

