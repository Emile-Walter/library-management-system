
package com.bibliotheque;

import com.bibliotheque.database.ConnexionSQLite;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreSQLiteRepository;

import java.sql.SQLException;
import java.util.List;

public class TestSQLite {

    public static void main(String[] args) {
        ConnexionSQLite.initialiserBase();

        LivreSQLiteRepository repository = new LivreSQLiteRepository();

        String isbn = "TEST-004";

        try {
            // Vérifier si le livre existe déjà
            List<Livre> livres = repository.listerTous();

            boolean existe = livres.stream()
                    .anyMatch(livre -> livre.getIsbn().equals(isbn));

            if (existe) {
                System.out.println(
                        "Le livre " + isbn + " existe déjà dans la base."
                );
            } else {
                Livre livre = new Livre(
                        isbn,
                        "Mon premier livre",
                        "Auteur Test"
                );

                repository.ajouter(livre);
                System.out.println("Livre enregistré avec succès !");
            }

            // Afficher tous les livres
            System.out.println("\nListe des livres enregistrés :");

            for (Livre livre : repository.listerTous()) {
                System.out.println(
                        livre.getIsbn() + " - "
                                + livre.getTitre() + " - "
                                + livre.getAuteur()
                );
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite : " + e.getMessage());
        }
    }
}
