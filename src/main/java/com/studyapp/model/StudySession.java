package com.studyapp.model;

import java.time.LocalDateTime;

public class StudySession {
    private int sessionID;
    private Deck deck;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public StudySession() {}

    public StudySession(int sessionID, Deck deck, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.sessionID = sessionID;
        this.deck = deck;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public int getSessionID() { return sessionID; }
    public void setSessionID(int sessionID) { this.sessionID = sessionID; }
    public Deck getDeck() { return deck; }
    public void setDeck(Deck deck) { this.deck = deck; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
}
