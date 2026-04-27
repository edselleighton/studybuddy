package com.studyapp.controller;

import com.studyapp.dao.impl.CardReviewDAOImpl;
import com.studyapp.model.CardReview;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewController {
    private MainController mc;
    private CardReviewDAOImpl cardReviewDAOImp = new CardReviewDAOImpl();

    private List<CardReview> cardReviews = new ArrayList<>();
    private List<CardReview> addedCardReviews = new ArrayList<>();
    private List<Integer> deletedCardReviews = new ArrayList<>();
    private int lastReviewID = 999;

    public ReviewController(MainController mc){
        this.mc = mc;
    }

    public void createCardReview(int sessionID, int cardID, LocalDateTime reviewedAt, boolean isCorrect) throws CustomException{
        StudySession studySession = mc.getAllSessions().stream()
                .filter(i -> i.getSessionID() == sessionID)
                .findFirst().orElse(null);
        if (studySession == null) {
            throw new CustomException("Study session not found.");
        }

        Flashcard flashcard = mc.allFlashcards().stream()
                .filter(i -> i.getCardID() == cardID)
                .findFirst().orElse(null);
        if (flashcard == null) {
            throw new CustomException("Flashcard not found.");
        }

        CardReview cardReview = new CardReview(lastReviewID, studySession.getSessionID(), flashcard.getCardID(), reviewedAt, isCorrect);
        cardReviews.add(cardReview);
        addedCardReviews.add(cardReview);
        lastReviewID++;
    }

    public List<CardReview> getAllCardReviews(){
        return new ArrayList<>(cardReviews);
    }

    public List<CardReview> getCorrectReviews(){
        return cardReviews.stream()
                .filter(CardReview::isCorrect)
                .toList();
    }

    public Collection<CardReview> getLatestUniqueReviews(List<CardReview> reviews) {
        return reviews.stream()
                .collect(Collectors.toMap(
                        CardReview::getFlashcardID,
                        review -> review,
                        (existing, replacement) ->
                                replacement.getReviewedAt().isAfter(existing.getReviewedAt()) ? replacement : existing
                ))
                .values();
    }

    public List<CardReview> getCardReviewsBySession(int sessionID){
        return cardReviews.stream()
                .filter(i -> i.getStudySessionID() == sessionID)
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

    void loadCardReviews(){
        cardReviews =cardReviewDAOImp.getAllReviews();
        lastReviewID = cardReviewDAOImp.getLastID() + 1;
    }

    void saveReviewToDB() throws CustomException{
        try{
            for(CardReview review: addedCardReviews){
                cardReviewDAOImp.insert(review);
            }
            for(int reviewID: deletedCardReviews){
                cardReviewDAOImp.delete(reviewID);
            }
            addedCardReviews.clear();
            deletedCardReviews.clear();

        }catch(Exception e){
            throw new CustomException(e.getMessage());
        }
    }

    public boolean hasPendingChanges() {
        return !addedCardReviews.isEmpty() || !deletedCardReviews.isEmpty();
    }

    void validateConstraints(CardReview cardReview) throws CustomException{
        //VALIDATE UNIQUE ID
        if(cardReviews.stream().anyMatch(i -> (i.getReviewID() == cardReview.getReviewID()) && (i != cardReview))){
            throw new CustomException("Review ID already exists.");
        }

        // CHECK IF DATE IS NOT NULL
        if(cardReview.getReviewedAt() == null){
            throw new CustomException("Date cannot be empty.");
        }
    }
}
