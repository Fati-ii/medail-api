# Système de Suivi des Médailles Olympiques

Une application backend Spring Boot complète développée pour gérer les athlètes, compétitions et médailles olympiques de manière robuste et performante.

## Configuration et Lancement
1. Assurez-vous d'avoir une instance **MySQL** fonctionnant sur `localhost:3306`. L'application se chargera de créer automatiquement la base de données `medailles_olympiques` (grâce à l'option `createDatabaseIfNotExist=true`).
2. Ouvrez le fichier `src/main/resources/application.properties` et mettez à jour (si nécessaire) le nom d'utilisateur `root` et le mot de passe de votre base de données.
3. À la racine du projet (`medail`), compilez et démarrez le projet via Maven :
   ```
   mvn clean install
   mvn spring-boot:run
   ```
   L'application démarrera par défaut sur le port **8000**.

##  Points d'Entrée REST (Endpoints)

L'API utilise les bonnes pratiques de routage REST et valide l'intégrité des données entrantes.

### Pays (`/api/v1/pays`)
- `GET /` : Liste complète des pays
- `POST /` : Enregistrer un nouveau pays
- `GET /{id}` : Obtenir un pays spécifique

### Athlètes (`/api/v1/athletes`)
- `GET /` : Liste de tous les sportifs
- `POST /` : Immatriculer un nouvel athlète (Requiert un `paysId` valide)
- `GET /pays/{paysId}` : Trouver tous les athlètes de la délégation d'un pays.

### Compétitions (`/api/v1/competitions`)
- `GET /` : Visualiser le calendrier sportif
- `POST /` : Planifier une nouvelle compétition

### Médailles (`/api/v1/medailles`)
- `POST /` : Attribuer une médaille en temps réel. Requiert l'identifiant de la compétition, l'athlète et le niveau de la récompense (OR, ARGENT, BRONZE).
- `GET /athlete/{athleteId}` : Historique des médailles gagnées par ce participant.

### Analytiques Classements (`/api/v1/classement`)
- `GET /` : Tableau officiel des médailles trié par le total des victoires (par défaut).
- `GET /?tri=or` : Classement qualitatif centré sur les médailles d'or.
- `GET /?tri=points` : Classement pondéré (Or = 3 pts, Argent = 2 pts, Bronze = 1 pt).
- `GET /pays/{paysId}` : Données et statistiques ciblées d'une délégation en particulier.

## Tester l'API avec Postman

Une collection Postman a été générée pour vous permettre de tester facilement tous les endpoints de l'API.
**Fichier :** `Medail_API_Postman_Collection.json` (à la racine du projet)

1. Ouvrez Postman.
2. Cliquez sur **Import** en haut à gauche.
3. Sélectionnez le fichier `Medail_API_Postman_Collection.json`.
4. Tous les endpoints (Pays, Athlètes, Compétitions, Médailles, Classement) seront disponibles en tant que collection prête à l'emploi.

