package com.studyapp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studyapp.dao.impl.FlashcardDAOImpl;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class FlashcardController {
    private MainController mc;
    private FlashcardDAOImpl flashcardDAOImpl = new FlashcardDAOImpl();
    private List<Flashcard> flashcards = new ArrayList<>();

    private List<Flashcard> addedFlashcards = new ArrayList<>();
    private Map<Integer, Flashcard> modifiedFlashcards = new HashMap<>();
    private List<Integer> deletedFlashcards  = new ArrayList<>();

    private int lastCardID = 999;

    public FlashcardController(MainController mc){
        this.mc = mc;
    }

    public List<Flashcard> allFlashcards(){
        return new ArrayList<>(flashcards);
    }

    public List<Flashcard> getHardFlashcards(){
    return flashcards.stream()
            .filter(i -> i.getDifficulty() != null
                    && i.getDifficulty().equalsIgnoreCase("HARD"))
            .toList();
    }

    public List<Flashcard> getMediumFlashcards(){
        return flashcards.stream()
                .filter(i -> i.getDifficulty() != null
                        && i.getDifficulty().equalsIgnoreCase("MEDIUM"))
                .toList();
    }

    public List<Flashcard> getEasyFlashcards(){
        return flashcards.stream()
                .filter(i -> i.getDifficulty() != null
                        && i.getDifficulty().equalsIgnoreCase("EASY"))
                .toList();
    }

    public List<Flashcard> getFlashcardsByDeck(int deckID){
        return flashcards.stream()
                .filter(card -> card.getDeck().getDeckID() == deckID)
                .toList();
    }

    public Flashcard createFlashcard(int deckID, String question, String answer, String difficulty) throws CustomException{
        //CHECK IF DECK EXISTS
        Deck deck = mc.allDecks().stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (deck == null) {
            throw new CustomException("Deck does not exist.");
        }

        Flashcard flashcard = new Flashcard(lastCardID, deck, question, answer, difficulty, LocalDateTime.now());
        validateConstraints(flashcard);
        flashcards.add(flashcard);
        addedFlashcards.add(flashcard);
        lastCardID++;
        return flashcard;
    }
    //edited so that it throws exception if flashcard doesn't exist, 
    // instead of returning null. 
    //allows to edit flashcard, no design change, just added exception handling.
    public void updateFlashcard(Flashcard flashcard) throws CustomException {
        Flashcard existing = flashcards.stream()
                .filter(i -> i.getCardID() == flashcard.getCardID())
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("Flashcard not found.");
        }

        // Remove first so validateConstraints doesn't see a duplicate ID.
        flashcards.remove(existing);

        try {
            validateConstraints(flashcard);
        } catch (CustomException e) {
            // Rollback: put the original back if validation fails.
            flashcards.add(existing);
            throw e;
        }

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

    void loadFLashcard(){
        try{
            flashcards = flashcardDAOImpl.getAllFlashcards();
            lastCardID = flashcardDAOImpl.getLastID() + 1;
        }catch(Exception e){
            System.out.println("Failed to Load Flashcards");
        }
    }

    void saveFlashcardToDB() throws CustomException{
        try{
            for(Flashcard flashcard: addedFlashcards){
                flashcardDAOImpl.insert(flashcard);
            }
            for(Flashcard flashcard: modifiedFlashcards.values()){
                flashcardDAOImpl.update(flashcard);
            }
            for(int flashcardID: deletedFlashcards){
                flashcardDAOImpl.delete(flashcardID);
            }
            addedFlashcards.clear();
            modifiedFlashcards.clear();
            deletedFlashcards.clear();
        }catch(Exception e){
            throw new CustomException("Failed to Save Flashcards");
        }
    }

    void saveAddedFlashcards(List<Flashcard> flashcardsToSave) throws CustomException {
        try {
            for (Flashcard flashcard : flashcardsToSave) {
                if (addedFlashcards.contains(flashcard)) {
                    flashcardDAOImpl.insert(flashcard);
                }
            }
            addedFlashcards.removeAll(flashcardsToSave);
        } catch (Exception e) {
            throw new CustomException("Failed to Save Flashcards");
        }
    }

    public boolean hasPendingChanges() {
        return !addedFlashcards.isEmpty() || !modifiedFlashcards.isEmpty() || !deletedFlashcards.isEmpty();
    }

    void validateConstraints(Flashcard flashcard) throws CustomException{
        //VALIDATE ID UNIQUENESS
        if(flashcards.stream().anyMatch(i -> (i.getCardID() == flashcard.getCardID()) && (i != flashcard))) {
            throw new CustomException("Flashcard ID already exists.");
        }

        //CHECK IF QUESTIONS IS EMPTY
        if(flashcard.getQuestion() == null || flashcard.getQuestion().trim().isEmpty()) {
            throw new CustomException("Question field cannot be empty.");
        }

        //CHECK IF ANSWER IS EMPTY
        if(flashcard.getAnswer() == null || flashcard.getAnswer().trim().isEmpty()) {
            throw new CustomException("Answer field cannot be empty.");
        }

        //CHECK IF DIFFICULTY IS VALID
        if(flashcard.getDifficulty() == null || flashcard.getDifficulty().trim().isEmpty()) {
            throw new CustomException("Difficulty field cannot be empty.");
        }else if(!flashcard.getDifficulty().equalsIgnoreCase("Easy") &&
                !flashcard.getDifficulty().equalsIgnoreCase("Medium") &&
                !flashcard.getDifficulty().equalsIgnoreCase("Hard")){
            throw new CustomException("Difficulty must be either Easy, Medium, or Hard.");
        }
    }
}
