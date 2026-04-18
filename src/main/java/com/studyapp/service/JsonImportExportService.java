package com.studyapp.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class JsonImportExportService {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Imports decks and cards from a JSON file into the application via the controller.
     * Supports both single-deck format { "deck_name":..., "cards":[...] }
     * and multi-deck format { "decks": [...] }.
     * Skips decks whose name already exists in the system.
     * Persists all changes to the database before returning.
     *
     * @return number of new decks actually imported
     */
    public int importFromFile(File file, MainController mc) throws CustomException {
        List<DeckJson> deckJsonList = parseFile(file);
        int imported = 0;

        for (DeckJson deckJson : deckJsonList) {
            String rawName = deckJson.getDeckName();
            if (rawName == null || rawName.isBlank()) continue;
            String deckName = rawName.trim();

            // Skip if name already exists
            boolean exists = mc.allDecks().stream()
                    .anyMatch(d -> d.getName().equalsIgnoreCase(deckName));
            if (exists) continue;

            String desc = deckJson.getDescription() == null ? "" : deckJson.getDescription().trim();
            mc.createDeck(deckName, desc);

            // Retrieve the newly created deck by name to get its assigned in-memory ID
            Deck newDeck = mc.allDecks().stream()
                    .filter(d -> d.getName().equalsIgnoreCase(deckName))
                    .findFirst()
                    .orElse(null);
            if (newDeck == null) continue;

            List<CardJson> cards = deckJson.getCards();
            if (cards != null) {
                for (CardJson c : cards) {
                    if (c.getQuestion() == null || c.getQuestion().isBlank()) continue;
                    if (c.getAnswer()   == null || c.getAnswer().isBlank())   continue;
                    String difficulty = normaliseDifficulty(c.getDifficulty());
                    mc.createFlashcard(newDeck.getDeckID(),
                            c.getQuestion().trim(),
                            c.getAnswer().trim(),
                            difficulty);
                }
            }
            imported++;
        }

        mc.saveChanges();
        return imported;
    }

    /**
     * Exports a single deck and its cards to a JSON file using the single-deck format.
     */
    public void exportDeckToFile(Deck deck, List<Flashcard> cards, File file) throws CustomException {
        List<CardJson> cardJsonList = new ArrayList<>();
        for (Flashcard card : cards) {
            cardJsonList.add(new CardJson(
                    card.getQuestion(),
                    card.getAnswer(),
                    card.getDifficulty()));
        }
        DeckJson deckJson = new DeckJson(
                deck.getName(),
                deck.getDescription(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                cardJsonList);

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(deckJson, writer);
        } catch (IOException e) {
            throw new CustomException("Failed to write export file: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private List<DeckJson> parseFile(File file) throws CustomException {
        try (FileReader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                throw new CustomException("Invalid JSON: root must be an object.");
            }
            JsonObject obj = root.getAsJsonObject();

            // Multi-deck format: { "decks": [...] }
            if (obj.has("decks")) {
                DeckJson[] arr = GSON.fromJson(obj.get("decks"), DeckJson[].class);
                return Arrays.asList(arr);
            }

            // Single-deck format: { "deck_name": ..., "cards": [...] }
            return List.of(GSON.fromJson(obj, DeckJson.class));

        } catch (IOException e) {
            throw new CustomException("Could not read file: " + e.getMessage());
        }
    }

    /** Normalises any case variant to the exact ENUM value; falls back to "Medium". */
    private String normaliseDifficulty(String raw) {
        if (raw == null || raw.isBlank()) return "Medium";
        String t = raw.trim();
        if (t.equalsIgnoreCase("Easy"))   return "Easy";
        if (t.equalsIgnoreCase("Medium")) return "Medium";
        if (t.equalsIgnoreCase("Hard"))   return "Hard";
        return "Medium";
    }
}