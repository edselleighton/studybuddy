package com.studyapp.controller;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.dao.FlashcardDAO;
import com.studyapp.dao.StudySessionDAO;
import com.studyapp.model.CardReview;
import com.studyapp.model.Flashcard;

public class ProgressController {

    private final FlashcardDAO flashcardDAO;
    private final CardReviewDAO cardReviewDAO;
    private final StudySessionDAO studySessionDAO;

    public ProgressController(FlashcardDAO flashcardDAO, CardReviewDAO cardReviewDAO, StudySessionDAO studySessionDAO) {
        this.flashcardDAO = flashcardDAO;
        this.cardReviewDAO = cardReviewDAO;
        this.studySessionDAO = studySessionDAO;
    }

    public double getOverallAccuracyPercent() {
        List<CardReview> reviews = cardReviewDAO.getAllReviews();
        if (reviews.isEmpty()) {
            return 0;
        }
        long correct = reviews.stream().filter(CardReview::isCorrect).count();
        return (correct * 100.0) / reviews.size();
    }

    public int getCardsReviewedCount() {
        Set<Integer> reviewedCards = cardReviewDAO.getAllReviews().stream()
                .map(CardReview::getFlashcard)
                .filter(card -> card != null)
                .map(Flashcard::getCardID)
                .collect(Collectors.toSet());
        return reviewedCards.size();
    }

    public int getTotalCardCount() {
        return flashcardDAO.getAllFlashcards().size();
    }

    public long getTotalStudyMinutes() {
        return studySessionDAO.getAllSessions().stream()
                .filter(session -> session.getStartedAt() != null && session.getEndedAt() != null)
                .mapToLong(session -> Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes())
                .sum();
    }

    public String getAccuracyLabel() {
        return String.format("%.1f%%", getOverallAccuracyPercent());
    }

    public String getReviewedCardsLabel() {
        return getCardsReviewedCount() + " / " + getTotalCardCount();
    }

    public String getStudyTimeLabel() {
        long minutes = getTotalStudyMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (hours == 0) {
            return remainingMinutes + " min";
        }
        return hours + " hr " + remainingMinutes + "m";
    }
}
