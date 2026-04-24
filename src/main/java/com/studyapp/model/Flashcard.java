package com.studyapp.model;

import java.time.LocalDateTime;

public class Flashcard {
    private int cardID;
    private int deckID;
    private String question;
    private String answer;
    private String difficulty;
    private LocalDateTime createdAt;

    public Flashcard(){};

    public Flashcard(int cardID, int deckID, String question, String answer, String difficulty,
            LocalDateTime createdAt) {
        this.cardID = cardID;
        this.deckID = deckID;
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
    }

    public int getCardID() { return cardID; } 
    public void setCardID(int cardID) { this.cardID = cardID; } 
    public int getDeckID() { return deckID; }
    public void setDeckID(int deckID) { this.deckID = deckID; }
    public String getQuestion() { return question; } 
    public void setQuestion(String question) { this.question = question; } 
    public String getAnswer() { return answer; } 
    public void setAnswer(String answer) { this.answer = answer; } 
    public String getDifficulty() { return difficulty; } 
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; } 
    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; };
}
