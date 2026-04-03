package com.studyapp.model;

import java.time.LocalDateTime;

public class Deck {
    private int deckID;
    private String name;
    private String description;
    LocalDateTime createdAt;

    public Deck(){}

    public Deck(int deckID, String name, String description, LocalDateTime createdAt) {
        this.deckID = deckID;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getDeckID() { return deckID; }
    public void setDeckID(int deckID) { this.deckID = deckID; }
    public String getName() { return name; } 
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } 
    public void setDescription(String description) { this.description = description; } 
    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
}
