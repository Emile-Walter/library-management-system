
package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AdherentRepositoryImplTest {

    private AdherentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AdherentRepositoryImpl();
    }

    @Test
    void ajouterPuisRechercherAdherent() {
        Adherent adherent = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        repository.ajouter(adherent);

        Optional<Adherent> resultat =
                repository.rechercherParId(1);

        assertTrue(resultat.isPresent());
        assertEquals("Dupont", resultat.get().getNom());
        assertEquals("Jean", resultat.get().getPrenom());
        assertEquals("jean@example.com", resultat.get().getEmail());
    }

    @Test
    void rechercherAdherentsParNom() {
        repository.ajouter(new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        ));

        repository.ajouter(new Adherent(
                2, "Martin", "Paul", "paul@example.com"
        ));

        List<Adherent> resultats =
                repository.rechercherParNom("dup");

        assertEquals(1, resultats.size());
        assertEquals("Dupont", resultats.get(0).getNom());
    }

    @Test
    void supprimerAdherent() {
        repository.ajouter(new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        ));

        assertTrue(repository.supprimer(1));
        assertFalse(repository.rechercherParId(1).isPresent());
        assertFalse(repository.supprimer(1));
    }

    @Test
    void listerTousLesAdherents() {
        repository.ajouter(new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        ));

        repository.ajouter(new Adherent(
                2, "Martin", "Paul", "paul@example.com"
        ));

        assertEquals(2, repository.listerTous().size());
    }

    @Test
    void rechercherAdherentInexistant() {
        assertTrue(repository.rechercherParId(999).isEmpty());
    }

    @Test
    void rechercherNomSansResultat() {
        repository.ajouter(new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        ));

        assertTrue(repository.rechercherParNom("Inconnu").isEmpty());
    }

    @Test
    void rechercherNomSansRespecterLaCasse() {
        repository.ajouter(new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        ));

        assertEquals(
                1,
                repository.rechercherParNom("DUPONT").size()
        );
    }

}
