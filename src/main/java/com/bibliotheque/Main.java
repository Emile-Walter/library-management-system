package com.bibliotheque;

import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreCsvRepository;
import com.bibliotheque.repository.LivreRepository;
import com.bibliotheque.service.BibliothequeService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        LivreRepository repository = new LivreCsvRepository();
        BibliothequeService service = new BibliothequeService();

        try {
            List<Livre> livresCharges = repository.chargerLivres();

            for (Livre livre : livresCharges) {
                service.ajouterLivre(livre);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du CSV : " + e.getMessage());
            return;
        }

        System.out.println("Bienvenue dans la bibliothèque !");
        lancerMenu(service);
    }

    private static void lancerMenu(BibliothequeService service) {
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
                    case "2" -> emprunterLivre(scanner, service);
                    case "3" -> retournerLivre(scanner, service);
                    case "0" -> continuer = false;
                    default -> System.out.println("Option inconnue. Réessayez.");
                }
            }

            System.out.println("Au revoir !");
        }
    }

    private static void afficherLivres(BibliothequeService service) {
        for (Livre livre : service.getLivres()) {
            String disponibilite = livre.estDisponible()
                    ? "Disponible"
                    : "Emprunté";

            System.out.println(
                    livre.getIsbn() + " — "
                            + livre.getTitre() + " — "
                            + livre.getAuteur() + " — "
                            + disponibilite
            );
        }
    }

    private static void emprunterLivre(
            Scanner scanner,
            BibliothequeService service
    ) {
        System.out.print("ISBN du livre à emprunter : ");
        String isbn = scanner.nextLine();

        try {
            service.emprunterLivre(isbn);
            System.out.println("Emprunt effectué.");
        } catch (LivreIndisponibleException | LivreIntrouvableException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void retournerLivre(
            Scanner scanner,
            BibliothequeService service
    ) {
        System.out.print("ISBN du livre à retourner : ");
        String isbn = scanner.nextLine();

        try {
            boolean retourReussi = service.retournerLivre(isbn);

            if (retourReussi) {
                System.out.println("Retour effectué.");
            } else {
                System.out.println("Ce livre était déjà disponible.");
            }
        } catch (LivreIntrouvableException e) {
            System.out.println(e.getMessage());
        }
    }
}