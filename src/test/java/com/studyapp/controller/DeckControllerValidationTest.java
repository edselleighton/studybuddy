package com.studyapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.studyapp.model.Deck;

/**
 * Deck-name validation, exercised through the in-memory paths of
 * {@link DeckController} (no database access, no MainController needed).
 */
class DeckControllerValidationTest {

    private final DeckController controller = new DeckController(null);

    private String messageOf(String name) {
        return assertThrows(CustomException.class, () -> controller.createDeck(name, "d")).getMessage();
    }

    @Test
    void validDeckIsCreated() throws Exception {
        Deck d = controller.createDeck("Biology", "desc");
        assertEquals("Biology", d.getName());
        assertEquals(1, controller.allDecks().size());
    }

    @Test
    void nullNameIsRejected() {
        assertEquals("Name cannot be empty.", messageOf(null));
    }

    @Test
    void emptyAndBlankNamesAreRejected() {
        assertEquals("Name cannot be empty.", messageOf(""));
        assertEquals("Name cannot be empty.", messageOf("   "));
    }

    @Test
    void nameOfExactly100CharactersIsAccepted() throws Exception {
        controller.createDeck("x".repeat(100), "d");
        assertEquals(1, controller.allDecks().size());
    }

    @Test
    void nameOver100CharactersIsRejected() {
        assertEquals("Name cannot exceed 100 characters.", messageOf("x".repeat(101)));
    }

    @Test
    void exactDuplicateNameIsRejected() throws Exception {
        controller.createDeck("Biology", "d");
        assertEquals("Deck name already exists.", messageOf("Biology"));
    }

    @Test
    void renamingADeckToItsOwnNameIsAllowed() throws Exception {
        Deck d = controller.createDeck("Biology", "d");
        d.setDescription("changed");
        controller.updateDeck(d);
        assertEquals("changed", controller.findDeck(d.getDeckID()).getDescription());
    }

    @Test
    void updateToAnotherDecksNameIsRejected() throws Exception {
        controller.createDeck("Biology", "d");
        Deck other = controller.createDeck("Chemistry", "d");
        Deck renamed = new Deck(other.getDeckID(), "Biology", "d", LocalDateTime.now());
        assertThrows(CustomException.class, () -> controller.updateDeck(renamed));
    }

    @Test
    void updatingUnknownDeckIsRejected() {
        Deck ghost = new Deck(12345, "Ghost", "d", LocalDateTime.now());
        assertEquals("Deck not found.",
                assertThrows(CustomException.class, () -> controller.updateDeck(ghost)).getMessage());
    }

    @Test
    @Tag("expected-to-flip")
    void caseDifferentNameIsWronglyAccepted() throws Exception {
        // Known bug (audit #3): MySQL's unique key is case-insensitive, so this later stalls saves.
        // Night 4 (B4) should reject it as a duplicate.
        controller.createDeck("Biology", "d");
        controller.createDeck("biology", "d");
        assertEquals(2, controller.allDecks().size());
    }

    @Test
    @Tag("expected-to-flip")
    void paddedNameIsWronglyTreatedAsDistinct() throws Exception {
        // Known bug (audit #3): trailing whitespace is not trimmed before the duplicate check.
        // Night 4 (B4) should reject it as a duplicate.
        controller.createDeck("Biology", "d");
        controller.createDeck("Biology ", "d");
        assertEquals(2, controller.allDecks().size());
    }
}
