package com.studyapp.controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.studyapp.dao.FlashcardDAO;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class StudyController {

    private final FlashcardDAO flashcardDAO;

    public StudyController(FlashcardDAO flashcardDAO) {
        this.flashcardDAO = flashcardDAO;
    }

    public List<Flashcard> getCards(Deck deck) {
        return flashcardDAO.getAllFlashcards().stream()
                .filter(card -> deck == null || (card.getDeck() != null && card.getDeck().getDeckID() == deck.getDeckID()))
                .toList();
    }

    public Map<String, Integer> getDifficultyBreakdown() {
        Map<String, Integer> breakdown = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        breakdown.put("Easy", 0);
        breakdown.put("Medium", 0);
        breakdown.put("Hard", 0);

        for (Flashcard card : flashcardDAO.getAllFlashcards()) {
            String difficulty = card.getDifficulty() == null ? "Medium" : card.getDifficulty();
            breakdown.put(difficulty, breakdown.getOrDefault(difficulty, 0) + 1);
        }
        return breakdown;
    }
}
