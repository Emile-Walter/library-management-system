package com.bibliotheque.service;

import com.bibliotheque.exception.BibliothequeException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.repository.BibliothequeSQLiteRepository;
import com.bibliotheque.repository.LivreCsvRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BibliothequeServiceTest {
    private static final LocalDate DEPART = LocalDate.of(2026, 10, 26);

    @TempDir
    Path dossier;

    private Path fichier;
    private BibliothequeService service;
    private int etudiant;

    @BeforeEach
    void initialiser() throws Exception {
        fichier = dossier.resolve("bibliotheque.db");
        service = ouvrirService(DEPART);
        etudiant = service.inscrire("Rivel", "Mina", "mina@example.org", TypeAdherent.ETUDIANT).getId();
    }

    @Test
    void rechercherParTitreAuteurOuIdentifiantSansTenirCompteDesAccents() throws Exception {
        assertEquals(List.of("FIC-001"), identifiants(service.rechercherLivres(" CREPUSCULE ")));
        assertEquals(List.of("FIC-001"), identifiants(service.rechercherLivres("ELISE")));
        assertEquals(List.of("FIC-002"), identifiants(service.rechercherLivres("fic-002")));
        assertTrue(service.rechercherLivres("%").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.rechercherLivres(" "));
    }

    @Test
    void leCatalogueLesAdherentsEtLesEmpruntsPersistentApresRedemarrage() throws Exception {
        Emprunt pret = service.emprunterLivre(" fic-001 ", etudiant);
        assertEquals(DEPART.plusDays(14), pret.getDateEcheance());

        BibliothequeService lendemain = ouvrirService(DEPART.plusDays(1));
        assertEquals(6, lendemain.listerLivres().size());
        assertEquals(etudiant, lendemain.listerAdherents().get(0).getId());
        assertFalse(lendemain.rechercherLivres("FIC-001").get(0).estDisponible());
        assertTrue(lendemain.historique(etudiant).get(0).estEnCours());
        lendemain.retournerLivre("FIC-001");

        BibliothequeService reouvert = ouvrirService(DEPART.plusDays(2));
        assertTrue(reouvert.rechercherLivres("FIC-001").get(0).estDisponible());
        assertEquals(DEPART.plusDays(1), reouvert.historique(etudiant).get(0).getDateRetour());
    }

    @Test
    void unLivreNePeutPasEtrePreteDeuxFoisAvantSonRetour() throws Exception {
        int autre = service.inscrire("Delvaux", "Rayan", "rayan@example.org", TypeAdherent.ENSEIGNANT).getId();
        service.emprunterLivre("FIC-001", etudiant);

        assertThrows(LivreIndisponibleException.class, () -> service.emprunterLivre("FIC-001", autre));
        assertTrue(service.historique(autre).isEmpty());

        service.retournerLivre("FIC-001");
        Emprunt pret = service.emprunterLivre("FIC-001", autre);
        assertEquals(DEPART.plusDays(28), pret.getDateEcheance());
        assertFalse(service.historique(etudiant).get(0).estEnCours());
    }

    @Test
    void laLimiteEtudianteBloqueLeQuatriemePretEtUnRetourLibereUnePlace() throws Exception {
        service.emprunterLivre("FIC-001", etudiant);
        service.emprunterLivre("FIC-002", etudiant);
        service.emprunterLivre("FIC-003", etudiant);

        assertThrows(BibliothequeException.class, () -> service.emprunterLivre("FIC-004", etudiant));
        assertTrue(service.rechercherLivres("FIC-004").get(0).estDisponible());

        service.retournerLivre("FIC-003");
        assertTrue(service.emprunterLivre("FIC-004", etudiant).estEnCours());
    }

    @Test
    void unEnseignantPeutEmprunterCinqLivresMaisPasSix() throws Exception {
        int enseignant = service.inscrire("Delvaux", "Rayan", "rayan@example.org", TypeAdherent.ENSEIGNANT).getId();
        for (String identifiant : List.of("FIC-001", "FIC-002", "FIC-003", "FIC-004", "FIC-005")) {
            service.emprunterLivre(identifiant, enseignant);
        }

        assertThrows(BibliothequeException.class, () -> service.emprunterLivre("FIC-006", enseignant));
        assertEquals(5, service.historique(enseignant).size());
    }

    @Test
    void leRetardCommenceApresLEcheanceEtDisparaitApresRetour() throws Exception {
        service.emprunterLivre("FIC-001", etudiant);
        assertTrue(ouvrirService(DEPART.plusDays(14)).listerRetards().isEmpty());

        BibliothequeService enRetard = ouvrirService(DEPART.plusDays(15));
        assertEquals("FIC-001", enRetard.listerRetards().get(0).getLivre().getIsbn());
        enRetard.retournerLivre("FIC-001");

        assertTrue(enRetard.listerRetards().isEmpty());
    }

    @Test
    void refuserUnLivreOuUnAdherentInconnuEtUnRetourSansPret() throws Exception {
        assertThrows(LivreIntrouvableException.class, () -> service.emprunterLivre("FIC-999", etudiant));
        assertThrows(BibliothequeException.class, () -> service.emprunterLivre("FIC-001", 999));
        assertThrows(BibliothequeException.class, () -> service.historique(999));
        assertThrows(BibliothequeException.class, () -> service.retournerLivre("FIC-001"));
        assertThrows(IllegalArgumentException.class, () -> service.emprunterLivre("FIC-001", 0));
        assertTrue(service.historique(etudiant).isEmpty());
    }

    @Test
    void refuserUneInscriptionInvalideEtUnCourrielDejaUtilise() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> service.inscrire("Rivel", "Mina", "invalide", TypeAdherent.ETUDIANT));
        assertThrows(IllegalArgumentException.class,
                () -> service.inscrire(" ", "Mina", "autre@example.org", TypeAdherent.ETUDIANT));
        assertThrows(BibliothequeException.class,
                () -> service.inscrire("Solène", "Nadir", " MINA@EXAMPLE.ORG ", TypeAdherent.ENSEIGNANT));
        assertEquals(1, service.listerAdherents().size());
    }

    @Test
    void uneDateDeRetourInvalideLaisseLePretActif() throws Exception {
        service.emprunterLivre("FIC-001", etudiant);
        BibliothequeService tropTot = ouvrirService(DEPART.minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> tropTot.retournerLivre("FIC-001"));
        assertTrue(service.historique(etudiant).get(0).estEnCours());
        assertFalse(service.rechercherLivres("FIC-001").get(0).estDisponible());
        assertFalse(service.retournerLivre("FIC-001").estEnCours());
    }

    @Test
    void leDemarrageRefuseUnAncienFichierSansLeModifier() throws Exception {
        Path ancienFichier = dossier.resolve("ancienne-bibliotheque.db");
        String url = "jdbc:sqlite:" + ancienFichier;
        try (var connexion = DriverManager.getConnection(url); var sql = connexion.createStatement()) {
            sql.executeUpdate("CREATE TABLE livres (isbn TEXT PRIMARY KEY, disponible INTEGER)");
            sql.executeUpdate("INSERT INTO livres VALUES ('ANCIEN-001', 0)");
        }

        var ancienStockage = new BibliothequeSQLiteRepository(ancienFichier);
        var livres = new LivreCsvRepository().chargerLivres();
        var erreur = assertThrows(SQLException.class, () -> ancienStockage.initialiser(livres));
        assertTrue(erreur.getMessage().contains("ancienne version"));

        try (var connexion = DriverManager.getConnection(url); var sql = connexion.createStatement();
             var resultat = sql.executeQuery("SELECT isbn, disponible FROM livres")) {
            assertTrue(resultat.next());
            assertEquals("ANCIEN-001", resultat.getString("isbn"));
            assertEquals(0, resultat.getInt("disponible"));
            assertFalse(resultat.next());
        }
    }

    private BibliothequeService ouvrirService(LocalDate date) throws Exception {
        BibliothequeSQLiteRepository stockage = new BibliothequeSQLiteRepository(fichier);
        stockage.initialiser(new LivreCsvRepository().chargerLivres());
        Clock horloge = Clock.fixed(date.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        return new BibliothequeService(stockage, horloge);
    }

    private List<String> identifiants(List<Livre> livres) {
        return livres.stream().map(Livre::getIsbn).toList();
    }
}
