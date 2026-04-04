package com.studyapp.model;

import java.time.LocalDateTime;

public class CardReview {
    private int reviewID;
    private StudySession studySession;
    private Flashcard flashcard;
    private LocalDateTime reviewedAt;
    private boolean isCorrect;

    public CardReview() {}

    public CardReview(int reviewID, StudySession studySession, Flashcard flashcard, LocalDateTime reviewedAt,
            boolean isCorrect) {
        this.reviewID = reviewID;
        this.studySession = studySession;
        this.flashcard = flashcard;
        this.reviewedAt = reviewedAt;
        this.isCorrect = isCorrect;
    }

    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }
    public StudySession getStudySession() { return studySession; }
    public void setStudySession(StudySession studySession) { this.studySession = studySession; }
    public Flashcard getFlashcard() { return flashcard; }
    public void setFlashcard(Flashcard flashcard) { this.flashcard = flashcard; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }
}
