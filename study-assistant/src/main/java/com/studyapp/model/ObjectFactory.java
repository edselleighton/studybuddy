package com.studyapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.studyapp.dao.impl.DeckDAOImpl;
import com.studyapp.dao.impl.FlashcardDAOImpl;
import com.studyapp.dao.impl.StudySessionDAOImpl;

//A HELPER CLASS FOR CREATING NEW OBJECT INSTANCES
public class ObjectFactory {

    //HELPER METHOD FOR CREATING A NEW DECK OBJECT
    public Deck createNewDeck(ResultSet rs){
        try{
            Deck deck = new Deck();
            deck.setDeckID(rs.getInt("deck_id"));
            deck.setName(rs.getString("name"));
            deck.setDescription(rs.getString("description"));
            
            Object createdAtObj = rs.getObject("created_at");
            if (createdAtObj instanceof LocalDateTime) {
                deck.setCreatedAt((LocalDateTime) createdAtObj);
            } else if (createdAtObj instanceof java.sql.Timestamp) {
                deck.setCreatedAt(((java.sql.Timestamp) createdAtObj).toLocalDateTime());
            }
            
            return deck;
        }catch(SQLException e) {
            e.printStackTrace();
        }
            return null;
    }

    //HELPER METHOD FOR CREATING A NEW FLASHCARD OBJECT
    public Flashcard createNewCard(ResultSet rs){
        try{
            Flashcard card = new Flashcard();
            card.setCardID(rs.getInt("card_id"));
            card.setQuestion(rs.getString("question"));
            card.setAnswer(rs.getString("answer"));
            card.setDifficulty(rs.getString("difficulty"));

            //CREATE NEW DECk OBJECT
            Deck deck = new DeckDAOImpl().findByID(rs.getInt("deck_id"));
            card.setDeck(deck);
            
            Object createdAtObj = rs.getObject("created_at");
            if (createdAtObj instanceof LocalDateTime) {
                card.setCreatedAt((LocalDateTime) createdAtObj);
            } else if (createdAtObj instanceof java.sql.Timestamp) {
                card.setCreatedAt(((java.sql.Timestamp) createdAtObj).toLocalDateTime());
            }
            
            return card;
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //HELPER METHOD FOR CREATING A NEW STUDYSESSION OBJECT
    public StudySession createStudySession(ResultSet rs){
        try{
            StudySession studySession = new StudySession();
            studySession.setSessionID(rs.getInt("session_id"));

            //CREATE NEW DECK
            Deck deck = new DeckDAOImpl().findByID(rs.getInt("deck_id"));
            studySession.setDeck(deck);
        
            Object startedAObject = rs.getObject("started_at");
            if (startedAObject instanceof LocalDateTime) {
                studySession.setStartedAt((LocalDateTime) startedAObject);
            } else if (startedAObject instanceof java.sql.Timestamp) {
                studySession.setStartedAt(((java.sql.Timestamp) startedAObject).toLocalDateTime());
            }

            Object endedAObject = rs.getObject("ended_at");
            if (endedAObject instanceof LocalDateTime) {
                studySession.setEndedAt((LocalDateTime) endedAObject);
            } else if (endedAObject instanceof java.sql.Timestamp) {
                studySession.setEndedAt(((java.sql.Timestamp) endedAObject).toLocalDateTime());
            }

            return studySession;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //HELPER METHOD FOR CREATING A NEW CARD REVIEW OBJECT
    public CardReview createNewReview(ResultSet rs){
        try{
            CardReview cardReview = new CardReview();
            cardReview.setReviewID(rs.getInt("review_id"));

            StudySession studySession = new StudySessionDAOImpl().findByID(rs.getInt("session_id"));
            cardReview.setStudySession(studySession);

            Flashcard flashcard = new FlashcardDAOImpl().findByID(rs.getInt("card_id"));
            cardReview.setFlashcard(flashcard);

            Object reviewedAtObject = rs.getObject("reviewed_at");
            if (reviewedAtObject instanceof LocalDateTime) {
                cardReview.setReviewedAt((LocalDateTime) reviewedAtObject);
            } else if (reviewedAtObject instanceof java.sql.Timestamp) {
                cardReview.setReviewedAt(((java.sql.Timestamp) reviewedAtObject).toLocalDateTime());
            }

            cardReview.setCorrect(rs.getBoolean("is_correct"));

            return cardReview;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
