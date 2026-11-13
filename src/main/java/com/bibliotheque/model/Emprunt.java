package com.bibliotheque.model;

import java.time.LocalDate;
import java.util.Objects;

public final class Emprunt {
    private final Adherent adherent;
    private final Livre livre;
    private final LocalDate dateEmprunt;
    private final LocalDate dateEcheance;
    private final LocalDate dateRetour;

    public Emprunt(Adherent adherent, Livre livre, LocalDate dateEmprunt,
                   LocalDate dateEcheance, LocalDate dateRetour) {
        this.adherent = Objects.requireNonNull(adherent, "Adhérent requis.");
        this.livre = Objects.requireNonNull(livre, "Livre requis.");
        this.dateEmprunt = Objects.requireNonNull(dateEmprunt, "Date d'emprunt requise.");
        this.dateEcheance = Objects.requireNonNull(dateEcheance, "Date d'échéance requise.");
        if (dateEcheance.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("L'échéance précède la date d'emprunt.");
        }
        if (dateRetour != null && dateRetour.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("La date de retour précède la date d'emprunt.");
        }
        this.dateRetour = dateRetour;
    }

    public static Emprunt ouvrir(Adherent adherent, Livre livre, LocalDate date) {
        if (!livre.estDisponible()) {
            throw new IllegalStateException("Ce livre est déjà emprunté.");
        }
        LocalDate echeance = date.plusDays(adherent.dureeEmpruntJours());
        return new Emprunt(adherent, avecDisponibilite(livre, false), date, echeance, null);
    }

    public Emprunt cloturer(LocalDate date) {
        Objects.requireNonNull(date, "Date de retour requise.");
        if (!estEnCours()) {
            throw new IllegalStateException("Cet emprunt est déjà clôturé.");
        }
        return new Emprunt(adherent, avecDisponibilite(livre, true), dateEmprunt, dateEcheance, date);
    }

    private static Livre avecDisponibilite(Livre livre, boolean disponible) {
        return new Livre(livre.getIsbn(), livre.getTitre(), livre.getAuteur(), disponible);
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

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public boolean estEnCours() {
        return dateRetour == null;
    }
}
