package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;

import java.io.IOException;
import java.util.List;

public interface LivreRepository {
    List<Livre> chargerLivres() throws IOException;
}
