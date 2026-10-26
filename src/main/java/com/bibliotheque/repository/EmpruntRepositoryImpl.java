
package com.bibliotheque.repository;

import com.bibliotheque.model.Emprunt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntRepositoryImpl implements EmpruntRepository {

    private final List<Emprunt> emprunts = new ArrayList<>();

    @Override
    public void ajouter(Emprunt emprunt) {
        if (emprunt == null) {
            throw new IllegalArgumentException(
                    "L'emprunt ne peut pas être null."
            );
        }

        emprunts.add(emprunt);
    }

    @Override
    public List<Emprunt> listerTous() {
        return new ArrayList<>(emprunts);
    }

    @Override
    public Optional<Emprunt> rechercherEmpruntEnCoursParIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return Optional.empty();
        }

        return emprunts.stream()
                .filter(Emprunt::estEnCours)
                .filter(emprunt ->
                        emprunt.getLivre().getIsbn().equalsIgnoreCase(isbn.trim())
                )
                .findFirst();
    }

    @Override
    public List<Emprunt> listerEmpruntsEnCours() {
        List<Emprunt> resultats = new ArrayList<>();

        for (Emprunt emprunt : emprunts) {
            if (emprunt.estEnCours()) {
                resultats.add(emprunt);
            }
        }

        return resultats;
    }

    @Override
    public List<Emprunt> rechercherParIdAdherent(int idAdherent) {
        List<Emprunt> resultats = new ArrayList<>();

        for (Emprunt emprunt : emprunts) {
            if (emprunt.getAdherent().getId() == idAdherent) {
                resultats.add(emprunt);
            }
        }

        return resultats;
    }
}
