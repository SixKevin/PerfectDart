# PerfectDart 🎯

API REST de gestion de joueurs et de parties de fléchettes (Spring Boot, Java 21, PostgreSQL).

## Avancement du binôme

| Membre | Travail pris en charge |
|---|---|
| Kevin ([SixKevin](https://github.com/SixKevin)) | Configuration de la base de données, entité et CRUD des joueurs |
| [gabbegue-coder](https://github.com/gabbegue-coder) | Entité `Game`, relation avec `Player` et CRUD des parties |

## Lancer l'API en local

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
| DELETE | `/games/{id}` | |
| POST | `/auth/login` | `{"pseudo":"nokiade","password":"mot-de-passe"}` |

Pour un POST ou un PUT dans Bruno, choisissez **Body → JSON** puis cliquez sur **Send**.

## Tests

Les tests utilisent une base H2 en mémoire et ne nécessitent pas de conteneur PostgreSQL :

```bash
./mvnw test
```
