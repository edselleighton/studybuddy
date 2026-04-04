package com.studyapp.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.dao.DeckDAO;
import com.studyapp.dao.FlashcardDAO;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class DeckController {

    public static final class DeckStats {
        private final int cardCount;
        private final int reviewedCardCount;

        public DeckStats(int cardCount, int reviewedCardCount) {
            this.cardCount = cardCount;
            this.reviewedCardCount = reviewedCardCount;
        }

        public int getCardCount() {
            return cardCount;
        }

        public int getReviewedCardCount() {
            return reviewedCardCount;
        }

        public double getProgressPercent() {
            if (cardCount == 0) {
                return 0;
            }
            return (reviewedCardCount * 100.0) / cardCount;
        }
    }

    private final DeckDAO deckDAO;
    private final FlashcardDAO flashcardDAO;
    private final CardReviewDAO cardReviewDAO;

    public DeckController(DeckDAO deckDAO, FlashcardDAO flashcardDAO, CardReviewDAO cardReviewDAO) {
        this.deckDAO = deckDAO;
        this.flashcardDAO = flashcardDAO;
        this.cardReviewDAO = cardReviewDAO;
    }

    public List<Deck> getAllDecks() {
        return deckDAO.getAllDecks().stream()
                .sorted(Comparator.comparing(Deck::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    public List<Deck> getRecentDecks(int limit) {
        return getAllDecks().stream().limit(limit).toList();
    }

    public Deck getDeck(int deckID) {
        return deckDAO.findByID(deckID);
    }

    public List<Flashcard> getCardsForDeck(int deckID) {
        return flashcardDAO.getAllFlashcards().stream()
                .filter(card -> card.getDeck() != null && card.getDeck().getDeckID() == deckID)
                .sorted(Comparator.comparing(Flashcard::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<Flashcard> getPreviewCards(int deckID, int limit) {
        return getCardsForDeck(deckID).stream().limit(limit).toList();
    }

    public Map<Integer, DeckStats> getDeckStats() {
        List<Flashcard> allCards = flashcardDAO.getAllFlashcards();
        List<CardReview> allReviews = cardReviewDAO.getAllReviews();

        Map<Integer, Integer> cardCounts = new HashMap<>();
        Map<Integer, Set<Integer>> deckCardIds = new HashMap<>();
        Map<Integer, Integer> cardToDeck = new HashMap<>();

        for (Flashcard card : allCards) {
            if (card.getDeck() == null) {
                continue;
            }
            int deckID = card.getDeck().getDeckID();
            cardCounts.merge(deckID, 1, Integer::sum);
            deckCardIds.computeIfAbsent(deckID, ignored -> new HashSet<>()).add(card.getCardID());
            cardToDeck.put(card.getCardID(), deckID);
        }

        Map<Integer, Set<Integer>> reviewedCardIdsByDeck = new HashMap<>();
        for (CardReview review : allReviews) {
            if (review.getFlashcard() == null) {
                continue;
            }
            Integer deckID = cardToDeck.get(review.getFlashcard().getCardID());
            if (deckID == null) {
                Flashcard reviewCard = review.getFlashcard();
                if (reviewCard.getDeck() != null) {
                    deckID = reviewCard.getDeck().getDeckID();
                }
            }
            if (deckID != null) {
                reviewedCardIdsByDeck.computeIfAbsent(deckID, ignored -> new HashSet<>()).add(review.getFlashcard().getCardID());
            }
        }

        Map<Integer, DeckStats> statsByDeck = new HashMap<>();
        for (Deck deck : getAllDecks()) {
            int deckID = deck.getDeckID();
            int cardCount = cardCounts.getOrDefault(deckID, 0);
            int reviewedCardCount = reviewedCardIdsByDeck.getOrDefault(deckID, Set.of()).size();
            statsByDeck.put(deckID, new DeckStats(cardCount, reviewedCardCount));
        }
        return statsByDeck;
    }

    public int getCardCount(int deckID) {
        return getDeckStats().getOrDefault(deckID, new DeckStats(0, 0)).getCardCount();
    }

    public int getReviewedCardCount(int deckID) {
        return getDeckStats().getOrDefault(deckID, new DeckStats(0, 0)).getReviewedCardCount();
    }

    public double getProgressPercent(int deckID) {
        return getDeckStats().getOrDefault(deckID, new DeckStats(0, 0)).getProgressPercent();
    }
}
