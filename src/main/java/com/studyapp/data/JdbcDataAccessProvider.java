package com.studyapp.data;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.dao.DeckDAO;
import com.studyapp.dao.FlashcardDAO;
import com.studyapp.dao.StudySessionDAO;
import com.studyapp.dao.impl.CardReviewDAOImpl;
import com.studyapp.dao.impl.DeckDAOImpl;
import com.studyapp.dao.impl.FlashcardDAOImpl;
import com.studyapp.dao.impl.StudySessionDAOImpl;

public class JdbcDataAccessProvider implements DataAccessProvider {

    private final DeckDAO deckDAO = new DeckDAOImpl();
    private final FlashcardDAO flashcardDAO = new FlashcardDAOImpl();
    private final StudySessionDAO studySessionDAO = new StudySessionDAOImpl();
    private final CardReviewDAO cardReviewDAO = new CardReviewDAOImpl();

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
}
