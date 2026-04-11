package com.studyapp.controller;

import java.time.LocalDateTime;
import java.util.List;

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
    private FlashcardController flashcardController;
    private StudyController studyController;
    private ReviewController reviewController;

    public MainController(){
        deckController = new DeckController(this);
        flashcardController = new FlashcardController(this);
        studyController = new StudyController(this);
        reviewController = new ReviewController(this);
    }

    // --------- AUTHENTICATION --------------
    public boolean tryAutoLogin() {
        if (!CredentialHandler.validateCredentials()) return false;
        try {
            DatabaseConnection.initializeDatabase();
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
        DatabaseConnection.initializeDatabase();
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
        return flashcardController.allFlashcards();
    }

    public List<Flashcard> getFlashcardsByDeck(int deckID){
        return flashcardController.getFlashcardsByDeck(deckID);
    }

    public void createFlashcard(int deckID, String question, String answer, String difficulty) throws CustomException{
        flashcardController.createFlashcard(deckID, question, answer, difficulty);
    }

    public void updateFlashcard(Flashcard flashcard) throws CustomException {
        flashcardController.updateFlashcard(flashcard);
    }

    public void deleteFlashcard(int flashcardID) throws CustomException {
        flashcardController.deleteFlashcard(flashcardID);
    }

    //---------- STUDY SESSIONS ----------------------//
    public StudySession createStudySession(int deckID, LocalDateTime startedAt) throws CustomException{
        return studyController.createStudySession(deckID, startedAt);
    }

    public void updateEndStudySession(StudySession studySession) throws CustomException{
        studyController.updateEndStudySession(studySession);
    }

    public List<StudySession> getAllSessions(){
        return studyController.getAllSessions();
    }

    public void deleteSession(int sessionID) throws CustomException{
        studyController.deleteSession(sessionID);
    }

    //---------- CARD REVIEWS ----------------------//
    public void createCardReview(int sessionID, int cardID, LocalDateTime reviewedAt, boolean isCorrect) throws CustomException{
        reviewController.createCardReview(sessionID, cardID,reviewedAt, isCorrect);
    }

    public List<CardReview> getAllCardReviews(){
        return reviewController.getAllCardReviews();
    }

    public List<CardReview> getCardReviewsBySession(int sessionID){
       return reviewController.getCardReviewsBySession(sessionID);
    }

    public void deleteCardReview(int reviewID) throws CustomException {
        reviewController.deleteCardReview(reviewID);
    }

    //----------------- DATA -----------------------
    public void loadData() throws CustomException{
        try{
            deckController.loadDecks();
            flashcardController.loadFLashcard();
            studyController.loadStudySessions();
            reviewController.loadCardReviews();
        }catch(Exception e){
            throw new CustomException("Failed to Load Data");
        }
    }

    public void saveChanges() throws CustomException{
        deckController.saveDeckToDB();
        flashcardController.saveFlashcardToDB();
        studyController.saveStudySessionToDB();
        reviewController.saveReviewToDB();
        System.out.println("Changes Saved to Database.");
    }

}
