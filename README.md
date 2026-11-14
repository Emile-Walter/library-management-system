# Walter Bibliothèque

Walter Bibliothèque est un projet de programmation orientée objet en Java 17. L’application console permet de consulter un catalogue, d’inscrire des adhérents et de suivre les emprunts et les retards. Le catalogue de démonstration contient six livres fictifs, chacun en un exemplaire.

## Démarrage rapide

1. Sur cette page GitHub, choisissez **Code → Download ZIP**, puis extrayez le ZIP.
2. Dans IntelliJ IDEA, ouvrez le dossier qui contient le [`pom.xml`](pom.xml), sélectionnez **Java 17** comme SDK du projet et attendez l’import Maven.
3. Ouvrez [`Main.java`](src/main/java/com/bibliotheque/Main.java) et lancez `Main.main()` avec la flèche verte. Si IntelliJ utilise un autre JDK, choisissez **Java 17** dans le champ **JRE** de **Run → Edit Configurations → Main**.

Depuis un terminal avec le JDK 17 et Maven, lancez `mvn compile exec:java` dans le dossier qui contient `pom.xml`. Une connexion est nécessaire au premier lancement pour télécharger les dépendances si elles sont absentes du cache Maven.

L’application crée `bibliotheque-beta.db` au démarrage pour conserver les adhérents et les emprunts.

## Essayer les fonctionnalités

Dans la console, validez chaque saisie avec **Entrée**.

1. Tapez `1` pour afficher le catalogue. L’option `4` recherche par titre, auteur ou identifiant.
2. Tapez `5` et saisissez un nom, un prénom, un e-mail et le profil `1` (étudiant) ou `2` (enseignant). Notez l’identifiant attribué. L’option `6` affiche les adhérents inscrits.
3. Tapez `2`, puis `FIC-001` et l’identifiant de l’adhérent. L’application affiche l’échéance. L’option `8` liste les prêts actifs en retard.
4. Tapez `3`, puis `FIC-001` pour rendre le livre. Tapez `7`, puis l’identifiant de l’adhérent pour consulter l’historique. Tapez `0` pour quitter.

Un étudiant peut avoir jusqu’à trois emprunts actifs pendant 14 jours chacun. Un enseignant peut en avoir jusqu’à cinq pendant 28 jours chacun. Une fois le livre rendu, il redevient disponible et l’emprunt reste dans l’historique.

## Classes et choix techniques

1. **Responsabilités distinctes.** [`Main`](src/main/java/com/bibliotheque/Main.java) assemble l’application, [`ConsoleBibliotheque`](src/main/java/com/bibliotheque/ui/ConsoleBibliotheque.java) gère le menu et [`BibliothequeService`](src/main/java/com/bibliotheque/service/BibliothequeService.java) orchestre les opérations. Le service reçoit l’interface [`StockageBibliotheque`](src/main/java/com/bibliotheque/repository/StockageBibliotheque.java) et une horloge injectable. Cela applique la responsabilité unique et l’inversion des dépendances, et permet de tester les échéances avec une date fixe.
2. **Héritage et polymorphisme.** Les classes abstraites [`Personne`](src/main/java/com/bibliotheque/model/Personne.java) et [`Adherent`](src/main/java/com/bibliotheque/model/Adherent.java) regroupent les coordonnées et les règles communes. [`Etudiant`](src/main/java/com/bibliotheque/model/Etudiant.java) et [`Enseignant`](src/main/java/com/bibliotheque/model/Enseignant.java) définissent chacun leur quota et leur durée. Le contrôle du quota et le calcul de l’échéance utilisent le contrat d’`Adherent` pour les deux profils, ce qui illustre le principe de substitution de Liskov. Java exécute les méthodes correspondant au profil réel.
3. **Catalogue CSV.** [`LivreCsvRepository`](src/main/java/com/bibliotheque/repository/LivreCsvRepository.java) implémente [`LivreRepository`](src/main/java/com/bibliotheque/repository/LivreRepository.java) et transforme le fichier [`livres.csv`](src/main/resources/livres.csv) en objets [`Livre`](src/main/java/com/bibliotheque/model/Livre.java) à chaque démarrage. Le fichier UTF-8 contient les colonnes `isbn,titre,auteur`. Les identifiants sont fictifs et chaque champ utilise du texte simple sans virgule. Le catalogue est enregistré au premier démarrage réussi de chaque base. Après une modification du CSV, lancez l’application avec un nouveau fichier de base pour charger le catalogue modifié.
4. **Persistance SQLite.** [`BibliothequeSQLiteRepository`](src/main/java/com/bibliotheque/repository/BibliothequeSQLiteRepository.java) conserve le catalogue, les adhérents et les objets [`Emprunt`](src/main/java/com/bibliotheque/model/Emprunt.java). Il vérifie la disponibilité et le quota dans la même transaction que l’enregistrement de l’emprunt. La disponibilité découle de la présence d’un emprunt actif, ce qui évite de stocker cette information deux fois.

## Exceptions

[`BibliothequeException`](src/main/java/com/bibliotheque/exception/BibliothequeException.java) représente les erreurs métier, par exemple un quota atteint, une adresse déjà utilisée ou un retour sans emprunt actif. [`LivreIntrouvableException`](src/main/java/com/bibliotheque/exception/LivreIntrouvableException.java) et [`LivreIndisponibleException`](src/main/java/com/bibliotheque/exception/LivreIndisponibleException.java) précisent les erreurs liées aux livres. Le menu affiche les erreurs de saisie, métier et SQLite.

## Tests

Dans le dossier qui contient `pom.xml`, exécutez la commande suivante.

```bash
mvn test
```

Les tests JUnit couvrent le [service et les opérations d’emprunt](src/test/java/com/bibliotheque/service/BibliothequeServiceTest.java), le [modèle et les dates](src/test/java/com/bibliotheque/model/EmpruntTest.java) et le [chargement du catalogue](src/test/java/com/bibliotheque/repository/LivreCsvRepositoryTest.java).
