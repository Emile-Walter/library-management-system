package com.bibliotheque.model;

public abstract class Personne {
    private final String nom;
    private final String prenom;
    private final String email;

    protected Personne(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getRole();
}
