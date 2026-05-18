package com.studyapp.controller;

import com.studyapp.dao.impl.StudySessionDAOImpl;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.StudySession;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class StudyController {
    private  MainController mc;
    private StudySessionDAOImpl studySessionDAOImpl = new StudySessionDAOImpl();

    private List<StudySession> studySessions = new ArrayList<>();
    private List<StudySession> addedStudySessions = new ArrayList<>();
    private Map<Integer, StudySession> modifiedStudySessions = new HashMap<>();
    private List<Integer> deletedStudySessions  = new ArrayList<>();

    private int lastSessionID = 999;

    public  StudyController(MainController mc){
        this.mc = mc;
    }

    public StudySession createStudySession(int deckID, LocalDateTime startedAt) throws CustomException{
        Deck deck = mc.allDecks().stream()
                .filter(i -> i.getDeckID() == deckID)
                .findFirst().orElse(null);
        if (deck == null) {
            throw new CustomException("Deck does not exist.");
        }

        StudySession studySession = new StudySession(lastSessionID, deck.getDeckID(), startedAt, null);

        validateConstraints(studySession);
        studySessions.add(studySession);
        addedStudySessions.add(studySession);

        lastSessionID++;

        return studySession;
    }

    public void updateEndStudySession(StudySession studySession) throws CustomException{
        StudySession existing  = studySessions.stream()
                .filter(i -> i.getSessionID() == studySession.getSessionID())
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("Study session not found.");
        }

        if(studySession.getEndedAt() == null){
            throw new CustomException("End date cannot be empty.");
        }

        studySessions.remove(existing);
        studySessions.add(studySession);

        if (addedStudySessions.contains(existing)){
            addedStudySessions.remove(existing);
            addedStudySessions.add(studySession);
        }else{
            modifiedStudySessions.put(studySession.getSessionID(), studySession);
        }
    }

    public List<StudySession> getAllSessions(){
        return new ArrayList<>(studySessions);
    }

    public List<StudySession> getRecentSessions() {
        return studySessions.stream()
                .filter(i -> i.getEndedAt() != null)
                .sorted(Comparator.comparing(StudySession::getEndedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<StudySession> getSpecificDeckSession(int deckID){
        return studySessions.stream()
                .filter(i -> i.getDeckID() == deckID)
                .toList();
    }

    public void deleteSession(int sessionID) throws CustomException{
        StudySession existing = studySessions.stream()
                .filter(i -> i.getSessionID() == sessionID)
                .findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException("No record matched. No row was deleted.");
        }

        for(CardReview review: mc.getAllCardReviews()){
            if(review.getStudySessionID() == sessionID){
                mc.deleteCardReview(review.getReviewID());
            }
        }

        studySessions.remove(existing);

        if (addedStudySessions.contains(existing)) {
            addedStudySessions.remove(existing);
        } else {
            modifiedStudySessions.remove(sessionID);
            deletedStudySessions.add(sessionID);
        }
    }

    void validateConstraints(StudySession studySession) throws CustomException{
        //VALIDATE UNIQUE ID
        if(studySessions.stream().anyMatch(i -> (i.getSessionID() == studySession.getSessionID()) && (i != studySession))){
            throw new CustomException("Session ID already exists.");
        }

        //CHECK IF DATE IS NOT EMPTY
        if(studySession.getStartedAt() == null){
            throw new CustomException("Date cannot be empty.");
        }
    }

    void loadStudySessions() throws CustomException{
        try{
            studySessions = studySessionDAOImpl.getAllSessions();
            lastSessionID = studySessionDAOImpl.getLastID() + 1;
        }catch(Exception e){
            throw new CustomException("Failed to Load Study Sessions");
        }
    }

    void saveNewStudySessionsToDB() throws CustomException {
        try{
            persistNewStudySessions(null);
        }catch(Exception e){
            throw new CustomException("Failed to Save Study Sessions");
        }
    }

    void finalizeStudySessionsToDB() throws CustomException {
        try {
            finalizeStudySessions(null);
            markPendingChangesSaved();
        }catch(Exception e){
            throw new CustomException("Failed to Save Study Sessions");
        }
    }

    public boolean hasPendingChanges() {
        return !addedStudySessions.isEmpty()
                || !modifiedStudySessions.isEmpty()
                || !deletedStudySessions.isEmpty();
    }

    public void persistNewStudySessions(Connection conn) throws Exception {
        for (StudySession session : addedStudySessions) {
            StudySession openSession = new StudySession(
                    session.getSessionID(),
                    session.getDeckID(),
                    session.getStartedAt(),
                    null
            );
            if (conn == null) {
                studySessionDAOImpl.insert(openSession);
            } else {
                studySessionDAOImpl.insert(conn, openSession);
            }
        }
    }

    public void finalizeStudySessions(Connection conn) throws Exception {
        for (StudySession session : addedStudySessions) {
            if (session.getEndedAt() != null) {
                if (conn == null) {
                    studySessionDAOImpl.updateEnd(session.getSessionID(), session.getEndedAt());
                } else {
                    studySessionDAOImpl.updateEnd(conn, session.getSessionID(), session.getEndedAt());
                }
            }
        }
        for (StudySession session : modifiedStudySessions.values()) {
            if (conn == null) {
                studySessionDAOImpl.updateEnd(session.getSessionID(), session.getEndedAt());
            } else {
                studySessionDAOImpl.updateEnd(conn, session.getSessionID(), session.getEndedAt());
            }
        }
        for (int sessionID : deletedStudySessions) {
            if (conn == null) {
                studySessionDAOImpl.delete(sessionID);
            } else {
                studySessionDAOImpl.delete(conn, sessionID);
            }
        }
    }

    public void markPendingChangesSaved() {
        addedStudySessions.clear();
        modifiedStudySessions.clear();
        deletedStudySessions.clear();
    }
}
