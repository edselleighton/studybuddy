package com.studyapp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.studyapp.controller.CustomException;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class CsvImportExportService {

    private static final String HEADER = "deck_name,description,question,answer,difficulty";

    //export pa la anay -------------------------------------------------------------
    public void exportDeckToFile(Deck deck, List<Flashcard> cards, File file) throws CustomException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
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
            throw new CustomException("Failed to write CSV export file.");
        }
    }

    private String sanitizeFormulaInjection(String value) {
        if (value == null) return "";

        // Strip null bytes (\0) — some parsers silently drop them, causing \0=formula
        // to land as =formula at the start of a cell.
        value = value.replace("\0", "");

        // Strip leading whitespace including Unicode variants that stripLeading() misses:
        // non-breaking space \u00A0, zero-width space \u200B, and BOM \uFEFF.
        int start = 0;
        while (start < value.length() && (Character.isWhitespace(value.charAt(start))
                || value.charAt(start) == '\u00A0'
                || value.charAt(start) == '\u200B'
                || value.charAt(start) == '\uFEFF')) {
            start++;
        }
        String trimmed = value.substring(start);

        if (trimmed.isEmpty()) return value;
        char first = trimmed.charAt(0);

        // Prefix the original value if it starts with a formula trigger character.
        // Covers = + - @ (formula triggers) and \t \r (whitespace bypass vectors).
        if (first == '=' || first == '+' || first == '-' || first == '@'
                || first == '\t' || first == '\r') {
            value = "'" + value;
        }

        // Sanitize embedded newlines: a formula trigger at the start of any line
        // embedded within the value would be evaluated as a new row's formula by
        // lenient spreadsheet parsers that split on newlines before respecting quotes.
        value = value.replaceAll("(\r\n|\r|\n)([=+\\-@\t\r])", "$1'$2");

        return value;
    }

    /** Wraps a value in quotes and escapes internal quotes by doubling them (RFC 4180). */
    private String escapeCsv(String value) {
        value = sanitizeFormulaInjection(value == null ? "" : value);
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
    

    //import soon -----------------------------------------------------------------
}
