package com.studyapp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.studyapp.model.Deck;

public interface DeckDAO {
    public void insert(Deck deck) throws SQLException;
    public void insert(Connection conn, Deck deck) throws SQLException;
    public void update(Deck deck) throws SQLException;
    public void update(Connection conn, Deck deck) throws SQLException;
    public void delete(int deckID) throws SQLException;
    public void delete(Connection conn, int deckID) throws SQLException;
    public Deck findByID(int deckID);
    public List<Deck> getAllDecks();
    public int getLastID();
}
