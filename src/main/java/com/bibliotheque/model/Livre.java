package com.bibliotheque.model;

import java.util.Locale;

public final class Livre {
    private final String isbn;
    private final String titre;
    private final String auteur;
    private final boolean disponible;

    public Livre(String isbn, String titre, String auteur) {
        this(isbn, titre, auteur, true);
    }

    public Livre(String isbn, String titre, String auteur, boolean disponible) {
        this.isbn = normaliserIsbn(isbn);
        this.titre = texteObligatoire(titre, "Le titre");
        this.auteur = texteObligatoire(auteur, "L'auteur");
        this.disponible = disponible;
    }

    public static String normaliserIsbn(String isbn) {
        return texteObligatoire(isbn, "L'identifiant du livre").toUpperCase(Locale.ROOT);
    }

    private static String texteObligatoire(String valeur, String libelle) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(libelle + " est obligatoire.");
        }
        return valeur.strip();
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public boolean estDisponible() {
        return disponible;
    }
}
