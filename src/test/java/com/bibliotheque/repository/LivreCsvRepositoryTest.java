package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LivreCsvRepositoryTest {
    @Test
    void leCatalogueFourniEstChargeEnUtf8() throws IOException {
        List<Livre> livres = new LivreCsvRepository().chargerLivres();

        assertEquals(6, livres.size());
        Livre premier = livres.get(0);
        assertEquals("FIC-001", premier.getIsbn());
        assertEquals("Les Archives du crépuscule", premier.getTitre());
        assertEquals("Élise Valorne", premier.getAuteur());
    }
}
