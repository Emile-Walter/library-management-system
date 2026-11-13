# Walter Bibliothèque

Application console en Java 17 pour gérer les adhérents et les emprunts d’une bibliothèque. Cette bêta locale contient six livres fictifs, chacun disponible en un seul exemplaire.

## Lancer l’application

**Dans IntelliJ IDEA** ouvrez le dossier qui contient le [`pom.xml`](pom.xml), sélectionnez le JDK 17 et laissez Maven importer les dépendances. Ouvrez ensuite [`Main.java`](src/main/java/com/bibliotheque/Main.java) et lancez `Main.main()` avec la flèche verte.

**Dans un terminal** avec le JDK 17 et Maven, exécutez cette commande depuis le dossier du projet. Le premier lancement peut nécessiter une connexion pour télécharger les dépendances et les plugins.

```bash
mvn compile exec:java
```

L’application crée `bibliotheque-beta.db` dans le dossier courant et conserve les adhérents et les emprunts entre deux lancements. L’ancienne base `bibliotheque.db` utilise un schéma différent. Gardez-la à part et utilisez la base de la bêta. Pour essayer une nouvelle base, choisissez un nom de fichier encore libre dans cette commande PowerShell.

```powershell
mvn compile exec:java '-Dexec.args=autre-bibliotheque.db'
```

Sous Windows, si le plugin `exec` ne peut pas être téléchargé et que Maven a déjà mis le pilote SQLite en cache, vous pouvez lancer le projet directement avec Java. Ces commandes PowerShell utilisent le dossier Maven standard.

```powershell
mvn -o compile
$sqlite = Join-Path $env:USERPROFILE '.m2/repository/org/xerial/sqlite-jdbc/3.49.1.0/sqlite-jdbc-3.49.1.0.jar'
java '-Dfile.encoding=UTF-8' -cp "target/classes;$sqlite" com.bibliotheque.Main
```

## Essayer les fonctionnalités

1. **Catalogue et recherche.** Tapez `1` pour afficher les livres ou `4` pour rechercher par titre, auteur ou identifiant. La recherche ignore la casse et les accents.
2. **Inscription.** Tapez `5`, puis saisissez un nom, un prénom, une adresse e-mail et un profil (`1` étudiant ou `2` enseignant). Notez l’identifiant attribué ou retrouvez-le avec `6`.
3. **Emprunt et retour.** Tapez `2`, puis saisissez `FIC-001` et l’identifiant de l’adhérent. Utilisez `7` suivi de son identifiant pour consulter l’historique. Tapez `3`, puis `FIC-001`, pour rendre le livre.
4. **Retards.** Tapez `8` pour afficher les emprunts encore actifs dont l’échéance est dépassée. Tapez `0` pour quitter.

Un étudiant peut avoir jusqu’à trois emprunts actifs pendant 14 jours chacun. Un enseignant peut en avoir jusqu’à cinq pendant 28 jours chacun. Une fois le livre rendu, il redevient disponible et l’emprunt reste dans l’historique.

## Classes et choix techniques

1. **Responsabilités distinctes.** [`Main`](src/main/java/com/bibliotheque/Main.java) assemble l’application, [`ConsoleBibliotheque`](src/main/java/com/bibliotheque/ui/ConsoleBibliotheque.java) gère le menu et [`BibliothequeService`](src/main/java/com/bibliotheque/service/BibliothequeService.java) orchestre les opérations. Le service reçoit l’interface [`StockageBibliotheque`](src/main/java/com/bibliotheque/repository/StockageBibliotheque.java) et une horloge injectable. Cela applique la responsabilité unique et l’inversion des dépendances, et permet de tester les échéances avec une date fixe.
2. **Héritage et polymorphisme.** Les classes abstraites [`Personne`](src/main/java/com/bibliotheque/model/Personne.java) et [`Adherent`](src/main/java/com/bibliotheque/model/Adherent.java) regroupent les coordonnées et les règles communes. [`Etudiant`](src/main/java/com/bibliotheque/model/Etudiant.java) et [`Enseignant`](src/main/java/com/bibliotheque/model/Enseignant.java) définissent chacun leur quota et leur durée. Le contrôle du quota et le calcul de l’échéance utilisent le contrat d’`Adherent` pour les deux profils, ce qui illustre le principe de substitution de Liskov. Java exécute les méthodes correspondant au profil réel.
3. **Catalogue CSV.** [`LivreCsvRepository`](src/main/java/com/bibliotheque/repository/LivreCsvRepository.java) implémente [`LivreRepository`](src/main/java/com/bibliotheque/repository/LivreRepository.java) et transforme le fichier [`livres.csv`](src/main/resources/livres.csv) en objets [`Livre`](src/main/java/com/bibliotheque/model/Livre.java) à chaque démarrage. Le fichier UTF-8 contient les colonnes `isbn,titre,auteur`. Les identifiants sont fictifs et chaque champ utilise du texte simple sans virgule. Le catalogue est enregistré au premier démarrage réussi de chaque base. Après une modification du CSV, lancez l’application avec un nouveau fichier de base pour charger le catalogue modifié.
4. **Persistance SQLite.** [`BibliothequeSQLiteRepository`](src/main/java/com/bibliotheque/repository/BibliothequeSQLiteRepository.java) conserve le catalogue, les adhérents et les objets [`Emprunt`](src/main/java/com/bibliotheque/model/Emprunt.java). Il vérifie la disponibilité et le quota dans la même transaction que l’enregistrement de l’emprunt. La disponibilité découle de la présence d’un emprunt actif, ce qui évite de stocker cette information deux fois.

## Exceptions

[`BibliothequeException`](src/main/java/com/bibliotheque/exception/BibliothequeException.java) représente les erreurs métier, par exemple un quota atteint, une adresse déjà utilisée ou un retour sans emprunt actif. [`LivreIntrouvableException`](src/main/java/com/bibliotheque/exception/LivreIntrouvableException.java) et [`LivreIndisponibleException`](src/main/java/com/bibliotheque/exception/LivreIndisponibleException.java) précisent les erreurs liées aux livres. Le menu affiche les erreurs de saisie, métier et SQLite.

## Tests

```bash
mvn test
```

Les tests JUnit couvrent le [service et les opérations d’emprunt](src/test/java/com/bibliotheque/service/BibliothequeServiceTest.java), le [modèle et les dates](src/test/java/com/bibliotheque/model/EmpruntTest.java) et le [chargement du catalogue](src/test/java/com/bibliotheque/repository/LivreCsvRepositoryTest.java).
