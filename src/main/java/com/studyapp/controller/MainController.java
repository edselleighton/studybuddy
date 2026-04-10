package com.studyapp.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studyapp.dao.FlashcardDAO;
import com.studyapp.dao.impl.CardReviewDAOImpl;
import com.studyapp.dao.impl.DeckDAOImpl;
import com.studyapp.dao.impl.FlashcardDAOImpl;
import com.studyapp.dao.impl.StudySessionDAOImpl;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

//HANDLES ALL OPERATIONS THAT CONNECTS BACKEND WITH FRONTEND
//INCLUDES:
//CRUD OPERATIONS
//DATA/FILE HANDLING
//AUTHENTICATION
//DATA VALIDATION

public class MainController {
    private DeckController deckController;
    private FlashcardDAOImpl flashcardDAOImpl = new FlashcardDAOImpl();
    private StudySessionDAOImpl studySessionDAOImpl = new StudySessionDAOImpl();
    private CardReviewDAOImpl cardReviewDAOImpl = new CardReviewDAOImpl();

    private List<Flashcard> flashcards = new ArrayList<>();
    private List<StudySession> studySessions = new ArrayList<>();
    private List<CardReview> cardReviews = new ArrayList<>();

    private List<Flashcard> addedFlashcards = new ArrayList<>();
    private Map<Integer, Flashcard> modifiedFlashcards = new HashMap<>();
    private List<Integer> deletedFlashcards  = new ArrayList<>();
    private List<StudySession> addedStudySessions = new ArrayList<>();
    private Map<Integer, StudySession> modifiedStudySessions = new HashMap<>();
    private List<Integer> deletedStudySessions  = new ArrayList<>();
    private List<CardReview> addedCardReviews = new ArrayList<>();
    private List<Integer> deletedCardReviews = new ArrayList<>();


    private int lastCardID = 999;
    private int lastSessionID = 999;
    private int lastReviewID = 999;

    public MainController(){
        deckController = new DeckController(this);
    }

    // --------- AUTHENTICATION --------------
    public boolean tryAutoLogin() {
        if (!CredentialHandler.validateCredentials()) return false;
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
        CredentialHandler.save(username, password);
        DatabaseConnection.setCredentials(username, password);
        loadData();
    }

    //------------- DMLs --------------------
    //-----DECK--------
    public void createDeck(String deckName, String description) throws CustomException{
        deckController.createDeck(deckName, description);
    }

    public void deleteDeck(int deckID) throws CustomException{
        deckController.deleteDeck(deckID);
    }

    public Deck findDeck(int deckID) throws  CustomException{
        return deckController.findDeck(deckID);
    }

    public List<Deck> allDecks(){
        return deckController.allDecks();
    }

    public void updateDeck(Deck deck) throws CustomException {
        deckController.updateDeck(deck);
    }

    //-----FLASHCARDS------
    public List<Flashcard> allFlashcards(){
        return new ArrayList<>(flashcards);
    }

    public List<Flashcard> getFlashcardsByDeck(int deckID){
        return flashcards.stream()
                .filter(card -> card.getDeck().getDeckID() == deckID)
                .toList();
    }

    public void createFlashcard(int deckID, String question, String answer, String difficulty) throws CustomException{
        //CHECK IF DECK EXISTS
        Deck deck = allDecks().stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (deck == null) {
            throw new CustomException("Deck does not exist.");
        }

        Flashcard flashcard = new Flashcard(++lastCardID, deck, question, answer, difficulty, LocalDateTime.now());
        //TODO: validate card constraints
        flashcards.add(flashcard);
        addedFlashcards.add(flashcard);
    }

    public void updateFlashcard(Flashcard flashcard) throws CustomException {
        Flashcard existing = flashcards.stream()
                .filter(i -> i.getCardID() == flashcard.getCardID())
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("Flashcard not found.");
        }

        flashcards.remove(existing);
        flashcards.add(flashcard);

        if (addedFlashcards.contains(existing)) {
            addedFlashcards.remove(existing);
            addedFlashcards.add(flashcard);
        } else {
            modifiedFlashcards.put(flashcard.getCardID(), flashcard);
        }
    }

    public void deleteFlashcard(int flashcardID) throws CustomException {
        Flashcard existing = flashcards.stream()
                .filter(i -> i.getCardID() == flashcardID)
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("No record matched. No row was deleted.");
        }
        flashcards.remove(existing);

        if (addedFlashcards.contains(existing)) {
            addedFlashcards.remove(existing);
        } else {
            modifiedFlashcards.remove(flashcardID);
            deletedFlashcards.add(flashcardID);
        }
    }

    //---------- STUDY SESSIONS ----------------------//
    public StudySession createStudySession(int deckID, LocalDateTime startedAt) throws CustomException{
        Deck deck = allDecks().stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (deck == null) {
            throw new CustomException("Deck does not exist.");
        }

        StudySession studySession = new StudySession(++lastSessionID, deck, startedAt, null);
        studySessions.add(studySession);
        addedStudySessions.add(studySession);

        return studySession;
    }

    public void updateEndStudySession(StudySession studySession) throws CustomException{
        StudySession existing  = studySessions.stream()
                .filter(i -> i.getSessionID() == studySession.getSessionID())
                .findFirst().orElse(null);
        if (studySession == null) {
            throw new CustomException("Study session not found.");
        }

        studySessions.remove(existing);
        studySessions.add(studySession);

        if (addedStudySessions.contains(existing)){
            addedStudySessions.remove(existing);
            addedStudySessions.add(studySession);
        }else{
            modifiedStudySessions.put(studySession.getSessionID(), studySession);
        }
    }

    public List<StudySession> getAllSessions(){
        return new ArrayList<>(studySessions);
    }

    public void deleteSession(int sessionID) throws CustomException{
        StudySession existing = studySessions.stream()
                .filter(i -> i.getSessionID() == sessionID)
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("No record matched. No row was deleted.");
        }

        for(CardReview review: cardReviews){
            if(review.getStudySession().getSessionID() == sessionID){
                deleteCardReview(review.getReviewID());
            }
        }

        studySessions.remove(existing);

        if (addedStudySessions.contains(existing)) {
            addedStudySessions.remove(existing);
        } else {
            modifiedStudySessions.remove(sessionID);
            deletedStudySessions.add(sessionID);
        }
    }

    //---------- CARD REVIEWS ----------------------//
    public void createCardReview(int sessionID, int cardID, LocalDateTime reviewedAt, boolean isCorrect) throws CustomException{
        StudySession studySession = studySessions.stream()
                .filter(i -> i.getSessionID() == sessionID)
                .findFirst().orElse(null);
        if (studySession == null) {
            throw new CustomException("Study session not found.");
        }

        Flashcard flashcard = flashcards.stream()
                .filter(i -> i.getCardID() == cardID)
                .findFirst().orElse(null);
        if (flashcard == null) {
            throw new CustomException("Flashcard not found.");
        }

        CardReview cardReview = new CardReview(++lastReviewID, studySession, flashcard, reviewedAt, isCorrect);
        cardReviews.add(cardReview);
        addedCardReviews.add(cardReview);
    }

    public List<CardReview> getAllCardReviews(){
        return new ArrayList<>(cardReviews);
    }

    public List<CardReview> getCardReviewsBySession(int sessionID){
        return cardReviews.stream()
                .filter(i -> i.getStudySession().getSessionID() == sessionID)
                .toList();
    }

    public void deleteCardReview(int reviewID) throws CustomException {
        CardReview existing = cardReviews.stream()
                .filter(i -> i.getReviewID() == reviewID)
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("No record matched. No row was deleted.");
        }
        cardReviews.remove(existing);

        if (addedCardReviews.contains(existing)) {
            addedCardReviews.remove(existing);
        } else {
            deletedCardReviews.add(reviewID);
        }
    }

    //----------------- DATA -----------------------
    public void loadData() throws CustomException{
        try{
            deckController.loadDecks();
            flashcards = flashcardDAOImpl.getAllFlashcards();
            lastCardID = flashcardDAOImpl.getLastID();
            studySessions = studySessionDAOImpl.getAllSessions();
            cardReviews = cardReviewDAOImpl.getAllReviews();
            lastReviewID = cardReviewDAOImpl.getLastID();
            lastSessionID = studySessionDAOImpl.getLastID();
        }catch(Exception e){
            throw new CustomException("Failed to Load Data");
        }
    }

    public void saveChanges() throws CustomException{
        // Persist added decks
        // Persist modified decks
        // Delete decks
        // Similar for flashcards, study sessions, card reviews
    }

}
