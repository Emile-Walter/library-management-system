
package com.bibliotheque.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConnexionSQLite {

    private static final String URL =
            "jdbc:sqlite:bibliotheque.db";

    // Ouvrir une connexion à la base de données
    public static Connection obtenirConnexion()
            throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Initialiser les tables de la base de données
    public static void initialiserBase() {
        String livres = """
                CREATE TABLE IF NOT EXISTS livres (
                    isbn TEXT PRIMARY KEY,
                    titre TEXT NOT NULL,
                    auteur TEXT NOT NULL,
                    disponible INTEGER NOT NULL DEFAULT 1
                )
                """;

        String adherents = """
                CREATE TABLE IF NOT EXISTS adherents (
                    id INTEGER PRIMARY KEY,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT NOT NULL
                )
                """;

        String emprunts = """
                CREATE TABLE IF NOT EXISTS emprunts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    adherent_id INTEGER NOT NULL,
                    isbn TEXT NOT NULL,
                    date_emprunt TEXT NOT NULL,
                    date_retour TEXT,
                    FOREIGN KEY (adherent_id)
                        REFERENCES adherents(id),
                    FOREIGN KEY (isbn)
                        REFERENCES livres(isbn)
                )
                """;

        try (Connection connexion = obtenirConnexion()) {
            try (Statement statement = connexion.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");

                statement.execute(livres);
                statement.execute(adherents);
                statement.execute(emprunts);
            }

            ajouterColonneDisponible(connexion);

            System.out.println(
                    "Base de données initialisée avec succès !"
            );

        } catch (SQLException e) {
            System.err.println(
                    "Erreur SQLite : " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    // Ajouter disponible uniquement si la colonne manque
    private static void ajouterColonneDisponible(
            Connection connexion) throws SQLException {

        boolean colonneExiste = false;

        try (Statement statement = connexion.createStatement();
             ResultSet resultat =
                     statement.executeQuery("PRAGMA table_info(livres)")) {

            while (resultat.next()) {
                String nomColonne = resultat.getString("name");

                if ("disponible".equalsIgnoreCase(nomColonne)) {
                    colonneExiste = true;
                    break;
                }
            }
        }

        if (!colonneExiste) {
            try (Statement statement = connexion.createStatement()) {
                statement.execute(
                        "ALTER TABLE livres "
                                + "ADD COLUMN disponible "
                                + "INTEGER NOT NULL DEFAULT 1"
                );

                System.out.println(
                        "Colonne disponible ajoutée avec succès."
                );
            }
        }
    }

    // Vérifier le fonctionnement de la base
    public static void main(String[] args) {
        initialiserBase();
    }
}
