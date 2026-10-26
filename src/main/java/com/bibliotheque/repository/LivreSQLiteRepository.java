
package com.bibliotheque.repository;

import com.bibliotheque.database.ConnexionSQLite;
import com.bibliotheque.model.Livre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivreSQLiteRepository {

    public void ajouter(Livre livre) throws SQLException {
        String sql = """
                INSERT INTO livres (isbn, titre, auteur, disponible)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connexion = ConnexionSQLite.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setString(1, livre.getIsbn());
            statement.setString(2, livre.getTitre());
            statement.setString(3, livre.getAuteur());
            statement.setInt(4, livre.estDisponible() ? 1 : 0);
            statement.executeUpdate();
        }
    }

    public List<Livre> listerTous() throws SQLException {
        List<Livre> livres = new ArrayList<>();

        String sql = """
                SELECT isbn, titre, auteur, disponible
                FROM livres
                ORDER BY titre
                """;

        try (Connection connexion = ConnexionSQLite.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultat = statement.executeQuery()) {

            while (resultat.next()) {
                livres.add(creerLivreDepuisResultat(resultat));
            }
        }

        return livres;
    }

    public List<Livre> rechercher(String recherche) throws SQLException {
        if (recherche == null || recherche.isBlank()) {
            return listerTous();
        }

        List<Livre> livres = new ArrayList<>();

        String sql = """
                SELECT isbn, titre, auteur, disponible
                FROM livres
                WHERE LOWER(titre) LIKE ?
                   OR LOWER(auteur) LIKE ?
                ORDER BY titre
                """;

        try (Connection connexion = ConnexionSQLite.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            String terme = "%" + recherche.trim().toLowerCase() + "%";
            statement.setString(1, terme);
            statement.setString(2, terme);

            try (ResultSet resultat = statement.executeQuery()) {
                while (resultat.next()) {
                    livres.add(creerLivreDepuisResultat(resultat));
                }
            }
        }

        return livres;
    }

    public void mettreAJourDisponibilite(
            String isbn, boolean disponible) throws SQLException {

        String sql = """
                UPDATE livres
                SET disponible = ?
                WHERE isbn = ?
                """;

        try (Connection connexion = ConnexionSQLite.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setInt(1, disponible ? 1 : 0);
            statement.setString(2, isbn);

            int lignesModifiees = statement.executeUpdate();

            if (lignesModifiees == 0) {
                throw new SQLException(
                        "Aucun livre trouvé avec l'ISBN : " + isbn);
            }
        }
    }

    private Livre creerLivreDepuisResultat(ResultSet resultat)
            throws SQLException {

        Livre livre = new Livre(
                resultat.getString("isbn"),
                resultat.getString("titre"),
                resultat.getString("auteur")
        );

        if (resultat.getInt("disponible") == 0) {
            livre.emprunter();
        }

        return livre;
    }
}
