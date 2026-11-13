package com.bibliotheque.model;

public final class Enseignant extends Adherent {
    public Enseignant(int id, String nom, String prenom, String email) {
        super(id, nom, prenom, email, TypeAdherent.ENSEIGNANT);
    }

    @Override
    public int dureeEmpruntJours() {
        return 28;
    }

    @Override
    public int nombreMaxEmprunts() {
        return 5;
    }
}
