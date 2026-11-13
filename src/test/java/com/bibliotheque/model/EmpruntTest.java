package com.bibliotheque.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmpruntTest {
    private final Adherent etudiant = new Etudiant(1, "Rivel", "Mina", "mina@example.org");
    private final Livre livre = new Livre("FIC-001", "Les Archives du crépuscule", "Élise Valorne");
    private final LocalDate date = LocalDate.of(2026, 10, 26);

    @Test
    void leProfilDetermineLaDureeParPolymorphisme() {
        Adherent enseignant = new Enseignant(2, "Solène", "Nadir", "nadir@example.org");

        assertEquals(date.plusDays(14), Emprunt.ouvrir(etudiant, livre, date).getDateEcheance());
        assertEquals(date.plusDays(28), Emprunt.ouvrir(enseignant, livre, date).getDateEcheance());
    }

    @Test
    void unRetourLeJourDeLEmpruntEstPossible() {
        Emprunt ouvert = Emprunt.ouvrir(etudiant, livre, date);
        Emprunt ferme = ouvert.cloturer(date);

        assertTrue(ouvert.estEnCours());
        assertFalse(ouvert.getLivre().estDisponible());
        assertFalse(ferme.estEnCours());
        assertEquals(date, ferme.getDateRetour());
        assertTrue(ferme.getLivre().estDisponible());
    }

    @Test
    void unRetourNePeutPasPrecederLEmprunt() {
        Emprunt emprunt = Emprunt.ouvrir(etudiant, livre, date);

        assertThrows(IllegalArgumentException.class, () -> emprunt.cloturer(date.minusDays(1)));
    }

    @Test
    void unEmpruntClotureNePeutPasEtreRenduUneSecondeFois() {
        Emprunt emprunt = Emprunt.ouvrir(etudiant, livre, date).cloturer(date);

        assertThrows(IllegalStateException.class, () -> emprunt.cloturer(date.plusDays(1)));
    }

    @Test
    void unLivreIndisponibleNePeutPasOuvrirUnAutreEmprunt() {
        Livre indisponible = Emprunt.ouvrir(etudiant, livre, date).getLivre();

        assertThrows(IllegalStateException.class, () -> Emprunt.ouvrir(etudiant, indisponible, date));
    }
}
