package com.studyapp.service;

import java.util.List;

public class DeckJson {
    // Field names match JSON keys exactly (Gson maps by field name)
    private String deck_name;
    private String description;
    private String exported_at;
    private List<CardJson> cards;

    public DeckJson() {}

    public DeckJson(String deck_name, String description, String exported_at, List<CardJson> cards) {
        this.deck_name    = deck_name;
        this.description  = description;
        this.exported_at  = exported_at;
        this.cards        = cards;
    }

    public String getDeckName()                 { return deck_name; }
    public void   setDeckName(String n)         { this.deck_name = n; }
    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d; }
    public String getExportedAt()               { return exported_at; }
    public void   setExportedAt(String t)       { this.exported_at = t; }
    public List<CardJson> getCards()            { return cards; }
    public void   setCards(List<CardJson> c)    { this.cards = c; }
}
