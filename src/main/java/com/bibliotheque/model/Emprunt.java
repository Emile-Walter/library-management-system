
package com.bibliotheque.model;

import java.time.LocalDate;

public class Emprunt {

    private final Adherent adherent;
    private final Livre livre;
    private final LocalDate dateEmprunt;
    private LocalDate dateRetour;

    public Emprunt(Adherent adherent, Livre livre) {
        if (adherent == null) {
            throw new IllegalArgumentException(
                    "L'adhérent ne peut pas être null."
            );
        }

        if (livre == null) {
            throw new IllegalArgumentException(
                    "Le livre ne peut pas être null."
            );
        }

        if (!livre.emprunter()) {
            throw new IllegalStateException(
                    "Ce livre n'est pas disponible."
            );
        }

        this.adherent = adherent;
        this.livre = livre;
        this.dateEmprunt = LocalDate.now();
        this.dateRetour = null;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public Livre getLivre() {
        return livre;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public boolean estEnCours() {
        return dateRetour == null;
    }

    public boolean retournerLivre() {
        if (!estEnCours()) {
            return false;
        }

        if (livre.retourner()) {
            dateRetour = LocalDate.now();
            return true;
        }

        return false;
    }
}
