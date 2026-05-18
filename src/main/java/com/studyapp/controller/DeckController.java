package com.studyapp.controller;

import com.studyapp.dao.impl.DeckDAOImpl;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckController {
    MainController mc;

    private DeckDAOImpl deckDaoImpl = new DeckDAOImpl();
    private List<Deck> decks = new ArrayList<>();

    private List<Deck> addedDecks    = new ArrayList<>();
    private Map<Integer, Deck> modifiedDecks = new HashMap<>();
    private List<Integer> deletedDecks  = new ArrayList<>();

    private int lastDeckID = 999;

    public DeckController(MainController mc){
        this.mc = mc;
    }

    public Deck createDeck(String deckName, String description) throws CustomException{
        Deck deck = new Deck(lastDeckID, deckName, description, LocalDateTime.now());

        validateConstraints(deck);
        decks.add(deck);
        addedDecks.add(deck);
        lastDeckID++;
        return deck;
    }

    public void deleteDeck(int deckID) throws CustomException{
        Deck existing = decks.stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("No record matched. No row was deleted.");
        }
        decks.remove(existing);

        if (addedDecks.contains(existing)) {
            addedDecks.remove(existing);
        } else {
            modifiedDecks.remove(deckID);
            deletedDecks.add(deckID);
        }

        //DELETE ALL CARDS IN THIS DECK
        for(Flashcard flashcard: mc.getFlashcardsByDeck(deckID)){
            mc.deleteFlashcard(flashcard.getCardID());
        }
        //DELETE ALL SESSIONS ASSOCIATED IN THIS DECK
        for(StudySession session: mc.getAllSessions()){
            if(session.getDeckID() == deckID){
                mc.deleteSession(session.getSessionID());
            }
        }
    }

    public Deck findDeck(int deckID) throws CustomException{
        Deck deck = decks.stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (deck == null) {
            throw new CustomException("Deck not found.");
        }
        return deck;
    }

    public List<Deck> allDecks(){
        return new ArrayList<>(decks);
    }

    public void updateDeck(Deck deck) throws CustomException {
        Deck existing = decks.stream()
                .filter(i -> i.getDeckID() == deck.getDeckID())
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("Deck not found.");
        }

        validateConstraints(deck);

        decks.remove(existing);
        decks.add(deck);

        if (addedDecks.contains(existing)) {
            addedDecks.remove(existing);
            addedDecks.add(deck);
        } else {
            modifiedDecks.put(deck.getDeckID(), deck);
        }
    }

    public void loadDecks() throws CustomException{
        try{
            decks = deckDaoImpl.getAllDecks();
            lastDeckID = deckDaoImpl.getLastID() + 1;
        }catch(Exception e){
            throw new CustomException("Failed to Load Decks");
        }
    }

    public void saveDeckToDB() throws CustomException{
        try{
            persistPendingChanges(null);
            markPendingChangesSaved();
        }catch(Exception e){
            throw new CustomException("Failed to Save Decks");
        }
    }

    public void saveAddedDecks(List<Deck> decksToSave) throws CustomException {
        try {
            for (Deck deck : decksToSave) {
                if (addedDecks.contains(deck)) {
                    deckDaoImpl.insert(deck);
                }
            }
            addedDecks.removeAll(decksToSave);
        } catch (Exception e) {
            throw new CustomException("Failed to Save Decks");
        }
    }

    public boolean hasPendingChanges() {
        return !addedDecks.isEmpty() || !modifiedDecks.isEmpty() || !deletedDecks.isEmpty();
    }

    public void persistPendingChanges(Connection conn) throws SQLException {
        if (conn == null) {
            for (int deckID : deletedDecks) {
                deckDaoImpl.delete(deckID);
            }
            for (Deck deck : addedDecks) {
                deckDaoImpl.insert(deck);
            }
            for (Deck deck : modifiedDecks.values()) {
                deckDaoImpl.update(deck);
            }
            return;
        }

        for (int deckID : deletedDecks) {
            deckDaoImpl.delete(conn, deckID);
        }
        for (Deck deck : addedDecks) {
            deckDaoImpl.insert(conn, deck);
        }
        for (Deck deck : modifiedDecks.values()) {
            deckDaoImpl.update(conn, deck);
        }
    }

    public void markPendingChangesSaved() {
        addedDecks.clear();
        modifiedDecks.clear();
        deletedDecks.clear();
    }

    void validateConstraints(Deck deck) throws CustomException{
        //VALIDATE ID UNIQUENESS
        if(decks.stream().anyMatch(i -> (i.getDeckID() == deck.getDeckID()) && i != deck)){
            throw new CustomException("Deck ID already exists.");
        }
        //VALIDATE NAME UNIQUENESS
        if(decks.stream().anyMatch(i -> (i.getName().equals(deck.getName()) && i != deck))){
            throw new CustomException("Deck name already exists.");
        }

        //VALIDATE NAME NOT NULL
        if(deck.getName() == null || deck.getName().trim().isEmpty()){
            throw new CustomException("Name cannot be empty.");
        }
        //VALIDATE NAME < 100
        if(deck.getName().length() > 100){
            throw new CustomException("Name cannot exceed 100 characters.");
        }
        //VALIDATE DATE IF NOT NULL
        if(deck.getCreatedAt() == null){
            throw new CustomException("Date cannot be null.");
        }
    }
}
