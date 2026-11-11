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

class JsonImportExportServiceTest {

    private final JsonImportExportService service = new JsonImportExportService();

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

    // ---- Export / round trip ----

    @Test
    void exportWritesFlatCardArray() throws Exception {
        File out = tmp.resolve("out.json").toFile();
        service.exportDeckToFile(deck(), List.of(card("Q", "A", "Easy")), out);
        String text = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertTrue(text.trim().startsWith("["));
        assertTrue(text.contains("\"question\": \"Q\""));
        assertTrue(text.contains("\"difficulty\": \"Easy\""));
    }

    @Test
    void roundTripPreservesTextIncludingUnicode() throws Exception {
        File out = tmp.resolve("rt.json").toFile();
        service.exportDeckToFile(deck(), List.of(
                card("Say \"hi\"\nnext", "café 日本", "Hard"),
                card("=1+1", "plain", "Medium")), out);

        List<CardJson> back = service.previewCards(out);
        assertEquals(2, back.size());
        assertEquals("Say \"hi\"\nnext", back.get(0).getQuestion());
        assertEquals("café 日本", back.get(0).getAnswer());
        assertEquals("Hard", back.get(0).getDifficulty());
        // JSON needs no formula guard
        assertEquals("=1+1", back.get(1).getQuestion());
    }

    @Test
    void exportToUnwritablePathThrows() {
        File bad = tmp.resolve("no-such-dir").resolve("x.json").toFile();
        assertThrows(CustomException.class, () -> service.exportDeckToFile(deck(), List.of(), bad));
    }

    // ---- Import shapes ----

    @Test
    void importAcceptsCardArray() throws Exception {
        File f = write("arr.json", "[{\"question\":\"Q\",\"answer\":\"A\",\"difficulty\":\"easy\"}]");
        ImportPreview p = service.previewImport(f);
        assertEquals(1, p.getCards().size());
        assertEquals("Easy", p.getCards().get(0).getDifficulty());
        assertNull(p.getDeckName());
    }

    @Test
    void importAcceptsDeckObjectWithMetadata() throws Exception {
        File f = write("deck.json",
                "{\"deck_name\":\"Bio\",\"description\":\"d\",\"exported_at\":\"2026-01-01\","
              + "\"cards\":[{\"question\":\"Q\",\"answer\":\"A\"}]}");
        ImportPreview p = service.previewImport(f);
        assertEquals("Bio", p.getDeckName());
        assertEquals("d", p.getDescription());
        assertEquals("2026-01-01", p.getExportedAt());
        assertEquals(1, p.getCards().size());
        assertNull(p.getCards().get(0).getDifficulty());
    }

    @Test
    void importSkipsCardsMissingQuestionOrAnswerAndNulls() throws Exception {
        File f = write("skip.json",
                "[null,{\"question\":\"\",\"answer\":\"A\"},{\"question\":\"Q\"},"
              + "{\"question\":\"  Q2 \",\"answer\":\" A2 \",\"difficulty\":\"bogus\"}]");
        List<CardJson> cards = service.previewCards(f);
        assertEquals(1, cards.size());
        assertEquals("Q2", cards.get(0).getQuestion());
        assertEquals("A2", cards.get(0).getAnswer());
        assertNull(cards.get(0).getDifficulty());
    }

    @Test
    void importEmptyArrayGivesNoCards() throws Exception {
        assertTrue(service.previewCards(write("e.json", "[]")).isEmpty());
    }

    @Test
    void importObjectWithoutCardsArrayThrows() throws Exception {
        File f = write("bad.json", "{\"deck_name\":\"X\"}");
        assertThrows(CustomException.class, () -> service.previewImport(f));
    }

    @Test
    void importScalarRootThrows() throws Exception {
        File f = write("scalar.json", "42");
        assertThrows(CustomException.class, () -> service.previewImport(f));
    }

    @Test
    void importMalformedJsonThrows() throws Exception {
        File f = write("mal.json", "[{\"question\": ");
        assertThrows(CustomException.class, () -> service.previewImport(f));
    }

    @Test
    void importMissingFileThrows() {
        File f = tmp.resolve("missing.json").toFile();
        assertThrows(CustomException.class, () -> service.previewImport(f));
    }

    @Test
    @Tag("expected-to-flip")
    void importRejectsMultiDeckFileWithGenericMessage() throws Exception {
        // sample-data2.json style {"decks":[...]} is not supported yet; it is rejected
        // with the generic message. Night 5 (B8) should support it or reject it clearly.
        File f = write("multi.json", "{\"decks\":[{\"deck_name\":\"A\",\"cards\":[]}]}");
        CustomException e = assertThrows(CustomException.class, () -> service.previewImport(f));
        assertTrue(e.getMessage().startsWith("Invalid JSON: expected a card array"));
    }
}
