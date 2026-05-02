package com.studyapp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class CsvImportExportService {

    private static final String HEADER = "deck_name,description,question,answer,difficulty";
    private static final List<String> EXPECTED_HEADERS = List.of(
            "deck_name",
            "description",
            "question",
            "answer",
            "difficulty"
    );

    public int importFromFile(File file, MainController mc) throws CustomException {
        List<List<String>> rows = parseCsv(file);
        List<List<String>> dataRows = extractDataRows(rows);

        Map<String, CsvDeckData> deckRows = new LinkedHashMap<>();
        for (List<String> row : dataRows) {
            String rawName = row.get(0);
            if (rawName == null || rawName.isBlank()) {
                continue;
            }

            String deckName = rawName.trim();
            String description = safeTrim(row.get(1));
            String question = safeTrim(row.get(2));
            String answer = safeTrim(row.get(3));
            String difficulty = normaliseDifficulty(row.get(4));

            String deckKey = deckName.toLowerCase(Locale.ROOT);
            CsvDeckData deckData = deckRows.get(deckKey);
            if (deckData == null) {
                deckData = new CsvDeckData(deckName, description);
                deckRows.put(deckKey, deckData);
            } else if (!description.isBlank()
                    && !deckData.description().isBlank()
                    && !deckData.description().equals(description)) {
                throw new CustomException("CSV contains conflicting descriptions for deck '" + deckName + "'.");
            } else if (deckData.description().isBlank() && !description.isBlank()) {
                deckData = new CsvDeckData(deckData.deckName(), description, deckData.cards());
                deckRows.put(deckKey, deckData);
            }

            if (question.isBlank() || answer.isBlank()) {
                continue;
            }

            deckRows.get(deckKey).cards().add(new CardJson(question, answer, difficulty));
        }

        int imported = 0;

        for (CsvDeckData deckData : deckRows.values()) {
            boolean exists = mc.allDecks().stream()
                    .anyMatch(d -> d.getName().equalsIgnoreCase(deckData.deckName()));
            if (exists) {
                continue;
            }

            Deck newDeck = mc.createDeck(deckData.deckName(), deckData.description());

            for (CardJson card : deckData.cards()) {
                mc.createFlashcard(
                        newDeck.getDeckID(),
                        card.getQuestion(),
                        card.getAnswer(),
                        card.getDifficulty()
                );
            }

            imported++;
        }

        return imported;
    }

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

    private List<List<String>> extractDataRows(List<List<String>> rows) throws CustomException {
        if (rows.isEmpty()) {
            throw new CustomException("CSV file is empty.");
        }

        int headerIndex = 0;
        while (headerIndex < rows.size() && isBlankRow(rows.get(headerIndex))) {
            headerIndex++;
        }
        if (headerIndex >= rows.size()) {
            throw new CustomException("CSV file is empty.");
        }

        List<String> header = rows.get(headerIndex);
        if (header.size() != EXPECTED_HEADERS.size()) {
            throw new CustomException("Invalid CSV header. Expected: " + HEADER);
        }

        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            if (!EXPECTED_HEADERS.get(i).equals(normalizeHeader(header.get(i)))) {
                throw new CustomException("Invalid CSV header. Expected: " + HEADER);
            }
        }

        List<List<String>> dataRows = new ArrayList<>();
        for (int i = headerIndex + 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (isBlankRow(row)) {
                continue;
            }
            if (row.size() != EXPECTED_HEADERS.size()) {
                throw new CustomException("Invalid CSV row format near line " + (i + 1) + ".");
            }
            dataRows.add(row);
        }
        return dataRows;
    }

    private List<List<String>> parseCsv(File file) throws CustomException {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }

            List<List<String>> rows = new ArrayList<>();
            List<String> currentRow = new ArrayList<>();
            StringBuilder currentField = new StringBuilder();
            boolean inQuotes = false;

            for (int i = 0; i < content.length(); i++) {
                char ch = content.charAt(i);

                if (inQuotes) {
                    if (ch == '"') {
                        if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
                            currentField.append('"');
                            i++;
                        } else {
                            inQuotes = false;
                        }
                    } else {
                        currentField.append(ch);
                    }
                    continue;
                }

                if (ch == '"') {
                    inQuotes = true;
                } else if (ch == ',') {
                    currentRow.add(currentField.toString());
                    currentField.setLength(0);
                } else if (ch == '\r' || ch == '\n') {
                    currentRow.add(currentField.toString());
                    currentField.setLength(0);
                    rows.add(currentRow);
                    currentRow = new ArrayList<>();

                    if (ch == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                        i++;
                    }
                } else {
                    currentField.append(ch);
                }
            }

            if (inQuotes) {
                throw new CustomException("Malformed CSV file: unmatched quotes.");
            }

            if (currentField.length() > 0 || !currentRow.isEmpty()) {
                currentRow.add(currentField.toString());
                rows.add(currentRow);
            }

            return rows;
        } catch (IOException e) {
            throw new CustomException("Could not read file: " + e.getMessage());
        }
    }

    private boolean isBlankRow(List<String> row) {
        return row.stream().allMatch(value -> value == null || value.isBlank());
    }

    private String normalizeHeader(String value) {
        return safeTrim(value).toLowerCase(Locale.ROOT);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String normaliseDifficulty(String raw) {
        if (raw == null || raw.isBlank()) return "Medium";
        String t = raw.trim();
        if (t.equalsIgnoreCase("Easy"))   return "Easy";
        if (t.equalsIgnoreCase("Medium")) return "Medium";
        if (t.equalsIgnoreCase("Hard"))   return "Hard";
        return "Medium";
    }

    private String sanitizeFormulaInjection(String value) {
        if (value == null) return "";

        value = value.replace("\0", "");

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

        if (first == '=' || first == '+' || first == '-' || first == '@'
                || first == '\t' || first == '\r') {
            value = "'" + value;
        }

        value = value.replaceAll("(\r\n|\r|\n)([=+\\-@\t\r])", "$1'$2");

        return value;
    }

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

    private record CsvDeckData(String deckName, String description, List<CardJson> cards) {
        private CsvDeckData(String deckName, String description) {
            this(deckName, description, new ArrayList<>());
        }
    }
}
