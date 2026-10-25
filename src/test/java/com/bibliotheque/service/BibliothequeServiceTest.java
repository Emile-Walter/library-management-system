package com.bibliotheque.service;

import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Livre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BibliothequeServiceTest {

    @Test
    void emprunterLivreRendLeLivreIndisponible() throws Exception {
        BibliothequeService service = new BibliothequeService();
        Livre livre = new Livre(
                "ISBN-001",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        service.ajouterLivre(livre);
        service.emprunterLivre("ISBN-001");

        assertFalse(livre.estDisponible());
    }

    @Test
    void livreEstDisponibleAvantEmprunt() {
        Livre livre = new Livre(
                "ISBN-002",
                "Les Misérables",
                "Victor Hugo"
        );

        assertTrue(livre.estDisponible());
    }

    @Test
    void emprunterDeuxFoisLeMemeLivreDeclencheUneException() throws Exception {
        BibliothequeService service = new BibliothequeService();
        Livre livre = new Livre("ISBN-003", "Candide", "Voltaire");

        service.ajouterLivre(livre);
        service.emprunterLivre("ISBN-003");

        assertThrows(
                LivreIndisponibleException.class,
                () -> service.emprunterLivre("ISBN-003")
        );
    }

    @Test
    void emprunterUnLivreAbsentDeclencheUneException() {
        BibliothequeService service = new BibliothequeService();

        assertThrows(
                LivreIntrouvableException.class,
                () -> service.emprunterLivre("ISBN-INEXISTANT")
        );
    }

    @Test
    void retournerLivreLeRendDisponible() throws Exception {
        BibliothequeService service = new BibliothequeService();
        Livre livre = new Livre("ISBN-004", "Candide", "Voltaire");

        service.ajouterLivre(livre);
        service.emprunterLivre("ISBN-004");

        boolean retourReussi = service.retournerLivre("ISBN-004");

        assertTrue(retourReussi);
        assertTrue(livre.estDisponible());
    }
}