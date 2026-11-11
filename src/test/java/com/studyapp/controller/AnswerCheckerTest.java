package com.studyapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for {@link AnswerChecker}: they pin down what the
 * checker does today.
 *
 * Tests tagged {@code expected-to-flip} assert CURRENT BUGGY behaviour (false
 * positives, audit #4). They are fixed in Night 6 (B10): when that change lands,
 * flip the expected value of each of them to the correct verdict.
 */
class AnswerCheckerTest {

    private final AnswerChecker checker = new AnswerChecker();

    // ---- Correct behaviour that must be preserved ----

    @Test
    void identicalAnswerIsCorrect() {
        assertEquals("CORRECT", checker.check("Paris", "Paris"));
    }

    @Test
    void caseDifferenceIsIgnored() {
        assertEquals("CORRECT", checker.check("Paris", "paris"));
    }

    @Test
    void accentsAreIgnored() {
        assertEquals("CORRECT", checker.check("héllo", "hello"));
    }

    @Test
    void leadingStopwordIsIgnored() {
        assertEquals("CORRECT", checker.check("The mitochondria", "mitochondria"));
    }

    @Test
    void minorTypoIsAccepted() {
        assertEquals("CORRECT", checker.check("Paris", "Pariss"));
    }

    @Test
    void synonymIsAccepted() {
        assertEquals("CORRECT", checker.check("big", "large"));
        assertEquals("CORRECT", checker.check("car", "automobile"));
    }

    @Test
    void unrelatedAnswerIsIncorrect() {
        assertEquals("INCORRECT", checker.check("Paris", "London"));
        assertEquals("INCORRECT", checker.check("a", "b"));
        assertEquals("INCORRECT", checker.check("x", "y"));
    }

    @Test
    void antonymIsIncorrect() {
        assertEquals("INCORRECT", checker.check("hot", "cold"));
    }

    @Test
    void partialAnswerIsClose() {
        assertEquals("CLOSE", checker.check("photosynthesis", "photosynthesis converts light"));
    }

    @Test
    void emptyAnswerAgainstRealAnswerIsIncorrect() {
        assertEquals("INCORRECT", checker.check("abc", ""));
        assertEquals("INCORRECT", checker.check("", "abc"));
        assertEquals("INCORRECT", checker.check("  ", "abc"));
    }

    // ---- Known false positives: EXPECTED TO FLIP in Night 6 (B10) ----

    @Test
    @Tag("expected-to-flip")
    void notEqualsOperatorVersusEqualsOperatorIsWronglyCorrect() {
        // Should be INCORRECT after B10.
        assertEquals("CORRECT", checker.check("!=", "=="));
    }

    @Test
    @Tag("expected-to-flip")
    void plusVersusMinusIsWronglyCorrect() {
        // Should be INCORRECT after B10.
        assertEquals("CORRECT", checker.check("+", "-"));
    }

    @Test
    @Tag("expected-to-flip")
    void cPlusPlusVersusCIsWronglyCorrect() {
        // Should be INCORRECT after B10.
        assertEquals("CORRECT", checker.check("C++", "C"));
    }

    @Test
    @Tag("expected-to-flip")
    void negativeFiveVersusFiveIsWronglyCorrect() {
        // Should be INCORRECT after B10.
        assertEquals("CORRECT", checker.check("-5", "5"));
    }

    @Test
    @Tag("expected-to-flip")
    void symbolOnlyAnswersAreWronglyCorrect() {
        // Should be INCORRECT after B10 (both normalize to empty token lists).
        assertEquals("CORRECT", checker.check("?", "!"));
    }

    @Test
    @Tag("expected-to-flip")
    void twoEmptyInputsAreWronglyCorrect() {
        // Two empty token lists are currently "equivalent".
        assertEquals("CORRECT", checker.check("", ""));
        assertEquals("CORRECT", checker.check(null, null));
    }

    @Test
    @Tag("expected-to-flip")
    void stopwordOnlyExpectedAndMissingAnswerIsWronglyCorrect() {
        // "a" is a stopword so it has no tokens; a null answer then "matches". Should be INCORRECT.
        assertEquals("CORRECT", checker.check("a", null));
    }
}
