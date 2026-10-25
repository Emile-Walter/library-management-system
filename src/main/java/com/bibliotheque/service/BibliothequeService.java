package com.bibliotheque.service;

import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Livre;

import java.util.ArrayList;
import java.util.List;

public class BibliothequeService {
    private final List<Livre> livres = new ArrayList<>();

    public void ajouterLivre(Livre livre) {
        livres.add(livre);
    }

    public void emprunterLivre(String isbn)
            throws LivreIndisponibleException, LivreIntrouvableException {

        for (Livre livre : livres) {
            if (livre.getIsbn().equals(isbn)) {
                boolean empruntReussi = livre.emprunter();

                if (!empruntReussi) {
                    throw new LivreIndisponibleException(
                            "Le livre est déjà emprunté : " + livre.getTitre()
                    );
                }

                return;
            }
        }

        throw new LivreIntrouvableException(
                "Aucun livre trouvé avec l'ISBN : " + isbn
        );
    }

    public boolean retournerLivre(String isbn)
            throws LivreIntrouvableException {

        for (Livre livre : livres) {
            if (livre.getIsbn().equals(isbn)) {
                return livre.retourner();
            }
        }

        throw new LivreIntrouvableException(
                "Aucun livre trouvé avec l'ISBN : " + isbn
        );
    }

    public List<Livre> getLivres() {
        return List.copyOf(livres);
    }
}