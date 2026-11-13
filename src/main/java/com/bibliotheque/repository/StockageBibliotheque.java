package com.bibliotheque.repository;

import com.bibliotheque.exception.BibliothequeException;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.TypeAdherent;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface StockageBibliotheque {
    void initialiser(List<Livre> livres) throws SQLException;

    List<Livre> listerLivres() throws SQLException;

    Adherent inscrire(String nom, String prenom, String email, TypeAdherent type)
            throws SQLException, BibliothequeException;

    List<Adherent> listerAdherents() throws SQLException;

    Emprunt emprunter(String isbn, int idAdherent, LocalDate date) throws SQLException, BibliothequeException;

    Emprunt retourner(String isbn, LocalDate date) throws SQLException, BibliothequeException;

    List<Emprunt> historique(int idAdherent) throws SQLException, BibliothequeException;

    List<Emprunt> listerRetards(LocalDate date) throws SQLException;
}
