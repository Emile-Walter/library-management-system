package com.bibliotheque.service;

import com.bibliotheque.exception.BibliothequeException;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.repository.StockageBibliotheque;

import java.sql.SQLException;
import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class BibliothequeService {
    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+");
    private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");

    private final StockageBibliotheque stockage;
    private final Clock horloge;

    public BibliothequeService(StockageBibliotheque stockage, Clock horloge) {
        this.stockage = Objects.requireNonNull(stockage);
        this.horloge = Objects.requireNonNull(horloge);
    }

    public List<Livre> listerLivres() throws SQLException {
        return stockage.listerLivres();
    }

    public List<Livre> rechercherLivres(String recherche) throws SQLException {
        String motCle = normaliserRecherche(texteObligatoire(recherche, "Le mot-clé"));
        if (motCle.isBlank()) {
            throw new IllegalArgumentException("Le mot-clé est obligatoire.");
        }
        return listerLivres().stream()
                .filter(livre -> normaliserRecherche(livre.getIsbn()).contains(motCle)
                        || normaliserRecherche(livre.getTitre()).contains(motCle)
                        || normaliserRecherche(livre.getAuteur()).contains(motCle))
                .toList();
    }

    public Adherent inscrire(String nom, String prenom, String email, TypeAdherent type)
            throws SQLException, BibliothequeException {
        String nomValide = texteObligatoire(nom, "Le nom");
        String prenomValide = texteObligatoire(prenom, "Le prénom");
        String emailNormalise = texteObligatoire(email, "L'adresse e-mail").toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(emailNormalise).matches()) {
            throw new IllegalArgumentException("L'adresse e-mail est invalide.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le profil d'adhérent est obligatoire.");
        }
        return stockage.inscrire(nomValide, prenomValide, emailNormalise, type);
    }

    public List<Adherent> listerAdherents() throws SQLException {
        return stockage.listerAdherents();
    }

    public Emprunt emprunterLivre(String isbn, int idAdherent) throws SQLException, BibliothequeException {
        verifierIdAdherent(idAdherent);
        return stockage.emprunter(Livre.normaliserIsbn(isbn), idAdherent, LocalDate.now(horloge));
    }

    public Emprunt retournerLivre(String isbn) throws SQLException, BibliothequeException {
        return stockage.retourner(Livre.normaliserIsbn(isbn), LocalDate.now(horloge));
    }

    public List<Emprunt> historique(int idAdherent) throws SQLException, BibliothequeException {
        verifierIdAdherent(idAdherent);
        return stockage.historique(idAdherent);
    }

    public List<Emprunt> listerRetards() throws SQLException {
        return stockage.listerRetards(LocalDate.now(horloge));
    }

    private static void verifierIdAdherent(int idAdherent) {
        if (idAdherent <= 0) {
            throw new IllegalArgumentException("L'identifiant de l'adhérent doit être positif.");
        }
    }

    private static String texteObligatoire(String valeur, String libelle) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(libelle + " est obligatoire.");
        }
        return valeur.strip();
    }

    private static String normaliserRecherche(String texte) {
        String decompose = Normalizer.normalize(texte.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return ACCENTS.matcher(decompose).replaceAll("");
    }
}
