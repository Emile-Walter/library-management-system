package com.bibliotheque.ui;

import com.bibliotheque.exception.BibliothequeException;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.service.BibliothequeService;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ConsoleBibliotheque {
    private static final String NOM_BIBLIOTHEQUE = "Walter Bibliothèque";

    private final BibliothequeService service;
    private final Scanner entree;
    private final PrintStream sortie;

    public ConsoleBibliotheque(BibliothequeService service, Scanner entree, PrintStream sortie) {
        this.service = service;
        this.entree = entree;
        this.sortie = sortie;
    }

    public void lancer() {
        sortie.println("Bienvenue à " + NOM_BIBLIOTHEQUE + " !");
        while (true) {
            afficherMenu();
            try {
                String choix = lire("Votre choix : ");
                if ("0".equals(choix)) {
                    break;
                }
                executer(choix);
            } catch (NoSuchElementException finDeSaisie) {
                break;
            } catch (BibliothequeException | IllegalArgumentException erreur) {
                sortie.println(erreur.getMessage());
            } catch (SQLException erreur) {
                sortie.println("Erreur SQLite : " + erreur.getMessage());
            }
        }
        sortie.println("Au revoir !");
    }

    private void afficherMenu() {
        sortie.println("""

                ===== %s =====
                1. Afficher le catalogue
                2. Emprunter un livre
                3. Retourner un livre
                4. Rechercher par titre, auteur ou identifiant
                5. Inscrire un adhérent
                6. Afficher les adhérents
                7. Afficher l'historique d'un adhérent
                8. Afficher les emprunts en retard
                0. Quitter""".formatted(NOM_BIBLIOTHEQUE));
    }

    private void executer(String choix) throws BibliothequeException, SQLException {
        switch (choix) {
            case "1" -> afficherLivres(service.listerLivres());
            case "2" -> emprunter();
            case "3" -> retourner();
            case "4" -> afficherLivres(service.rechercherLivres(lire("Titre, auteur ou identifiant : ")));
            case "5" -> inscrire();
            case "6" -> afficherAdherents(service.listerAdherents());
            case "7" -> afficherEmprunts(service.historique(lireIdentifiant()), "Aucun emprunt dans cet historique.");
            case "8" -> afficherEmprunts(service.listerRetards(), "Aucun emprunt en retard.");
            default -> sortie.println("Option inconnue. Réessayez.");
        }
    }

    private void afficherLivres(List<Livre> livres) {
        if (livres.isEmpty()) {
            sortie.println("Aucun livre trouvé.");
        }
        for (Livre livre : livres) {
            sortie.printf("%s | %s | %s | %s%n",
                    livre.getIsbn(), livre.getTitre(), livre.getAuteur(),
                    livre.estDisponible() ? "Disponible" : "Emprunté");
        }
    }

    private void emprunter() throws BibliothequeException, SQLException {
        String isbn = lire("Identifiant du livre à emprunter : ");
        int identifiant = lireIdentifiant();
        Emprunt emprunt = service.emprunterLivre(isbn, identifiant);
        sortie.printf("Emprunt enregistré pour « %s ». Date d'échéance %s.%n",
                emprunt.getLivre().getTitre(), emprunt.getDateEcheance());
    }

    private void retourner() throws BibliothequeException, SQLException {
        Emprunt emprunt = service.retournerLivre(lire("Identifiant du livre à retourner : "));
        sortie.printf("Retour enregistré pour « %s » le %s.%n",
                emprunt.getLivre().getTitre(), emprunt.getDateRetour());
    }

    private void inscrire() throws BibliothequeException, SQLException {
        String nom = lire("Nom : ");
        String prenom = lire("Prénom : ");
        String email = lire("Adresse e-mail : ");
        TypeAdherent type = switch (lire("Profil (1. Étudiant, 2. Enseignant) : ")) {
            case "1" -> TypeAdherent.ETUDIANT;
            case "2" -> TypeAdherent.ENSEIGNANT;
            default -> throw new IllegalArgumentException("Choisissez le profil 1 ou 2.");
        };
        Adherent adherent = service.inscrire(nom, prenom, email, type);
        sortie.printf("Adhérent inscrit. Identifiant %d, profil %s.%n",
                adherent.getId(), adherent.getRole());
    }

    private void afficherAdherents(List<Adherent> adherents) {
        if (adherents.isEmpty()) {
            sortie.println("Aucun adhérent inscrit.");
        }
        for (Adherent adherent : adherents) {
            sortie.printf("%d | %s %s | %s | %s%n",
                    adherent.getId(), adherent.getPrenom(), adherent.getNom(),
                    adherent.getRole(), adherent.getEmail());
        }
    }

    private void afficherEmprunts(List<Emprunt> emprunts, String messageSiVide) {
        if (emprunts.isEmpty()) {
            sortie.println(messageSiVide);
        }
        for (Emprunt emprunt : emprunts) {
            Livre livre = emprunt.getLivre();
            Adherent adherent = emprunt.getAdherent();
            String etat = emprunt.estEnCours() ? "En cours" : "Rendu le " + emprunt.getDateRetour();
            sortie.printf("%s | %s | %s %s (n° %d) | Emprunté le %s | Échéance %s | %s%n",
                    livre.getIsbn(), livre.getTitre(), adherent.getPrenom(), adherent.getNom(),
                    adherent.getId(), emprunt.getDateEmprunt(), emprunt.getDateEcheance(), etat);
        }
    }

    private int lireIdentifiant() {
        String saisie = lire("Identifiant de l'adhérent : ");
        try {
            int identifiant = Integer.parseInt(saisie);
            if (identifiant > 0) {
                return identifiant;
            }
        } catch (NumberFormatException erreur) {
            throw new IllegalArgumentException("Saisissez un identifiant entier positif.", erreur);
        }
        throw new IllegalArgumentException("Saisissez un identifiant entier positif.");
    }

    private String lire(String invite) {
        sortie.print(invite);
        sortie.flush();
        return entree.nextLine().strip();
    }
}
