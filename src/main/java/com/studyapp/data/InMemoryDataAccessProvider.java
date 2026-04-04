package com.studyapp.data;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.dao.DeckDAO;
import com.studyapp.dao.FlashcardDAO;
import com.studyapp.dao.StudySessionDAO;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

public class InMemoryDataAccessProvider implements DataAccessProvider {

    private final InMemoryStore store = new InMemoryStore();
    private final DeckDAO deckDAO = new InMemoryDeckDAO(store);
    private final FlashcardDAO flashcardDAO = new InMemoryFlashcardDAO(store);
    private final StudySessionDAO studySessionDAO = new InMemoryStudySessionDAO(store);
    private final CardReviewDAO cardReviewDAO = new InMemoryCardReviewDAO(store);

    @Override
    public DeckDAO decks() {
        return deckDAO;
    }

    @Override
    public FlashcardDAO flashcards() {
        return flashcardDAO;
    }

    @Override
    public StudySessionDAO studySessions() {
        return studySessionDAO;
    }

    @Override
    public CardReviewDAO cardReviews() {
        return cardReviewDAO;
    }

    private static final class InMemoryStore {
        private final List<Deck> decks = new ArrayList<>();
        private final List<Flashcard> flashcards = new ArrayList<>();
        private final List<StudySession> studySessions = new ArrayList<>();
        private final List<CardReview> cardReviews = new ArrayList<>();
        private final AtomicInteger nextDeckId = new AtomicInteger(100);
        private final AtomicInteger nextCardId = new AtomicInteger(1000);
        private final AtomicInteger nextSessionId = new AtomicInteger(500);
        private final AtomicInteger nextReviewId = new AtomicInteger(900);

        private InMemoryStore() {
            seed();
        }

        private void seed() {
            Deck cmsc = addDeck("CMSC127 - LE2", "Covers SQL, ER diagrams, and normalization.", LocalDateTime.now().minusDays(6));
            Deck math = addDeck("MATH55 - LE4", "Limits, derivatives, and quick formula recall.", LocalDateTime.now().minusDays(4));
            Deck bio = addDeck("BIOLOGY - QUIZ", "Cell structure, respiration, and genetics.", LocalDateTime.now().minusDays(2));

            Flashcard jdbc = addCard(cmsc, "What does JDBC stand for?", "Java Database Connectivity", "Medium", LocalDateTime.now().minusDays(6));
            Flashcard normalization = addCard(cmsc, "What is normalization?", "A process for organizing data to reduce redundancy.", "Hard", LocalDateTime.now().minusDays(6));
            Flashcard primaryKey = addCard(cmsc, "What is a primary key?", "A column that uniquely identifies a row.", "Easy", LocalDateTime.now().minusDays(5));
            Flashcard sql = addCard(cmsc, "What does SQL stand for?", "Structured Query Language", "Easy", LocalDateTime.now().minusDays(5));
            Flashcard derivative = addCard(math, "What is the derivative of x^2?", "2x", "Easy", LocalDateTime.now().minusDays(4));
            Flashcard limit = addCard(math, "What is a limit?", "The value a function approaches as the input approaches a point.", "Medium", LocalDateTime.now().minusDays(4));
            Flashcard chainRule = addCard(math, "State the chain rule.", "Derivative of outer times derivative of inner.", "Hard", LocalDateTime.now().minusDays(3));
            Flashcard mitochondria = addCard(bio, "What is the function of the mitochondria?", "It produces energy for the cell.", "Easy", LocalDateTime.now().minusDays(2));
            Flashcard dna = addCard(bio, "What carries genetic information?", "DNA", "Easy", LocalDateTime.now().minusDays(2));
            Flashcard respiration = addCard(bio, "What is cellular respiration?", "The process cells use to convert glucose into ATP.", "Medium", LocalDateTime.now().minusDays(1));

            StudySession cmscMorning = addSession(cmsc, LocalDateTime.now().minusDays(1).minusMinutes(80), LocalDateTime.now().minusDays(1));
            StudySession cmscReview = addSession(cmsc, LocalDateTime.now().minusHours(12).minusMinutes(35), LocalDateTime.now().minusHours(12));
            StudySession mathSession = addSession(math, LocalDateTime.now().minusHours(30).minusMinutes(45), LocalDateTime.now().minusHours(30));
            StudySession bioSession = addSession(bio, LocalDateTime.now().minusHours(6).minusMinutes(25), LocalDateTime.now().minusHours(6));

            addReview(cmscMorning, jdbc, true, LocalDateTime.now().minusHours(23));
            addReview(cmscMorning, normalization, false, LocalDateTime.now().minusHours(23).plusMinutes(5));
            addReview(cmscReview, primaryKey, true, LocalDateTime.now().minusHours(12).plusMinutes(8));
            addReview(cmscReview, sql, true, LocalDateTime.now().minusHours(12).plusMinutes(12));
            addReview(mathSession, derivative, true, LocalDateTime.now().minusHours(29));
            addReview(mathSession, limit, true, LocalDateTime.now().minusHours(29).plusMinutes(6));
            addReview(mathSession, chainRule, false, LocalDateTime.now().minusHours(29).plusMinutes(10));
            addReview(bioSession, mitochondria, true, LocalDateTime.now().minusHours(5));
            addReview(bioSession, dna, true, LocalDateTime.now().minusHours(5).plusMinutes(4));
        }

        private Deck addDeck(String name, String description, LocalDateTime createdAt) {
            Deck deck = new Deck(nextDeckId.getAndIncrement(), name, description, createdAt);
            decks.add(deck);
            return deck;
        }

        private Flashcard addCard(Deck deck, String question, String answer, String difficulty, LocalDateTime createdAt) {
            Flashcard card = new Flashcard(nextCardId.getAndIncrement(), deck, question, answer, difficulty, createdAt);
            flashcards.add(card);
            return card;
        }

        private StudySession addSession(Deck deck, LocalDateTime startedAt, LocalDateTime endedAt) {
            StudySession session = new StudySession(nextSessionId.getAndIncrement(), deck, startedAt, endedAt);
            studySessions.add(session);
            return session;
        }

        private void addReview(StudySession session, Flashcard card, boolean isCorrect, LocalDateTime reviewedAt) {
            cardReviews.add(new CardReview(nextReviewId.getAndIncrement(), session, card, reviewedAt, isCorrect));
        }
    }

    private static final class InMemoryDeckDAO implements DeckDAO {
        private final InMemoryStore store;

        private InMemoryDeckDAO(InMemoryStore store) {
            this.store = store;
        }

        @Override
        public void insert(Deck deck) throws SQLException {
            deck.setDeckID(store.nextDeckId.getAndIncrement());
            if (deck.getCreatedAt() == null) {
                deck.setCreatedAt(LocalDateTime.now());
            }
            store.decks.add(deck);
        }

        @Override
        public void update(Deck deck) throws SQLException {
            for (int i = 0; i < store.decks.size(); i++) {
                if (store.decks.get(i).getDeckID() == deck.getDeckID()) {
                    store.decks.set(i, deck);
                    return;
                }
            }
            throw new SQLException("Deck not found: " + deck.getDeckID());
        }

        @Override
        public void delete(int deckID) throws SQLException {
            boolean removed = store.decks.removeIf(deck -> deck.getDeckID() == deckID);
            store.flashcards.removeIf(card -> card.getDeck() != null && card.getDeck().getDeckID() == deckID);
            store.studySessions.removeIf(session -> session.getDeck() != null && session.getDeck().getDeckID() == deckID);
            Set<Integer> remainingCardIds = store.flashcards.stream().map(Flashcard::getCardID).collect(Collectors.toSet());
            store.cardReviews.removeIf(review -> review.getFlashcard() == null || !remainingCardIds.contains(review.getFlashcard().getCardID()));
            if (!removed) {
                throw new SQLException("Deck not found: " + deckID);
            }
        }

        @Override
        public Deck findByID(int deckID) {
            return store.decks.stream().filter(deck -> deck.getDeckID() == deckID).findFirst().orElse(null);
        }

        @Override
        public List<Deck> getAllDecks() {
            return new ArrayList<>(store.decks);
        }
    }

    private static final class InMemoryFlashcardDAO implements FlashcardDAO {
        private final InMemoryStore store;

        private InMemoryFlashcardDAO(InMemoryStore store) {
            this.store = store;
        }

        @Override
        public void insert(Flashcard flashcard) throws SQLException {
            flashcard.setCardID(store.nextCardId.getAndIncrement());
            if (flashcard.getCreatedAt() == null) {
                flashcard.setCreatedAt(LocalDateTime.now());
            }
            store.flashcards.add(flashcard);
        }

        @Override
        public void update(Flashcard flashcard) throws SQLException {
            for (int i = 0; i < store.flashcards.size(); i++) {
                if (store.flashcards.get(i).getCardID() == flashcard.getCardID()) {
                    store.flashcards.set(i, flashcard);
                    return;
                }
            }
            throw new SQLException("Flashcard not found: " + flashcard.getCardID());
        }

        @Override
        public void delete(int cardID) throws SQLException {
            boolean removed = store.flashcards.removeIf(card -> card.getCardID() == cardID);
            store.cardReviews.removeIf(review -> review.getFlashcard() != null && review.getFlashcard().getCardID() == cardID);
            if (!removed) {
                throw new SQLException("Flashcard not found: " + cardID);
            }
        }

        @Override
        public Flashcard findByID(int cardID) {
            return store.flashcards.stream().filter(card -> card.getCardID() == cardID).findFirst().orElse(null);
        }

        @Override
        public List<Flashcard> getAllFlashcards() {
            return new ArrayList<>(store.flashcards);
        }
    }

    private static final class InMemoryStudySessionDAO implements StudySessionDAO {
        private final InMemoryStore store;

        private InMemoryStudySessionDAO(InMemoryStore store) {
            this.store = store;
        }

        @Override
        public void insert(StudySession studySession) throws SQLException {
            studySession.setSessionID(store.nextSessionId.getAndIncrement());
            if (studySession.getStartedAt() == null) {
                studySession.setStartedAt(LocalDateTime.now());
            }
            store.studySessions.add(studySession);
        }

        @Override
        public void updateEnd(LocalDateTime endedAt) throws SQLException {
            if (store.studySessions.isEmpty()) {
                throw new SQLException("No study sessions available.");
            }
            store.studySessions.get(store.studySessions.size() - 1).setEndedAt(endedAt);
        }

        @Override
        public StudySession findByID(int sessionID) {
            return store.studySessions.stream().filter(session -> session.getSessionID() == sessionID).findFirst().orElse(null);
        }

        @Override
        public List<StudySession> getAllSessions() {
            return new ArrayList<>(store.studySessions);
        }
    }

    private static final class InMemoryCardReviewDAO implements CardReviewDAO {
        private final InMemoryStore store;

        private InMemoryCardReviewDAO(InMemoryStore store) {
            this.store = store;
        }

        @Override
        public void insert(CardReview cardReview) throws SQLException {
            cardReview.setReviewID(store.nextReviewId.getAndIncrement());
            if (cardReview.getReviewedAt() == null) {
                cardReview.setReviewedAt(LocalDateTime.now());
            }
            store.cardReviews.add(cardReview);
        }

        @Override
        public CardReview findByID(int reviewID) {
            return store.cardReviews.stream().filter(review -> review.getReviewID() == reviewID).findFirst().orElse(null);
        }

        @Override
        public List<CardReview> getAllReviews() {
            return new ArrayList<>(store.cardReviews);
        }
    }
}
