package com.bibliotheque.repository;

import com.bibliotheque.exception.BibliothequeException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.TypeAdherent;
import org.sqlite.SQLiteConfig;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BibliothequeSQLiteRepository implements StockageBibliotheque {

    private static final String DISPONIBILITE = """
            NOT EXISTS (
                SELECT 1 FROM emprunts actifs
                WHERE actifs.isbn = l.isbn AND actifs.date_retour IS NULL
            ) AS disponible
            """;
    private static final String SELECTION_LIVRES =
            "SELECT l.isbn, l.titre, l.auteur, " + DISPONIBILITE + " FROM livres l";
    private static final String SELECTION_EMPRUNTS = """
            SELECT a.id, a.nom, a.prenom, a.email, a.type,
                   l.isbn, l.titre, l.auteur, e.date_emprunt, e.date_echeance, e.date_retour,
            """ + DISPONIBILITE + """
             FROM emprunts e
             JOIN adherents a ON a.id = e.adherent_id
             JOIN livres l ON l.isbn = e.isbn
            """;
    private final String url;

    public BibliothequeSQLiteRepository(Path fichier) {
        this.url = "jdbc:sqlite:" + Objects.requireNonNull(fichier).toAbsolutePath();
    }

    @Override
    public void initialiser(List<Livre> livres) throws SQLException {
        try (Connection connexion = ouvrirConnexion(); Statement sql = connexion.createStatement()) {
            sql.execute("BEGIN IMMEDIATE");
            verifierBaseCompatible(sql);
            creerSchema(sql);
            if (!catalogueInitialise(sql)) {
                insererCatalogue(connexion, livres);
                sql.executeUpdate("INSERT INTO parametres(cle) VALUES ('catalogue_initialise')");
            }
            sql.execute("COMMIT");
        }
    }

    private static void verifierBaseCompatible(Statement sql) throws SQLException {
        try (ResultSet resultat = sql.executeQuery("""
                SELECT name FROM sqlite_schema WHERE type = 'table'
                    AND name IN ('livres', 'adherents', 'emprunts', 'parametres')
                """)) {
            boolean contientTablesBibliotheque = false;
            while (resultat.next()) {
                if ("parametres".equals(resultat.getString("name"))) {
                    return;
                }
                contientTablesBibliotheque = true;
            }
            if (contientTablesBibliotheque) {
                throw new SQLException("Cette base provient de l'ancienne version. Utilisez un nouveau fichier SQLite.");
            }
        }
    }

    private static void creerSchema(Statement sql) throws SQLException {
        sql.executeUpdate("""
                CREATE TABLE IF NOT EXISTS livres (
                    isbn TEXT NOT NULL PRIMARY KEY COLLATE NOCASE,
                    titre TEXT NOT NULL,
                    auteur TEXT NOT NULL
                )
                """);
        sql.executeUpdate("""
                CREATE TABLE IF NOT EXISTS adherents (
                    id INTEGER PRIMARY KEY,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    type TEXT NOT NULL CHECK (type IN ('ETUDIANT', 'ENSEIGNANT'))
                )
                """);
        sql.executeUpdate("""
                CREATE TABLE IF NOT EXISTS emprunts (
                    id INTEGER PRIMARY KEY,
                    isbn TEXT NOT NULL COLLATE NOCASE REFERENCES livres(isbn),
                    adherent_id INTEGER NOT NULL REFERENCES adherents(id),
                    date_emprunt TEXT NOT NULL,
                    date_echeance TEXT NOT NULL CHECK (date_echeance >= date_emprunt),
                    date_retour TEXT CHECK (date_retour >= date_emprunt)
                )
                """);
        sql.executeUpdate("""
                CREATE UNIQUE INDEX IF NOT EXISTS un_emprunt_actif_par_livre
                ON emprunts(isbn) WHERE date_retour IS NULL
                """);
        sql.executeUpdate("CREATE INDEX IF NOT EXISTS emprunts_par_adherent ON emprunts(adherent_id)");
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS parametres (cle TEXT NOT NULL PRIMARY KEY)");
    }

    private static boolean catalogueInitialise(Statement sql) throws SQLException {
        try (ResultSet resultat = sql.executeQuery("SELECT 1 FROM parametres WHERE cle = 'catalogue_initialise'")) {
            return resultat.next();
        }
    }

    private static void insererCatalogue(Connection connexion, List<Livre> livres) throws SQLException {
        try (PreparedStatement sql = connexion.prepareStatement("INSERT INTO livres(isbn, titre, auteur) VALUES (?, ?, ?)")) {
            for (Livre livre : livres) {
                sql.setString(1, livre.getIsbn());
                sql.setString(2, livre.getTitre());
                sql.setString(3, livre.getAuteur());
                sql.addBatch();
            }
            sql.executeBatch();
        }
    }

    @Override
    public List<Livre> listerLivres() throws SQLException {
        try (Connection connexion = ouvrirConnexion(); Statement sql = connexion.createStatement();
             ResultSet resultat = sql.executeQuery(SELECTION_LIVRES + " ORDER BY l.titre COLLATE NOCASE, l.isbn")) {
            List<Livre> livres = new ArrayList<>();
            while (resultat.next()) {
                livres.add(lireLivre(resultat));
            }
            return List.copyOf(livres);
        }
    }

    @Override
    public Adherent inscrire(String nom, String prenom, String email, TypeAdherent type)
            throws SQLException, BibliothequeException {
        String requete = """
                INSERT INTO adherents(nom, prenom, email, type) VALUES (?, ?, ?, ?)
                ON CONFLICT(email) DO NOTHING RETURNING id
                """;
        try (Connection connexion = ouvrirConnexion(); PreparedStatement sql = connexion.prepareStatement(requete)) {
            sql.setString(1, nom);
            sql.setString(2, prenom);
            sql.setString(3, email);
            sql.setString(4, type.name());
            try (ResultSet resultat = sql.executeQuery()) {
                if (!resultat.next()) {
                    throw new BibliothequeException("Cette adresse e-mail est déjà utilisée.");
                }
                Adherent adherent = type.creer(resultat.getInt("id"), nom, prenom, email);
                resultat.next(); // Termine l'instruction d'écriture RETURNING.
                return adherent;
            }
        }
    }

    @Override
    public List<Adherent> listerAdherents() throws SQLException {
        try (Connection connexion = ouvrirConnexion(); Statement sql = connexion.createStatement();
             ResultSet resultat = sql.executeQuery("SELECT id, nom, prenom, email, type FROM adherents ORDER BY id")) {
            List<Adherent> adherents = new ArrayList<>();
            while (resultat.next()) {
                adherents.add(lireAdherent(resultat));
            }
            return List.copyOf(adherents);
        }
    }

    @Override
    public Emprunt emprunter(String isbn, int idAdherent, LocalDate date) throws SQLException, BibliothequeException {
        try (Connection connexion = ouvrirConnexion(); Statement transaction = connexion.createStatement()) {
            // Acquiert le verrou d'écriture avant de lire le quota et la disponibilité.
            transaction.execute("BEGIN IMMEDIATE");
            Adherent adherent = trouverAdherent(connexion, idAdherent);
            Livre livre = trouverLivre(connexion, isbn);
            if (!livre.estDisponible()) {
                throw new LivreIndisponibleException("Ce livre est déjà emprunté.");
            }
            if (!adherent.peutEncoreEmprunter(compterEmpruntsActifs(connexion, idAdherent))) {
                throw new BibliothequeException("Le quota d'emprunts de cet adhérent est atteint.");
            }

            Emprunt emprunt = Emprunt.ouvrir(adherent, livre, date);
            enregistrerEmprunt(connexion, emprunt);
            transaction.execute("COMMIT");
            return emprunt;
        }
    }

    private static int compterEmpruntsActifs(Connection connexion, int idAdherent) throws SQLException {
        try (PreparedStatement sql = connexion.prepareStatement(
                "SELECT COUNT(*) FROM emprunts WHERE adherent_id = ? AND date_retour IS NULL")) {
            sql.setInt(1, idAdherent);
            try (ResultSet resultat = sql.executeQuery()) {
                resultat.next();
                return resultat.getInt(1);
            }
        }
    }

    private static void enregistrerEmprunt(Connection connexion, Emprunt emprunt) throws SQLException {
        try (PreparedStatement sql = connexion.prepareStatement("""
                INSERT INTO emprunts(isbn, adherent_id, date_emprunt, date_echeance) VALUES (?, ?, ?, ?)
                """)) {
            sql.setString(1, emprunt.getLivre().getIsbn());
            sql.setInt(2, emprunt.getAdherent().getId());
            sql.setString(3, emprunt.getDateEmprunt().toString());
            sql.setString(4, emprunt.getDateEcheance().toString());
            sql.executeUpdate();
        }
    }

    @Override
    public Emprunt retourner(String isbn, LocalDate date) throws SQLException, BibliothequeException {
        try (Connection connexion = ouvrirConnexion(); Statement transaction = connexion.createStatement()) {
            transaction.execute("BEGIN IMMEDIATE");
            Livre livre = trouverLivre(connexion, isbn);
            Emprunt actif = trouverEmpruntActif(connexion, livre.getIsbn());
            Emprunt cloture = actif.cloturer(date);
            try (PreparedStatement sql = connexion.prepareStatement(
                    "UPDATE emprunts SET date_retour = ? WHERE isbn = ? AND date_retour IS NULL")) {
                sql.setString(1, cloture.getDateRetour().toString());
                sql.setString(2, livre.getIsbn());
                sql.executeUpdate();
            }
            transaction.execute("COMMIT");
            return cloture;
        }
    }

    private static Emprunt trouverEmpruntActif(Connection connexion, String isbn)
            throws SQLException, BibliothequeException {
        try (PreparedStatement sql = connexion.prepareStatement(
                SELECTION_EMPRUNTS + " WHERE e.isbn = ? AND e.date_retour IS NULL")) {
            sql.setString(1, isbn);
            try (ResultSet resultat = sql.executeQuery()) {
                if (!resultat.next()) {
                    throw new BibliothequeException("Ce livre n'a aucun emprunt en cours.");
                }
                return lireEmprunt(resultat);
            }
        }
    }

    @Override
    public List<Emprunt> historique(int idAdherent) throws SQLException, BibliothequeException {
        String requete = SELECTION_EMPRUNTS + " WHERE e.adherent_id = ? ORDER BY e.date_emprunt DESC, e.id DESC";
        try (Connection connexion = ouvrirConnexion(); PreparedStatement sql = connexion.prepareStatement(requete)) {
            trouverAdherent(connexion, idAdherent);
            sql.setInt(1, idAdherent);
            return lireEmprunts(sql);
        }
    }

    @Override
    public List<Emprunt> listerRetards(LocalDate date) throws SQLException {
        String requete = SELECTION_EMPRUNTS +
                " WHERE e.date_retour IS NULL AND e.date_echeance < ? ORDER BY e.date_echeance, e.id";
        try (Connection connexion = ouvrirConnexion(); PreparedStatement sql = connexion.prepareStatement(requete)) {
            sql.setString(1, date.toString());
            return lireEmprunts(sql);
        }
    }

    private static List<Emprunt> lireEmprunts(PreparedStatement sql) throws SQLException {
        try (ResultSet resultat = sql.executeQuery()) {
            List<Emprunt> emprunts = new ArrayList<>();
            while (resultat.next()) {
                emprunts.add(lireEmprunt(resultat));
            }
            return List.copyOf(emprunts);
        }
    }

    private static Livre trouverLivre(Connection connexion, String isbn) throws SQLException, LivreIntrouvableException {
        try (PreparedStatement sql = connexion.prepareStatement(SELECTION_LIVRES + " WHERE l.isbn = ?")) {
            sql.setString(1, isbn);
            try (ResultSet resultat = sql.executeQuery()) {
                if (!resultat.next()) {
                    throw new LivreIntrouvableException("Livre introuvable pour l'ISBN " + isbn + ".");
                }
                return lireLivre(resultat);
            }
        }
    }

    private static Adherent trouverAdherent(Connection connexion, int id) throws SQLException, BibliothequeException {
        try (PreparedStatement sql = connexion.prepareStatement(
                "SELECT id, nom, prenom, email, type FROM adherents WHERE id = ?")) {
            sql.setInt(1, id);
            try (ResultSet resultat = sql.executeQuery()) {
                if (!resultat.next()) {
                    throw new BibliothequeException("Aucun adhérent avec le numéro " + id + ".");
                }
                return lireAdherent(resultat);
            }
        }
    }

    private Connection ouvrirConnexion() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setBusyTimeout(5000);
        return config.createConnection(url);
    }

    private static Livre lireLivre(ResultSet resultat) throws SQLException {
        return new Livre(resultat.getString("isbn"), resultat.getString("titre"), resultat.getString("auteur"),
                resultat.getBoolean("disponible"));
    }

    private static Adherent lireAdherent(ResultSet resultat) throws SQLException {
        TypeAdherent type = TypeAdherent.valueOf(resultat.getString("type"));
        return type.creer(resultat.getInt("id"), resultat.getString("nom"), resultat.getString("prenom"),
                resultat.getString("email"));
    }

    private static Emprunt lireEmprunt(ResultSet resultat) throws SQLException {
        String retour = resultat.getString("date_retour");
        return new Emprunt(lireAdherent(resultat), lireLivre(resultat),
                LocalDate.parse(resultat.getString("date_emprunt")),
                LocalDate.parse(resultat.getString("date_echeance")),
                retour == null ? null : LocalDate.parse(retour));
    }
}
