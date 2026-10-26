
package com.bibliotheque.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivreTest {

    @Test
    void unNouveauLivreEstDisponible() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        assertTrue(livre.estDisponible());
    }

    @Test
    void emprunterUnLivreDisponible() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        assertTrue(livre.emprunter());
        assertFalse(livre.estDisponible());
    }

    @Test
    void refuserUnDeuxiemeEmprunt() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        assertTrue(livre.emprunter());

        assertFalse(livre.emprunter());
        assertFalse(livre.estDisponible());
    }

    @Test
    void retournerUnLivreEmprunte() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        livre.emprunter();

        assertTrue(livre.retourner());
        assertTrue(livre.estDisponible());
    }

    @Test
    void refuserLeRetourDUnLivreDejaDisponible() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        assertFalse(livre.retourner());
        assertTrue(livre.estDisponible());
    }
}
