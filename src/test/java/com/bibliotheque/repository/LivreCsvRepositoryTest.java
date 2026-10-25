
package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivreCsvRepositoryTest {

    @Test
    void chargerLivresLitLesLivresDuFichierCsv() throws IOException {
        LivreCsvRepository repository = new LivreCsvRepository();

        List<Livre> livres = repository.chargerLivres();

        assertEquals(3, livres.size());
        assertEquals("Le Petit Prince", livres.get(0).getTitre());
        assertTrue(livres.get(0).estDisponible());
    }

    @Test
    void rechercherLivreParTitre() throws IOException {
        LivreCsvRepository repository = new LivreCsvRepository();

        List<Livre> resultats =
                repository.rechercherLivres("Petit Prince");

        assertEquals(1, resultats.size());
        assertEquals("Le Petit Prince", resultats.get(0).getTitre());
    }

    @Test
    void rechercherLivreParAuteur() throws IOException {
        LivreCsvRepository repository = new LivreCsvRepository();

        List<Livre> resultats =
                repository.rechercherLivres("Antoine");

        assertFalse(resultats.isEmpty());
    }

    @Test
    void rechercheSansResultatRetourneListeVide() throws IOException {
        LivreCsvRepository repository = new LivreCsvRepository();

        List<Livre> resultats =
                repository.rechercherLivres("LivreInexistantXYZ");

        assertTrue(resultats.isEmpty());
    }
}
