package com.studyapp.data;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.dao.DeckDAO;
import com.studyapp.dao.FlashcardDAO;
import com.studyapp.dao.StudySessionDAO;

public interface DataAccessProvider {
    DeckDAO decks();
    FlashcardDAO flashcards();
    StudySessionDAO studySessions();
    CardReviewDAO cardReviews();
}
