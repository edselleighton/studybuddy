package com.studyapp.model;

import java.time.LocalDateTime;

public class CardReview {
    private int reviewID;
    private int sessionID;
    private int flashcardID;
    private LocalDateTime reviewedAt;
    private boolean isCorrect;

    public CardReview(){};
    
    public CardReview(int reviewID, int sessionID, int flashcardID, LocalDateTime reviewedAt,
            boolean isCorrect) {
        this.reviewID = reviewID;
        this.sessionID = sessionID;
        this.flashcardID = flashcardID;
        this.reviewedAt = reviewedAt;
        this.isCorrect = isCorrect;
    }

    public int getReviewID() { return reviewID; } 
    public void setReviewID(int reviewID) { this.reviewID = reviewID; } 
    public int getStudySessionID() { return sessionID; }
    public void setStudySessionID(int sessionID) { this.sessionID = sessionID; }
    public int getFlashcardID() { return flashcardID; }
    public void setFlashcardID(int flashcardID) { this.flashcardID = flashcardID; }
    public LocalDateTime getReviewedAt() { return reviewedAt; } 
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; } 
    public boolean isCorrect() { return isCorrect; } 
    public void setCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }

}
