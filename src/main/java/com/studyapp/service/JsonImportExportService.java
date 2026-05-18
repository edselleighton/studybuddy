package com.studyapp.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.studyapp.controller.CustomException;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class JsonImportExportService {

    // Shared Gson instance configured for pretty-printed output
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Exports a deck's flashcards to a JSON file as a flat card array.
     * Each entry has {@code question}, {@code answer}, and {@code difficulty} fields.
     */
    public void exportDeckToFile(Deck deck, List<Flashcard> cards, File file) throws CustomException {
        List<CardJson> cardJsonList = new ArrayList<>();
        for (Flashcard card : cards) {
            cardJsonList.add(new CardJson(
                    card.getQuestion(),
                    card.getAnswer(),
                    card.getDifficulty()));
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(cardJsonList, writer);
        } catch (IOException e) {
            throw new CustomException("Failed to write export file: " + e.getMessage());
        }
    }

    /**
     * Parses a JSON file and returns a flat list of card candidates for UI preview.
     * Accepts a JSON array of card objects: {@code [{question, answer, difficulty}, ...]}.
     * Difficulty is normalised to "Easy", "Medium", or "Hard" if recognised; null otherwise.
     */
    public List<CardJson> previewCards(File file) throws CustomException {
        List<CardJson> result = new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                throw new CustomException("Invalid JSON: expected a flat card array.");
            }
            CardJson[] arr = GSON.fromJson(root, CardJson[].class);
            for (CardJson c : arr) {
                if (c.getQuestion() == null || c.getQuestion().isBlank()) continue;
                if (c.getAnswer()   == null || c.getAnswer().isBlank())   continue;
                result.add(new CardJson(
                    c.getQuestion().trim(),
                    c.getAnswer().trim(),
                    previewDifficulty(c.getDifficulty())
                ));
            }
        } catch (JsonParseException e) {
            throw new CustomException("Malformed JSON file: " + e.getMessage());
        } catch (IOException e) {
            throw new CustomException("Could not read file: " + e.getMessage());
        }
        return result;
    }

    /** Returns "Easy", "Medium", or "Hard" if recognised; null otherwise. */
    private String previewDifficulty(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String t = raw.trim();
        if (t.equalsIgnoreCase("Easy"))   return "Easy";
        if (t.equalsIgnoreCase("Medium")) return "Medium";
        if (t.equalsIgnoreCase("Hard"))   return "Hard";
        return null;
    }
}
