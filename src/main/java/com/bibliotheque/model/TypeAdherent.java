package com.bibliotheque.model;

public enum TypeAdherent {
    ETUDIANT("Étudiant"),
    ENSEIGNANT("Enseignant");

    private final String libelle;

    TypeAdherent(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public Adherent creer(int id, String nom, String prenom, String email) {
        return switch (this) {
            case ETUDIANT -> new Etudiant(id, nom, prenom, email);
            case ENSEIGNANT -> new Enseignant(id, nom, prenom, email);
        };
    }
}
