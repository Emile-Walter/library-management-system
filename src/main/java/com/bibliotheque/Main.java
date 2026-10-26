
package com.bibliotheque;

import com.bibliotheque.database.ConnexionSQLite;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreSQLiteRepository;
import com.bibliotheque.service.BibliothequeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ConnexionSQLite.initialiserBase();

        LivreSQLiteRepository repository = new LivreSQLiteRepository();
        BibliothequeService service = new BibliothequeService();

        try {
            List<Livre> livresCharges = repository.listerTous();

            for (Livre livre : livresCharges) {
                service.ajouterLivre(livre);
            }
        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors du chargement des livres : "
                            + e.getMessage());
            return;
        }

        System.out.println("Bienvenue dans la bibliothèque !");
        lancerMenu(service, repository);
    }

    private static void lancerMenu(
            BibliothequeService service,
            LivreSQLiteRepository repository) {

        try (Scanner scanner = new Scanner(System.in)) {
            boolean continuer = true;

            while (continuer) {
                System.out.println("\n1. Afficher les livres");
                System.out.println("2. Emprunter un livre");
                System.out.println("3. Retourner un livre");
                System.out.println("0. Quitter");
                System.out.print("Choisissez une option : ");

                String choix = scanner.nextLine();

                switch (choix) {
                    case "1" -> afficherLivres(service);
                    case "2" -> emprunterLivre(
                            scanner, service, repository);
                    case "3" -> retournerLivre(
                            scanner, service, repository);
                    case "0" -> continuer = false;
                    default -> System.out.println(
                            "Option inconnue. Réessayez.");
                }
            }

            System.out.println("Au revoir !");
        }
    }

    private static void afficherLivres(
            BibliothequeService service) {

        for (Livre livre : service.getLivres()) {
            String disponibilite = livre.estDisponible()
                    ? "Disponible"
                    : "Emprunté";

            System.out.println(
                    livre.getIsbn() + " — "
                            + livre.getTitre() + " — "
                            + livre.getAuteur() + " — "
                            + disponibilite);
        }
    }

    private static void emprunterLivre(
            Scanner scanner,
            BibliothequeService service,
            LivreSQLiteRepository repository) {

        System.out.print("ISBN du livre à emprunter : ");
        String isbn = scanner.nextLine();

        try {
            service.emprunterLivre(isbn);

            repository.mettreAJourDisponibilite(isbn, false);

            System.out.println(
                    "Emprunt effectué et enregistré dans SQLite.");

        } catch (LivreIndisponibleException
                 | LivreIntrouvableException e) {

            System.out.println(e.getMessage());

        } catch (SQLException e) {
            System.out.println(
                    "Erreur lors de l'enregistrement : "
                            + e.getMessage());
        }
    }

    private static void retournerLivre(
            Scanner scanner,
            BibliothequeService service,
            LivreSQLiteRepository repository) {

        System.out.print("ISBN du livre à retourner : ");
        String isbn = scanner.nextLine();

        try {
            boolean retourReussi =
                    service.retournerLivre(isbn);

            if (retourReussi) {
                repository.mettreAJourDisponibilite(
                        isbn, true);

                System.out.println(
                        "Retour effectué et enregistré dans SQLite.");
            } else {
                System.out.println(
                        "Ce livre était déjà disponible.");
            }

        } catch (LivreIntrouvableException e) {
            System.out.println(e.getMessage());

        } catch (SQLException e) {
            System.out.println(
                    "Erreur lors de l'enregistrement : "
                            + e.getMessage());
        }
    }
}
