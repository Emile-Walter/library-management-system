package com.bibliotheque.model;

public abstract class Adherent extends Personne {
    private final int id;
    private final TypeAdherent type;

    protected Adherent(int id, String nom, String prenom, String email, TypeAdherent type) {
        super(nom, prenom, email);
        if (id <= 0) {
            throw new IllegalArgumentException("L'identifiant de l'adhérent doit être positif.");
        }
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public TypeAdherent getType() {
        return type;
    }

    @Override
    public String getRole() {
        return type.getLibelle();
    }

    public abstract int dureeEmpruntJours();

    public abstract int nombreMaxEmprunts();

    public boolean peutEncoreEmprunter(int empruntsEnCours) {
        if (empruntsEnCours < 0) {
            throw new IllegalArgumentException("Le nombre d'emprunts en cours doit être positif ou nul.");
        }
        return empruntsEnCours < nombreMaxEmprunts();
    }
}
