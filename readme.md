# PerfectDart 🎯

API REST de gestion de joueurs et de parties de fléchettes (Spring Boot, Java 21, PostgreSQL).

## Avancement du binôme

| Membre | Travail pris en charge |
|---|---|
| Kevin ([SixKevin](https://github.com/SixKevin)) | Configuration de la base de données, entité et CRUD des joueurs |
| [gabbegue-coder](https://github.com/gabbegue-coder) | Entité `Game`, relation avec `Player` et CRUD des parties |

## Lancer l'API en local

L'API utilise un secret pour signer les JWT. Définissez-en un dans le terminal avant de démarrer l'API ou Docker (ne réutilisez pas une valeur d'exemple en production) :

```bash
export JWT_SECRET="$(openssl rand -hex 32)"
```

Depuis la racine du projet, démarrez PostgreSQL dans un terminal :

```bash
docker compose up -d postgres
```

Le conteneur expose PostgreSQL sur `localhost:5433`. Lancez ensuite Spring Boot dans un autre terminal :

```bash
./mvnw spring-boot:run
```

L'API est disponible sur **http://localhost:8084**. Pour arrêter PostgreSQL :

```bash
docker compose stop postgres
```

## Tout lancer avec Docker

Cette commande démarre l'API, PostgreSQL et Adminer :

```bash
docker compose up --build
```

- API : http://localhost:8084
- Adminer : http://localhost:8081 (serveur `postgres`, utilisateur `postgres`, mot de passe `postgrespassword`, base `base_db`)

## Endpoints

Les exemples de corps JSON correspondent aux champs actuels des entités. `level` est un entier et le mot de passe est facultatif à la création d'un joueur.

| Méthode | URL | Corps JSON |
|---|---|---|
| GET | `/players` | |
| GET | `/players/{id}` | |
| GET | `/players/search?pseudo=nokiade` | |
| POST | `/players` | `{"pseudo":"nokiade","email":"nokiade@gmail.com","level":1}` |
| PUT | `/players/{id}` | `{"pseudo":"nokiade","email":"nokiade@gmail.com","level":1}` |
| DELETE | `/players/{id}` | |
| GET | `/games` | |
| GET | `/games/{id}` | |
| POST | `/games` | `{"mode":"501","statut":"EN_COURS"}` |
| PUT | `/games/{id}` | `{"mode":"501","statut":"TERMINE"}` |
| PUT | `/games/{id}/players` | `[1,2]` (remplace les joueurs associés à la partie) |
| DELETE | `/games/{id}` | |
| POST | `/auth/login` | `{"pseudo":"nokiade","password":"mot-de-passe"}` |

`GET /`, `GET /rules` et les routes `/players/**` sont accessibles sans token. Les routes `/games/**` nécessitent un JWT dans l'en-tête `Authorization`. Le mot de passe n'est jamais inclus dans les réponses JSON.

## Documentation Swagger

Quand l'API est lancée, la documentation interactive est disponible sur **http://localhost:8084/swagger-ui/index.html**. Elle permet de consulter les endpoints et d'essayer les requêtes.

Pour tester les routes `/games/**`, appelle d'abord `POST /auth/login` depuis Swagger UI. Copie la valeur `token` renvoyée, clique sur **Authorize**, puis colle le JWT (sans ajouter `Bearer ` : Swagger le préfixe automatiquement). Les routes joueurs sont publiques dans la configuration actuelle.

## Authentification

`POST /auth/login`, `GET /`, `GET /rules` et les routes `/players/**` sont accessibles sans authentification. Les routes `/games/**` nécessitent un JWT valide, valable une heure.

1. Se connecter pour obtenir un JWT :

```http
POST http://localhost:8084/auth/login
Content-Type: application/json

{"pseudo":"nokiade","password":"mot-de-passe"}
```

Réponse `200` : `{"token":"..."}`. Mauvais identifiants : `401`.

2. Envoyer ce token dans l'en-tête `Authorization` sur les routes protégées :

```http
GET http://localhost:8084/games
Authorization: Bearer <token>
```

Sans token, avec un token invalide ou expiré : `401`. Dans Bruno, onglet **Auth** de la requête, choisir **Bearer Token** et coller le token.

## Tests

Les tests utilisent une base H2 en mémoire et ne nécessitent pas de conteneur PostgreSQL :

```bash
./mvnw test
```
