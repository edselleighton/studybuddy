package com.studyapp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.studyapp.controller.CustomException;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

public class CsvImportExportService {

    // The expected header row written at the top of every exported CSV
    private static final String HEADER_WITH_DIFF = "question,answer,difficulty";
    private static final String HEADER_NO_DIFF   = "question,answer";

    // Accepted column sets for imported files
    private static final List<String> HEADERS_3 = List.of("question", "answer", "difficulty");
    private static final List<String> HEADERS_2 = List.of("question", "answer");

    /**
     * Exports a single deck and its flashcards to a CSV file.
     */
    
    public void exportDeckToFile(Deck deck, List<Flashcard> cards, File file) throws CustomException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_WITH_DIFF + "\n");

            if (cards == null || cards.isEmpty()) return;

            for (Flashcard card : cards) {
                writer.write(buildRow(
                        card.getQuestion(),
                        card.getAnswer(),
                        card.getDifficulty()
                ) + "\n");
            }
        } catch (IOException e) {
            throw new CustomException("Failed to write CSV export file.");
        }
    }

    /**
     * Reads a CSV file and splits it into a list of rows (each row is a list of
     * field strings). Handles RFC 4180 quoting, BOM stripping, and CRLF pairs.
     * Throws CustomException for unmatched quotes or unreadable files.
     */
    private List<List<String>> parseCsv(File file) throws CustomException {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);

            // Strip UTF-8 BOM that Excel and some editors prepend
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
                            // Escaped quote ("") — append a single literal quote
                            currentField.append('"');
                            i++;
                        } else {
                            // Closing quote — exit quoted mode
                            inQuotes = false;
                        }
                    } else {
                        currentField.append(ch);
                    }
                    continue;
                }

                if (ch == '"') {
                    // Opening quote — enter quoted mode
                    inQuotes = true;
                } else if (ch == ',') {
                    // Comma outside quotes — field boundary
                    currentRow.add(currentField.toString());
                    currentField.setLength(0);
                } else if (ch == '\r' || ch == '\n') {
                    // Line ending outside quotes — row boundary
                    currentRow.add(currentField.toString());
                    currentField.setLength(0);
                    rows.add(currentRow);
                    currentRow = new ArrayList<>();

                    // Consume the \n of a CRLF pair so it isn't treated as a second row
                    if (ch == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                        i++;
                    }
                } else {
                    currentField.append(ch);
                }
            }

            // A file that ends without a trailing newline still has a quote open — error
            if (inQuotes) {
                throw new CustomException("Malformed CSV file: unmatched quotes.");
            }

            // Flush any remaining content as the final row
            if (currentField.length() > 0 || !currentRow.isEmpty()) {
                currentRow.add(currentField.toString());
                rows.add(currentRow);
            }

            return rows;
        } catch (IOException e) {
            throw new CustomException("Could not read file: " + e.getMessage());
        }
    }

    /** Returns true if every cell in the row is null or whitespace-only. */
    private boolean isBlankRow(List<String> row) {
        return row.stream().allMatch(value -> value == null || value.isBlank());
    }

    /** Trims and lowercases a header cell for case-insensitive comparison. */
    private String normalizeHeader(String value) {
        return safeTrim(value).toLowerCase(Locale.ROOT);
    }

    /** Null-safe trim — returns an empty string instead of throwing on null. */
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Guards against CSV formula injection by prepending a single quote to values
     * that start with a formula trigger character (=, +, -, @, tab, CR).
     * Also neutralises triggers after embedded newlines.
     */
    private String sanitizeFormulaInjection(String value) {
        if (value == null) return "";

        // Remove null bytes that could confuse parsers
        value = value.replace("\0", "");

        // Skip leading whitespace variants to find the effective first character
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

        // Prepend a single quote if the value would be interpreted as a formula
        if (first == '=' || first == '+' || first == '-' || first == '@'
                || first == '\t' || first == '\r') {
            value = "'" + value;
        }

        // Also neutralise formula triggers that appear at the start of embedded lines
        value = value.replaceAll("(\r\n|\r|\n)([=+\\-@\t\r])", "$1'$2");

        return value;
    }

    /**
     * Wraps a single field value in double-quotes for CSV output.
     * Any internal double-quotes are escaped by doubling them ("").
     * Formula injection is sanitised before quoting.
     */
    private String escapeCsv(String value) {
        value = sanitizeFormulaInjection(value == null ? "" : value);
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /**
     * Builds a complete CSV row string from an arbitrary number of field values.
     * Each field is individually escaped and joined with commas.
     */
    private String buildRow(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeCsv(values[i]));
        }
        return sb.toString();
    }



        /**
     * Parses a CSV file and returns a flat list of all card candidates for UI preview.
     *
     * @param  file the CSV file to read
     * @return flat list of card DTOs; never null
     * @throws CustomException if the file is empty, unreadable, or has an unrecognised header
     */
    
    
    /**
     * Parses a CSV file and returns a flat list of card candidates for UI preview.
     * Accepted headers: {@code question,answer} or {@code question,answer,difficulty}.
     * Difficulty is normalised to "Easy", "Medium", or "Hard" if recognised; null otherwise.
     */
    public List<CardJson> previewCards(File file) throws CustomException {
        List<List<String>> rows = parseCsv(file);
        if (rows.isEmpty()) throw new CustomException("CSV file is empty.");

        int headerIdx = 0;
        while (headerIdx < rows.size() && isBlankRow(rows.get(headerIdx))) headerIdx++;
        if (headerIdx >= rows.size()) throw new CustomException("CSV file contains no data.");

        List<String> header = rows.get(headerIdx).stream()
                .map(this::normalizeHeader)
                .collect(java.util.stream.Collectors.toList());

        boolean hasDiff;
        if (header.equals(HEADERS_3)) {
            hasDiff = true;
        } else if (header.equals(HEADERS_2)) {
            hasDiff = false;
        } else {
            throw new CustomException(
                "Invalid CSV header. Expected \"" + HEADER_WITH_DIFF
                + "\" or \"" + HEADER_NO_DIFF + "\".");
        }

        List<CardJson> result = new ArrayList<>();
        for (int i = headerIdx + 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (isBlankRow(row) || row.size() < 2) continue;
            String question = safeTrim(row.get(0));
            String answer   = safeTrim(row.get(1));
            if (question.isBlank() || answer.isBlank()) continue;
            String diff = (hasDiff && row.size() > 2) ? safeTrim(row.get(2)) : null;
            result.add(new CardJson(question, answer, previewDifficulty(diff)));
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
