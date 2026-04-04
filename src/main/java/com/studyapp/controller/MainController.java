package com.studyapp.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.studyapp.dao.impl.CardReviewDAOImpl;
import com.studyapp.dao.impl.DeckDAOImpl;
import com.studyapp.dao.impl.FlashcardDAOImpl;
import com.studyapp.dao.impl.StudySessionDAOImpl;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

public class MainController {
    private DeckDAOImpl deckDaoImpl = new DeckDAOImpl();
    private CredentialHandler cHandler = new CredentialHandler();
    private FlashcardDAOImpl flashcardDAOImpl = new FlashcardDAOImpl();
    private StudySessionDAOImpl studySessionDAOImpl = new StudySessionDAOImpl();
    private CardReviewDAOImpl cardReviewDAOImpl = new CardReviewDAOImpl();

    private List<Deck> decks = new ArrayList<>();
    private List<Flashcard> flashcards = new ArrayList<>();
    private List<StudySession> studySessions = new ArrayList<>();
    private List<CardReview> cardReviews = new ArrayList<>();

    public boolean tryAutoLogin() {
        if (!cHandler.checkForCred()) return false;
        if (!cHandler.readAndValidate()) return false;
        try {
            loadData();
            return true;
        } catch (CustomException e) {
            return false;
        }
    }

    public void login(String username, String password) throws CustomException {
        if (!DatabaseConnection.authenticate(username, password)) {
            throw new CustomException("Invalid credentials.");
        }
        cHandler.write(username, password);
        DatabaseConnection.setCredentials(username, password);
        loadData();
    }

    public void createDeck(String deckName, String description) throws CustomException {
        try {
            Deck deck = new Deck(999, deckName, description, LocalDateTime.now());
            deckDaoImpl.insert(deck);
        } catch (SQLException e) {
            throw new CustomException("Error adding deck.");
        }
    }

    public void updateDeck(int deckID, String deckName, String description) throws CustomException {
        try {
            Deck deck = new Deck(deckID, deckName, description, LocalDateTime.now());
            deckDaoImpl.update(deck);
        } catch (SQLException e) {
            throw new CustomException("Error updating deck.");
        }
    }

    public void deleteDeck(int deckID) throws CustomException {
        try {
            deckDaoImpl.delete(deckID);
        } catch (SQLException e) {
            throw new CustomException("Error deleting deck.");
        }
    }

    public Deck findDeck(int deckID) {
        return deckDaoImpl.findByID(deckID);
    }

    public List<Deck> allDecks() {
        return new ArrayList<>(decks);
    }

    public List<Flashcard> allFlashcards() {
        return new ArrayList<>(flashcards);
    }

    public List<Flashcard> getFlashcardsByDeck(int deckID) {
        return flashcards.stream()
            .filter(card -> card.getDeck().getDeckID() == deckID)
            .toList();
    }

    public void loadData() throws CustomException {
        try {
            decks = deckDaoImpl.getAllDecks();
            flashcards = flashcardDAOImpl.getAllFlashcards();
            studySessions = studySessionDAOImpl.getAllSessions();
            cardReviews = cardReviewDAOImpl.getAllReviews();
        } catch (Exception e) {
            throw new CustomException("Failed to load data.");
        }
    }
}
