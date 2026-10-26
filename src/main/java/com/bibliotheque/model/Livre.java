package com.bibliotheque.model;

public class Livre {
    private final String isbn;
    private final String titre;
    private final String auteur;
    private boolean disponible;

    public Livre(String isbn, String titre, String auteur) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = true;
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

    public boolean emprunter() {
        if (!disponible) {
            return false;
        }

        disponible = false;
        return true;
    }

    public boolean retourner() {
        if (disponible) {
            return false;
        }

        disponible = true;
        return true;
    }
}
