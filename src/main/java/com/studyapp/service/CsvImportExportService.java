package com.studyapp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.studyapp.controller.CustomException;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class CsvImportExportService {

    private static final String HEADER = "deck_name,description,question,answer,difficulty";

    //export
    public void exportDeckToFile(Deck deck, List<Flashcard> cards, File file) throws CustomException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(HEADER + "\n");
            if (cards == null || cards.isEmpty()) {
                writer.write(buildRow(
                        deck.getName(),
                        deck.getDescription(),
                        "",
                        "",
                        ""
                ) + "\n");
                return;
            }

            for (Flashcard card : cards) {
                writer.write(buildRow(
                        deck.getName(),
                        deck.getDescription(),
                        card.getQuestion(),
                        card.getAnswer(),
                        card.getDifficulty()
                ) + "\n");
            }
        } catch (IOException e) {
            throw new CustomException("Failed to write CSV export file: " + e.getMessage());
        }
    }

    /** Wraps a value in quotes and escapes internal quotes by doubling them (RFC 4180). */
    private String escapeCsv(String value) {
        if (value == null) value = "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String buildRow(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeCsv(values[i]));
        }
        return sb.toString();
    }
}
