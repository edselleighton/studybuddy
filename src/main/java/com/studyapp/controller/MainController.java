package com.studyapp.controller;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;
import com.studyapp.service.CardJson;
import com.studyapp.service.CsvImportExportService;
import com.studyapp.service.JsonImportExportService;
import com.studyapp.service.SaveService;

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
    private SaveService saveService;

    public MainController(){
        deckController = new DeckController(this);
        flashcardController = new FlashcardController(this);
        studyController = new StudyController(this);
        reviewController = new ReviewController(this);
        answerChecker = new AnswerChecker();
        saveService = new SaveService();
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

    public String checkAnswer(String expected, String actual){
        return answerChecker.check(expected, actual);
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
        saveService.saveAll(deckController, flashcardController, studyController, reviewController);
        System.out.println("Changes Saved to Database.");
    }

    public boolean hasUnsavedChanges() {
        return deckController.hasPendingChanges()
                || flashcardController.hasPendingChanges()
                || studyController.hasPendingChanges()
                || reviewController.hasPendingChanges();
    }

    // --------- JSON IMPORT / EXPORT --------------
    public void exportDeckToJson(int deckID, File file) throws CustomException {
        Deck deck = findDeck(deckID);
        List<Flashcard> cards = getFlashcardsByDeck(deckID);
        new JsonImportExportService().exportDeckToFile(deck, cards, file);
    }

    public void exportDeckToCsv(int deckID, File file) throws CustomException {
        Deck deck = findDeck(deckID);
        List<Flashcard> cards = getFlashcardsByDeck(deckID);
        new CsvImportExportService().exportDeckToFile(deck, cards, file);
    }

        // --------- CARD PREVIEW (for ImportDialogPanel) ---------------

    /**
     * Parses a JSON file and returns all card data as a flat preview list.
     * No Deck or Flashcard objects are created; the list is for UI preview only.
     *
     * @param  file  the JSON file to parse
     * @return list of card DTOs (difficulty is {@code null} when not set or unrecognised)
     * @throws CustomException on read or parse errors
     */
    public List<CardJson> previewJsonCards(File file) throws CustomException {
        return new JsonImportExportService().previewCards(file);
    }

    /**
     * Parses a CSV file and returns all card data as a flat preview list.
     * No Deck or Flashcard objects are created; the list is for UI preview only.
     *
     * @param  file  the CSV file to parse
     * @return list of card DTOs (difficulty is {@code null} when not set or unrecognised)
     * @throws CustomException on read or parse errors
     */
    public List<CardJson> previewCsvCards(File file) throws CustomException {
        return new CsvImportExportService().previewCards(file);
    }

    /**
     * Adds a list of cards to an existing deck.
     * Objects are created in memory only — call {@link #saveChanges()} to persist.
     *
     * @param deckID the ID of the target deck
     * @param cards  cards to import; each must have a non-null difficulty
     * @throws CustomException if the deck does not exist or a card cannot be created
     */
    public void importCardsToExistingDeck(int deckID, List<CardJson> cards) throws CustomException {
        for (CardJson card : cards) {
            createFlashcard(deckID, card.getQuestion(), card.getAnswer(), card.getDifficulty());
        }
    }

    /**
     * Creates a new deck and imports a list of cards into it.
     * Objects are created in memory only — call {@link #saveChanges()} to persist.
     *
     * @param deckName    name for the new deck (must not be blank)
     * @param description optional description for the new deck (may be blank)
     * @param cards       cards to import; each must have a non-null difficulty
     * @throws CustomException if the deck or any card cannot be created
     */
    public void importCardsToNewDeck(String deckName, String description, List<CardJson> cards)
            throws CustomException {
        Deck newDeck = createDeck(deckName, description);
        for (CardJson card : cards) {
            createFlashcard(newDeck.getDeckID(), card.getQuestion(), card.getAnswer(), card.getDifficulty());
        }
    }

}
