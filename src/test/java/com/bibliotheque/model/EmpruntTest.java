
package com.bibliotheque.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmpruntTest {

    @Test
    void creerUnEmpruntAssocieAdherentEtLivre() {
        Adherent adherent = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        Emprunt emprunt = new Emprunt(adherent, livre);

        assertEquals(adherent, emprunt.getAdherent());
        assertEquals(livre, emprunt.getLivre());
        assertNotNull(emprunt.getDateEmprunt());
        assertNull(emprunt.getDateRetour());
        assertTrue(emprunt.estEnCours());
        assertFalse(livre.estDisponible());
    }

    @Test
    void retournerUnLivreMetFinALEmprunt() {
        Adherent adherent = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        Emprunt emprunt = new Emprunt(adherent, livre);

        assertTrue(emprunt.retournerLivre());
        assertFalse(emprunt.estEnCours());
        assertNotNull(emprunt.getDateRetour());
        assertTrue(livre.estDisponible());
    }

    @Test
    void refuserEmpruntDUnLivreDejaEmprunte() {
        Adherent adherent1 = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        Adherent adherent2 = new Adherent(
                2, "Martin", "Paul", "paul@example.com"
        );

        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        new Emprunt(adherent1, livre);

        assertThrows(
                IllegalStateException.class,
                () -> new Emprunt(adherent2, livre)
        );
    }

    @Test
    void refuserUnEmpruntAvecUnAdherentNull() {
        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Emprunt(null, livre)
        );

        assertTrue(livre.estDisponible());
    }

    @Test
    void refuserUnEmpruntAvecUnLivreNull() {
        Adherent adherent = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Emprunt(adherent, null)
        );
    }

    @Test
    void permettreUnNouvelEmpruntApresLeRetour() {
        Adherent adherent1 = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        Adherent adherent2 = new Adherent(
                2, "Martin", "Paul", "paul@example.com"
        );

        Livre livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );

        Emprunt premierEmprunt = new Emprunt(adherent1, livre);

        assertTrue(premierEmprunt.retournerLivre());
        assertTrue(livre.estDisponible());

        Emprunt deuxiemeEmprunt = new Emprunt(adherent2, livre);

        assertEquals(adherent2, deuxiemeEmprunt.getAdherent());
        assertTrue(deuxiemeEmprunt.estEnCours());
        assertFalse(livre.estDisponible());
    }

}
