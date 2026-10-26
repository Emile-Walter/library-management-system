
# Gestion de bibliothèque

Application console Java permettant de consulter les livres d'une bibliothèque,
d'emprunter des livres et de les retourner.

## Fonctionnalités

- Affichage des livres et de leur disponibilité
- Emprunt et retour d'un livre à partir de son ISBN
- Gestion des erreurs : livre introuvable ou déjà emprunté
- Chargement des livres depuis SQLite au démarrage
- Enregistrement de la disponibilité des livres dans SQLite
- Chargement et recherche de livres depuis un fichier CSV
- Tests automatisés avec JUnit 5

## Technologies

- Java 17
- Maven
- SQLite
- JUnit 5
- IntelliJ IDEA

## Prérequis

- JDK 17
- Maven, ou Maven intégré à IntelliJ IDEA

## Lancer l'application

1. Ouvrir le projet dans IntelliJ IDEA.
2. Vérifier que le projet utilise Java 17.
3. Attendre que Maven télécharge les dépendances.
4. Ouvrir `src/main/java/com/bibliotheque/Main.java`.
5. Faire un clic droit et choisir `Run 'Main.main()'`.

La base de données SQLite est initialisée au démarrage.

## Utiliser le menu

- Entrer `1` pour afficher les livres.
- Entrer `2` pour emprunter un livre, puis saisir son ISBN.
- Entrer `3` pour retourner un livre, puis saisir son ISBN.
- Entrer `0` pour quitter.

## Fichier CSV

Le fichier de livres est situé dans :

`src/main/resources/livres.csv`

Format attendu :

```csv
isbn,titre,auteur
ISBN-001,Le Petit Prince,Antoine de Saint-Exupéry
ISBN-002,Les Misérables,Victor Hugo
ISBN-003,Candide,Voltaire
```

La première ligne contient les noms des colonnes.

## Base de données

Le projet utilise SQLite. Le fichier `bibliotheque.db` est créé à partir de la configuration de connexion.

La table `livres` conserve notamment l'ISBN, le titre, l'auteur et la disponibilité.

## Exécuter les tests

Dans IntelliJ IDEA :

1. Ouvrir `src/test/java`.
2. Faire un clic droit sur le dossier ou sur une classe de test.
3. Choisir `Run`.

Les tests utilisent JUnit 5.

## Compiler le projet avec Maven

Dans le terminal à la racine du projet :

```bash
mvn clean test
```

Cette commande nettoie les fichiers compilés et exécute les tests.

## Structure du projet

- `model` : classes représentant les données, notamment `Livre`
- `service` : logique métier de la bibliothèque
- `repository` : accès aux données CSV et SQLite
- `database` : connexion et initialisation SQLite
- `exception` : exceptions personnalisées
- `Main.java` : point d'entrée de l'application
- `src/test/java` : tests automatisés

## Auteur

Projet réalisé dans le cadre de l'apprentissage de la programmation orientée objet en Java.
