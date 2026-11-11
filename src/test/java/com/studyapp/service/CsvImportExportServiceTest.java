package com.studyapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.studyapp.controller.CustomException;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

class CsvImportExportServiceTest {

    private final CsvImportExportService service = new CsvImportExportService();

    @TempDir
    Path tmp;

    private File write(String name, String content) throws IOException {
        Path p = tmp.resolve(name);
        Files.writeString(p, content, StandardCharsets.UTF_8);
        return p.toFile();
    }

    private static Flashcard card(String q, String a, String d) {
        return new Flashcard(0, 1, q, a, d, LocalDateTime.now());
    }

    private static Deck deck() {
        return new Deck(1, "Deck", "desc", LocalDateTime.now());
    }

    // ---- Export ----

    @Test
    void exportWritesHeaderAndQuotedRows() throws Exception {
        File out = tmp.resolve("out.csv").toFile();
        service.exportDeckToFile(deck(), List.of(card("Q1", "A1", "Easy")), out);
        String text = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertEquals("question,answer,difficulty\n\"Q1\",\"A1\",\"Easy\"\n", text);
    }

    @Test
    void exportOfEmptyDeckWritesOnlyHeader() throws Exception {
        File out = tmp.resolve("empty.csv").toFile();
        service.exportDeckToFile(deck(), List.of(), out);
        assertEquals("question,answer,difficulty\n", Files.readString(out.toPath(), StandardCharsets.UTF_8));
    }

    @Test
    void exportGuardsAgainstFormulaInjection() throws Exception {
        File out = tmp.resolve("f.csv").toFile();
        service.exportDeckToFile(deck(), List.of(card("=1+1", "@cmd", "Easy")), out);
        String text = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertTrue(text.contains("\"'=1+1\""));
        assertTrue(text.contains("\"'@cmd\""));
    }

    @Test
    void exportToUnwritablePathThrows() {
        File bad = tmp.resolve("no-such-dir").resolve("x.csv").toFile();
        assertThrows(CustomException.class, () -> service.exportDeckToFile(deck(), List.of(), bad));
    }

    // ---- Round trip ----

    @Test
    void roundTripPreservesQuotesCommasNewlinesAndUnicode() throws Exception {
        File out = tmp.resolve("rt.csv").toFile();
        List<Flashcard> cards = List.of(
                card("Say \"hi\", ok?", "line1\nline2", "Hard"),
                card("café 日本", "über", "Medium"));
        service.exportDeckToFile(deck(), cards, out);

        List<CardJson> back = service.previewCards(out);
        assertEquals(2, back.size());
        assertEquals("Say \"hi\", ok?", back.get(0).getQuestion());
        assertEquals("line1\nline2", back.get(0).getAnswer());
        assertEquals("Hard", back.get(0).getDifficulty());
        assertEquals("café 日本", back.get(1).getQuestion());
        assertEquals("über", back.get(1).getAnswer());
    }

    @Test
    @Tag("expected-to-flip")
    void roundTripKeepsFormulaGuardApostrophe() throws Exception {
        // Known bug (audit #19): the guard apostrophe is not stripped on import.
        // Night 5 (B8) should make this return "=1+1".
        File out = tmp.resolve("rt2.csv").toFile();
        service.exportDeckToFile(deck(), List.of(card("=1+1", "ok", "Easy")), out);
        assertEquals("'=1+1", service.previewCards(out).get(0).getQuestion());
    }

    // ---- Import parsing ----

    @Test
    void importAcceptsTwoColumnHeaderWithNullDifficulty() throws Exception {
        File f = write("two.csv", "question,answer\nQ,A\n");
        List<CardJson> cards = service.previewCards(f);
        assertEquals(1, cards.size());
        assertNull(cards.get(0).getDifficulty());
    }

    @Test
    void importHeaderIsCaseInsensitiveAndBomIsStripped() throws Exception {
        File f = write("bom.csv", "﻿Question,ANSWER,Difficulty\r\nQ,A,hard\r\n");
        List<CardJson> cards = service.previewCards(f);
        assertEquals(1, cards.size());
        assertEquals("Hard", cards.get(0).getDifficulty());
    }

    @Test
    void importNormalizesDifficultyAndDropsUnknownValues() throws Exception {
        File f = write("d.csv", "question,answer,difficulty\nA1,B1,EASY\nA2,B2,impossible\nA3,B3,\n");
        List<CardJson> cards = service.previewCards(f);
        assertEquals("Easy", cards.get(0).getDifficulty());
        assertNull(cards.get(1).getDifficulty());
        assertNull(cards.get(2).getDifficulty());
    }

    @Test
    void importSkipsBlankRowsAndRowsMissingQuestionOrAnswer() throws Exception {
        File f = write("skip.csv", "question,answer\n\nQ1,A1\n,A2\nQ3,\n  ,  \nQ4,A4\n");
        List<CardJson> cards = service.previewCards(f);
        assertEquals(2, cards.size());
        assertEquals("Q1", cards.get(0).getQuestion());
        assertEquals("Q4", cards.get(1).getQuestion());
    }

    @Test
    void importTrimsFields() throws Exception {
        File f = write("trim.csv", "question,answer\n  Q  ,  A  \n");
        CardJson c = service.previewCards(f).get(0);
        assertEquals("Q", c.getQuestion());
        assertEquals("A", c.getAnswer());
    }

    @Test
    void importLeavesBlankLinesBeforeHeaderAlone() throws Exception {
        File f = write("lead.csv", "\n\nquestion,answer\nQ,A\n");
        assertEquals(1, service.previewCards(f).size());
    }

    @Test
    void importWithoutTrailingNewlineKeepsLastRow() throws Exception {
        File f = write("nonl.csv", "question,answer\nQ,A");
        assertEquals(1, service.previewCards(f).size());
    }

    @Test
    void importEmptyFileThrows() throws Exception {
        File f = write("empty.csv", "");
        assertThrows(CustomException.class, () -> service.previewCards(f));
    }

    @Test
    void importBlankOnlyFileThrows() throws Exception {
        File f = write("blank.csv", "\n  \n");
        assertThrows(CustomException.class, () -> service.previewCards(f));
    }

    @Test
    void importWrongHeaderThrows() throws Exception {
        File f = write("hdr.csv", "front,back\nQ,A\n");
        CustomException e = assertThrows(CustomException.class, () -> service.previewCards(f));
        assertTrue(e.getMessage().contains("Invalid CSV header"));
    }

    @Test
    void importUnmatchedQuoteThrows() throws Exception {
        File f = write("quote.csv", "question,answer\n\"Q,A\n");
        CustomException e = assertThrows(CustomException.class, () -> service.previewCards(f));
        assertTrue(e.getMessage().contains("unmatched quotes"));
    }

    @Test
    void importMissingFileThrows() {
        File f = tmp.resolve("missing.csv").toFile();
        assertThrows(CustomException.class, () -> service.previewCards(f));
    }

    @Test
    @Tag("expected-to-flip")
    void importAcceptsRowsWithTooManyColumns() throws Exception {
        // Known gap: rows with the wrong column count are silently accepted.
        // Night 5 (B8) should reject them.
        File f = write("wide.csv", "question,answer\nQ,A,extra,more\n");
        assertEquals(1, service.previewCards(f).size());
    }

    @Test
    @Tag("expected-to-flip")
    void importSilentlySkipsRowsWithTooFewColumns() throws Exception {
        // Known gap: a row with only one column is silently skipped.
        // Night 5 (B8) should reject it with an error.
        File f = write("narrow.csv", "question,answer\nOnlyQuestion\nQ,A\n");
        assertEquals(1, service.previewCards(f).size());
    }
}
