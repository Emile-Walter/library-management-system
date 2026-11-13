package com.bibliotheque.model;

public final class Etudiant extends Adherent {
    public Etudiant(int id, String nom, String prenom, String email) {
        super(id, nom, prenom, email, TypeAdherent.ETUDIANT);
    }

    @Override
    public int dureeEmpruntJours() {
        return 14;
    }

    @Override
    public int nombreMaxEmprunts() {
        return 3;
    }
}
