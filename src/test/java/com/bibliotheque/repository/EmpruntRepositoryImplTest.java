
package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmpruntRepositoryImplTest {

    private EmpruntRepository repository;
    private Adherent adherent;
    private Livre livre;

    @BeforeEach
    void setUp() {
        repository = new EmpruntRepositoryImpl();

        adherent = new Adherent(
                1, "Dupont", "Jean", "jean@example.com"
        );

        livre = new Livre(
                "978-2070612758",
                "Le Petit Prince",
                "Antoine de Saint-Exupéry"
        );
    }

    @Test
    void ajouterEtListerUnEmprunt() {
        Emprunt emprunt = new Emprunt(adherent, livre);

        repository.ajouter(emprunt);

        assertEquals(1, repository.listerTous().size());
        assertEquals(emprunt, repository.listerTous().get(0));
    }

    @Test
    void refuserUnEmpruntNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.ajouter(null)
        );
    }

    @Test
    void rechercherEmpruntEnCoursParIsbn() {
        Emprunt emprunt = new Emprunt(adherent, livre);
        repository.ajouter(emprunt);

        Optional<Emprunt> resultat =
                repository.rechercherEmpruntEnCoursParIsbn(
                        "978-2070612758"
                );

        assertTrue(resultat.isPresent());
        assertEquals(emprunt, resultat.get());
    }

    @Test
    void rechercherUnIsbnInexistant() {
        assertTrue(
                repository.rechercherEmpruntEnCoursParIsbn(
                        "ISBN-INCONNU"
                ).isEmpty()
        );
    }

    @Test
    void listerEmpruntsEnCours() {
        Emprunt emprunt = new Emprunt(adherent, livre);
        repository.ajouter(emprunt);

        assertEquals(1, repository.listerEmpruntsEnCours().size());

        emprunt.retournerLivre();

        assertTrue(repository.listerEmpruntsEnCours().isEmpty());
    }

    @Test
    void rechercherEmpruntsParIdAdherent() {
        Emprunt emprunt = new Emprunt(adherent, livre);
        repository.ajouter(emprunt);

        List<Emprunt> resultats =
                repository.rechercherParIdAdherent(1);

        assertEquals(1, resultats.size());
        assertEquals(adherent, resultats.get(0).getAdherent());
    }

    @Test
    void conserverHistoriqueApresRetourEtNouvelEmprunt() {
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
        repository.ajouter(premierEmprunt);

        assertTrue(premierEmprunt.retournerLivre());

        Emprunt deuxiemeEmprunt = new Emprunt(adherent2, livre);
        repository.ajouter(deuxiemeEmprunt);

        List<Emprunt> historique = repository.listerTous();

        assertEquals(2, historique.size());
        assertFalse(historique.get(0).estEnCours());
        assertTrue(historique.get(1).estEnCours());
        assertEquals(adherent1, historique.get(0).getAdherent());
        assertEquals(adherent2, historique.get(1).getAdherent());
    }

}
