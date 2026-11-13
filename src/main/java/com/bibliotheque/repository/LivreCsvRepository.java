package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LivreCsvRepository implements LivreRepository {
    private static final String RESSOURCE = "/livres.csv";

    @Override
    public List<Livre> chargerLivres() throws IOException {
        InputStream fichier = getClass().getResourceAsStream(RESSOURCE);
        if (fichier == null) {
            throw new IOException("Le fichier livres.csv est introuvable.");
        }

        List<Livre> livres = new ArrayList<>();
        Set<String> identifiants = new HashSet<>();
        try (BufferedReader lecteur = new BufferedReader(new InputStreamReader(fichier, StandardCharsets.UTF_8))) {
            String entete = lecteur.readLine();
            if (entete != null && entete.startsWith("\uFEFF")) {
                entete = entete.substring(1);
            }
            if (!"isbn,titre,auteur".equals(entete)) {
                throw new IOException("L'en-tête de livres.csv doit être isbn,titre,auteur.");
            }

            int numeroLigne = 1;
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                numeroLigne++;
                if (ligne.isBlank()) {
                    continue;
                }

                Livre livre = lireLivre(ligne, numeroLigne);
                if (!identifiants.add(livre.getIsbn())) {
                    throw new IOException("Identifiant en double dans livres.csv, ligne " + numeroLigne + ".");
                }
                livres.add(livre);
            }
        }
        if (livres.isEmpty()) {
            throw new IOException("Le fichier livres.csv doit contenir au moins un livre.");
        }
        return List.copyOf(livres);
    }

    private Livre lireLivre(String ligne, int numeroLigne) throws IOException {
        String[] colonnes = ligne.split(",", -1);
        if (colonnes.length != 3) {
            throw new IOException("Trois colonnes sont attendues dans livres.csv, ligne " + numeroLigne + ".");
        }
        try {
            return new Livre(colonnes[0], colonnes[1], colonnes[2]);
        } catch (IllegalArgumentException erreur) {
            throw new IOException("livres.csv, ligne " + numeroLigne + " : " + erreur.getMessage(), erreur);
        }
    }
}
