package com.studyapp.model;

import java.time.LocalDateTime;

public class StudySession {
    private int sessionID;
    private int deckID;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public StudySession() { }

    public StudySession(int sessionID, int deckID, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.sessionID = sessionID;
        this.deckID = deckID;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public int getSessionID() { return sessionID; } 
    public void setSessionID(int sessionID) { this.sessionID = sessionID; } 
    public int getDeckID() { return deckID; }
    public void setDeckID(int deckID) { this.deckID = deckID; }
    public LocalDateTime getStartedAt() { return startedAt; } 
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; } 
    public LocalDateTime getEndedAt() { return endedAt; } 
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

}
