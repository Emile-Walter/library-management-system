
package com.bibliotheque.repository;

import com.bibliotheque.model.Emprunt;

import java.util.List;
import java.util.Optional;

public interface EmpruntRepository {

    // Enregistrer un nouvel emprunt
    void ajouter(Emprunt emprunt);

    // Afficher tous les emprunts
    List<Emprunt> listerTous();

    // Rechercher un emprunt par ISBN du livre
    Optional<Emprunt> rechercherEmpruntEnCoursParIsbn(String isbn);

    // Afficher les emprunts encore en cours
    List<Emprunt> listerEmpruntsEnCours();

    // Afficher les emprunts d'un adhérent
    List<Emprunt> rechercherParIdAdherent(int idAdherent);
}
