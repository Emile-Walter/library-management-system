
package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdherentRepositoryImpl implements AdherentRepository {

    private final List<Adherent> adherents = new ArrayList<>();

    @Override
    public void ajouter(Adherent adherent) {
        if (adherent == null) {
            throw new IllegalArgumentException(
                    "L'adhérent ne peut pas être null."
            );
        }

        if (rechercherParId(adherent.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Un adhérent avec cet identifiant existe déjà."
            );
        }

        adherents.add(adherent);
    }

    @Override
    public List<Adherent> listerTous() {
        return new ArrayList<>(adherents);
    }

    @Override
    public Optional<Adherent> rechercherParId(int id) {
        return adherents.stream()
                .filter(adherent -> adherent.getId() == id)
                .findFirst();
    }

    @Override
    public List<Adherent> rechercherParNom(String nom) {
        List<Adherent> resultats = new ArrayList<>();

        if (nom == null || nom.isBlank()) {
            return resultats;
        }

        String recherche = nom.trim().toLowerCase();

        for (Adherent adherent : adherents) {
            if (adherent.getNom() != null
                    && adherent.getNom().toLowerCase()
                    .contains(recherche)) {
                resultats.add(adherent);
            }
        }

        return resultats;
    }

    @Override
    public boolean supprimer(int id) {
        return adherents.removeIf(
                adherent -> adherent.getId() == id
        );
    }
}
