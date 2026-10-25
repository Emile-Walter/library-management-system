package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}