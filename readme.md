# PerfectDart 🎯

API REST de comptage de points, de scores et de statistiques de fléchettes (R3.03, Spring Boot).

## Binôme

| Membre | Tâches réalisées (Sprint 1) |
|---|---|
| **NOM Prénom** (Kevin) : [SixKevin](https://github.com/SixKevin) | Configuration de la base H2, arborescence des packages, entité `Player`, CRUD joueurs (`PlayerRepository`, `PlayerService`, `PlayerController`) |
| **NOM Prénom** : [gabbegue-coder](https://github.com/gabbegue-coder) | README, entité `Game` (relation `@ManyToMany` avec `Player`), CRUD parties (`GameRepository`, `GameService`, `GameController`) |

## Lancer l'API

```bash
git clone https://github.com/SixKevin/PerfectDart.git
cd PerfectDart
./mvnw spring-boot:run
```

L'API tourne sur **http://localhost:8084**.

## Connexion avec Bruno

1. Lancer l'API (commande ci-dessus).
2. Dans Bruno : **New Collection** → `PerfectDart`.
3. Créer une requête avec l'URL de base `http://localhost:8084` :

| Requête | URL | Body (JSON) |
|---|---|---|
| GET | `/players` | |
| POST | `/players` | `{"pseudo":"nokiade","email":"nokiade@gmail.com","niveau":"debutant"}` |
| GET | `/games` | |
| POST | `/games` | `{"mode":"501","statut":"EN_COURS"}` |

Pour un POST : choisir **Body → JSON**, puis cliquer sur **Send** (réponse attendue : `201`).