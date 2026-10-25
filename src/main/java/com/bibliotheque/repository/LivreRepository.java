
package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;

import java.io.IOException;
import java.util.List;

public interface LivreRepository {

    // Charger tous les livres
    List<Livre> chargerLivres() throws IOException;

    // Rechercher des livres par titre ou par auteur
    List<Livre> rechercherLivres(String recherche) throws IOException;
}
