
package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;

import java.util.List;
import java.util.Optional;

public interface AdherentRepository {

    void ajouter(Adherent adherent);

    List<Adherent> listerTous();

    Optional<Adherent> rechercherParId(int id);

    List<Adherent> rechercherParNom(String nom);

    boolean supprimer(int id);
}
