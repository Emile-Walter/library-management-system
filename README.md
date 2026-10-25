# Gestion de bibliothèque

Application console Java permettant de consulter les livres d'une bibliothèque,
de les emprunter et de les retourner.

## Fonctionnalités

- Chargement des livres depuis un fichier CSV au démarrage
- Affichage des livres et de leur disponibilité
- Emprunt et retour d'un livre à partir de son ISBN
- Messages d'erreur pour un livre introuvable ou déjà emprunté
- Tests automatisés avec JUnit 5

## Technologies

- Java 17
- Maven
- JUnit 5

## Lancer le programme

1. Ouvrir le projet dans IntelliJ IDEA.
2. Vérifier que le projet utilise un JDK 17.
3. Ouvrir `src/main/java/com/bibliotheque/Main.java`.
4. Faire un clic droit dans le fichier et choisir **Run 'Main.main()'**.

## Utiliser le menu

- Entrer `1` pour afficher les livres.
- Entrer `2` pour emprunter un livre, puis saisir son ISBN.
- Entrer `3` pour retourner un livre, puis saisir son ISBN.
- Entrer `0` pour quitter.

## Fichier des livres

Les données sont dans `src/main/resources/livres.csv`.

Le fichier utilise ce format :

```csv
isbn,titre,auteur
ISBN-001,Le Petit Prince,Antoine de Saint-Exupéry
ISBN-002,Les Misérables,Victor Hugo
ISBN-003,Candide,Voltaire