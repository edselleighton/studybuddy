package com.studyapp.dao;

import java.sql.SQLException;
import java.util.List;

import com.studyapp.model.Deck;

public interface DeckDAO {
    public void insert(Deck deck) throws SQLException;
    public void update(Deck deck) throws SQLException;
    public void delete(int deckID) throws SQLException;
    public Deck findByID(int deckID);
    public List<Deck> getAllDecks();
}
