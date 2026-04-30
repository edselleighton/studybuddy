package com.studyapp.controller;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;
import com.studyapp.service.JsonImportExportService;

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
    private AnswerChecker answerChecker;

    public MainController(){
        deckController = new DeckController(this);
        flashcardController = new FlashcardController(this);
        studyController = new StudyController(this);
        reviewController = new ReviewController(this);
        answerChecker = new AnswerChecker();
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
    public Deck createDeck(String deckName, String description) throws CustomException{
        return deckController.createDeck(deckName, description);
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

    public Flashcard getFlashcard(int flashcardID){
        return flashcardController.getFlashcard(flashcardID);
    }

    public List<Flashcard> getFlashcardsByDeck(int deckID){
        return flashcardController.getFlashcardsByDeck(deckID);
    }

    public List<Flashcard> getHardFlashcards(){
        return flashcardController.getHardFlashcards();
    }

    public List<Flashcard> getMediumFlashcards(){
        return flashcardController.getMediumFlashcards();
    }

    public List<Flashcard> getEasyFlashcards(){
        return flashcardController.getEasyFlashcards();
    }

    public Flashcard createFlashcard(int deckID, String question, String answer, String difficulty) throws CustomException{
        return flashcardController.createFlashcard(deckID, question, answer, difficulty);
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

    public String checkAnswer(String answer, String expected){
        return answerChecker.check(expected, answer);
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

    //------------ STATISTICS  -------------------//
    public int getDeckProgress(int deckID){
        // Delegate filtering logic to the ReviewController
        List<CardReview> deckReviews = studyController.getSpecificDeckSession(deckID).stream()
                .flatMap(session -> getCardReviewsBySession(session.getSessionID()).stream())
                .toList();

        Set<Integer> correctCardIds = deckReviews.stream()
                .filter(CardReview::isCorrect)
                .map(CardReview::getFlashcardID)
                .collect(Collectors.toSet());

        long uniqueCorrectlyReviewed = correctCardIds.size();

        int total = getFlashcardsByDeck(deckID).size();

        if (total == 0) {
            return 0;
        }

        return (int) (uniqueCorrectlyReviewed * 100 / total);
    }

    public int getAccuracy(){
        int allCorrectReviews = reviewController.getCorrectReviews().size();
        int allReviews = getAllCardReviews().size();

        if (allReviews == 0) {
            return 0;
        }

        return (allCorrectReviews*100)/allReviews;
    }

    public String getCardsReviewedProgress() {
        // Coverage is just the count of unique cards that have been reviewed
        int uniqueReviewedCount = reviewController.getLatestUniqueReviews(getAllCardReviews()).size();
        return uniqueReviewedCount + "/" + allFlashcards().size();
    }

    public List<Deck> getRecentDecks() {
        return studyController.getRecentSessions().stream()
                .map(session -> {
                    try {
                        return findDeck(session.getDeckID());
                    } catch (Exception e) {
                        System.err.println("Could not find deck: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct() // REMOVES DUPLICATE
                .limit(5)
                .toList();
    }

    public String getTotalStudyTime(){
        Duration studyTime = getAllSessions().stream()
                .filter(i -> i.getStartedAt() != null && i.getEndedAt() != null)
                .map(i -> Duration.between(i.getStartedAt(), i.getEndedAt()))
                .reduce(Duration.ZERO, Duration::plus);

        return studyTime.toHours() + "hr " + studyTime.toMinutesPart() + "m";
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

    public boolean hasUnsavedChanges() {
        return deckController.hasPendingChanges()
                || flashcardController.hasPendingChanges()
                || studyController.hasPendingChanges()
                || reviewController.hasPendingChanges();
    }

    public void saveImportedChanges(List<Deck> importedDecks, List<Flashcard> importedFlashcards) throws CustomException {
        deckController.saveAddedDecks(importedDecks);
        flashcardController.saveAddedFlashcards(importedFlashcards);
    }

    // --------- JSON IMPORT / EXPORT --------------
    /**
     * Imports decks and cards from a JSON file.
     * Supports both single-deck and multi-deck JSON formats.
     * @return number of decks imported
     */
    public int importFromJson(File file) throws CustomException {
        return new JsonImportExportService().importFromFile(file, this);
    }

    /**
     * Exports a deck and all its cards to a JSON file.
     */
    public void exportDeckToJson(int deckID, File file) throws CustomException {
        Deck deck = findDeck(deckID);
        List<Flashcard> cards = getFlashcardsByDeck(deckID);
        new JsonImportExportService().exportDeckToFile(deck, cards, file);
    }

}
