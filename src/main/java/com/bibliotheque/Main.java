package com.bibliotheque;

import com.bibliotheque.repository.BibliothequeSQLiteRepository;
import com.bibliotheque.repository.LivreCsvRepository;
import com.bibliotheque.repository.LivreRepository;
import com.bibliotheque.repository.StockageBibliotheque;
import com.bibliotheque.service.BibliothequeService;
import com.bibliotheque.ui.ConsoleBibliotheque;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Path fichier = Path.of(args.length == 0 ? "bibliotheque-beta.db" : args[0]);
        LivreRepository catalogue = new LivreCsvRepository();
        StockageBibliotheque stockage = new BibliothequeSQLiteRepository(fichier);

        try {
            stockage.initialiser(catalogue.chargerLivres());
        } catch (IOException | SQLException erreur) {
            throw new IllegalStateException("Démarrage impossible : " + erreur.getMessage(), erreur);
        }

        BibliothequeService service = new BibliothequeService(stockage, Clock.systemDefaultZone());
        try (Scanner entree = new Scanner(System.in)) {
            new ConsoleBibliotheque(service, entree, System.out).lancer();
        }
    }
}
