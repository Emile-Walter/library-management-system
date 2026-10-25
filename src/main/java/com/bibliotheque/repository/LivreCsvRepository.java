package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LivreCsvRepository implements LivreRepository {

    @Override
    public List<Livre> chargerLivres() throws IOException {
        InputStream fichier = getClass().getResourceAsStream("/livres.csv");

        if (fichier == null) {
            throw new IOException("Le fichier livres.csv est introuvable.");
        }

        List<Livre> livres = new ArrayList<>();

        try (BufferedReader lecteur = new BufferedReader(
                new InputStreamReader(fichier, StandardCharsets.UTF_8))) {

            String ligne = lecteur.readLine();
            int numeroLigne = 1;

            while ((ligne = lecteur.readLine()) != null) {
                numeroLigne++;
                String[] colonnes = ligne.split(",", -1);

                if (colonnes.length != 3) {
                    throw new IOException(
                            "Format CSV incorrect à la ligne " + numeroLigne
                    );
                }

                String isbn = colonnes[0].trim();
                String titre = colonnes[1].trim();
                String auteur = colonnes[2].trim();

                livres.add(new Livre(isbn, titre, auteur));
            }
        }

        return livres;
    }
}