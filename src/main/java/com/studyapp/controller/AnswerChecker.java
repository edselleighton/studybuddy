package com.studyapp.controller;

import info.debatty.java.stringsimilarity.Cosine;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public final class AnswerChecker {

    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern PUNCTUATION = Pattern.compile("[\\p{Punct}]");
    private static final Pattern NON_ALNUM_SPACE = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]");
    private static final Pattern MULTISPACE = Pattern.compile("\\s+");

    private static final LevenshteinDistance LEVENSHTEIN = LevenshteinDistance.getDefaultInstance();
    private static final JaroWinklerSimilarity JARO_WINKLER = new JaroWinklerSimilarity();
    private static final Cosine BIGRAM_COSINE = new Cosine(2);
    private static final Cosine TRIGRAM_COSINE = new Cosine(3);
    private static final Set<POS> WORDNET_PARTS_OF_SPEECH = EnumSet.of(POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB);
    private static final Dictionary WORDNET_DICTIONARY = loadWordNetDictionary();
    private static final ConcurrentMap<String, Boolean> SYNONYM_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Boolean> ANTONYM_CACHE = new ConcurrentHashMap<>();

    private static final Set<String> STOPWORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has",
            "he", "in", "is", "it", "its", "of", "on", "or", "that", "the", "their",
            "there", "this", "to", "was", "were", "will", "with", "your", "what",
            "which", "who", "whom", "whose", "when", "where", "why", "how", "into",
            "onto", "than", "then", "also", "about", "across", "through", "using"
    );

    private static final Map<String, String> SYNONYM_NORMALIZATION = Map.ofEntries(
            Map.entry("kids", "child"),
            Map.entry("kid", "child"),
            Map.entry("children", "child"),
            Map.entry("child", "child"),
            Map.entry("car", "automobile"),
            Map.entry("cars", "automobile"),
            Map.entry("automobile", "automobile"),
            Map.entry("tv", "television"),
            Map.entry("tvs", "television"),
            Map.entry("television", "television"),
            Map.entry("phone", "telephone"),
            Map.entry("phones", "telephone"),
            Map.entry("telephone", "telephone")
    );

    public AnswerChecker() {
    }

    public String check(String expected, String actual) {
        String safeExpected = expected == null ? "" : expected;
        String safeActual = actual == null ? "" : actual;

        String expectedForCharacters = normalizeForCharacters(safeExpected);
        String actualForCharacters = normalizeForCharacters(safeActual);
        List<String> expectedTokens = tokenizeKeywords(safeExpected);
        List<String> actualTokens = tokenizeKeywords(safeActual);
        String expectedCanonical = String.join(" ", expectedTokens);
        String actualCanonical = String.join(" ", actualTokens);

        double characterScore = characterScore(
                expectedForCharacters,
                actualForCharacters,
                expectedCanonical,
                actualCanonical
        );
        double keywordScore = keywordScore(expectedTokens, actualTokens);
        double ngramScore = ngramScore(expectedCanonical, actualCanonical);
        double contradictionPenalty = contradictionPenalty(expectedTokens, actualTokens);
        boolean semanticEquivalent = areEquivalentPhrases(expectedTokens, actualTokens);

        if (semanticEquivalent) {
            characterScore = Math.max(characterScore, 0.90);
            ngramScore = Math.max(ngramScore, 0.90);
        }

        double[] weights = weightsFor(Math.max(expectedTokens.size(), actualTokens.size()),
                Math.max(expectedForCharacters.length(), actualForCharacters.length()));

        double finalScore = clamp(
                (characterScore * weights[0]) +
                        (keywordScore * weights[1]) +
                        (ngramScore * weights[2])
        );
        finalScore = clamp(finalScore - contradictionPenalty);

        return verdictFor(finalScore);
    }

    private static double characterScore(String expected, String actual,
                                         String expectedCanonical, String actualCanonical) {
        if (expected.isEmpty() && actual.isEmpty()) {
            return 1.0;
        }
        if (expected.isEmpty() || actual.isEmpty()) {
            return 0.0;
        }

        double rawScore = basicCharacterScore(expected, actual);
        if (expectedCanonical.isEmpty() || actualCanonical.isEmpty()) {
            return rawScore;
        }

        double canonicalScore = basicCharacterScore(expectedCanonical, actualCanonical);
        if (expectedCanonical.equals(actualCanonical) && !expected.equals(actual)) {
            return Math.max(rawScore, 0.90);
        }

        return Math.max(rawScore, canonicalScore * 0.92);
    }

    private static double basicCharacterScore(String expected, String actual) {
        int maxLength = Math.max(expected.length(), actual.length());
        int distance = LEVENSHTEIN.apply(expected, actual);
        double normalizedLevenshtein = 1.0 - ((double) distance / maxLength);
        double jaroWinkler = JARO_WINKLER.apply(expected, actual);
        return clamp((normalizedLevenshtein * 0.55) + (jaroWinkler * 0.45));
    }

    private static double keywordScore(List<String> expectedTokens, List<String> actualTokens) {
        if (expectedTokens.isEmpty() && actualTokens.isEmpty()) {
            return 1.0;
        }
        if (expectedTokens.isEmpty() || actualTokens.isEmpty()) {
            return 0.0;
        }

        boolean[] usedActual = new boolean[actualTokens.size()];
        double matchedExpectedWeight = 0.0;
        double matchedActualWeight = 0.0;
        double totalExpectedWeight = 0.0;
        double totalActualWeight = 0.0;

        for (String expectedToken : expectedTokens) {
            totalExpectedWeight += tokenImportance(expectedToken);
        }
        for (String actualToken : actualTokens) {
            totalActualWeight += tokenImportance(actualToken);
        }

        for (String expectedToken : expectedTokens) {
            int bestIndex = -1;
            double bestScore = 0.0;
            double expectedWeight = tokenImportance(expectedToken);

            for (int i = 0; i < actualTokens.size(); i++) {
                if (usedActual[i]) {
                    continue;
                }
                double candidate = tokenSimilarity(expectedToken, actualTokens.get(i));
                if (candidate > bestScore) {
                    bestScore = candidate;
                    bestIndex = i;
                }
            }

            if (bestIndex >= 0 && bestScore >= 0.82) {
                usedActual[bestIndex] = true;
                double actualWeight = tokenImportance(actualTokens.get(bestIndex));
                matchedExpectedWeight += expectedWeight * bestScore;
                matchedActualWeight += actualWeight * bestScore;
            }
        }

        if (totalExpectedWeight == 0.0 || totalActualWeight == 0.0) {
            return 0.0;
        }

        double precision = matchedActualWeight / totalActualWeight;
        double recall = matchedExpectedWeight / totalExpectedWeight;
        if (precision + recall == 0.0) {
            return 0.0;
        }

        double f1 = (2.0 * precision * recall) / (precision + recall);
        double conceptCoverage = recall;
        return clamp((f1 * 0.65) + (conceptCoverage * 0.35));
    }

    private static boolean areEquivalentPhrases(List<String> expectedTokens, List<String> actualTokens) {
        if (expectedTokens.size() != actualTokens.size()) {
            return false;
        }
        if (expectedTokens.isEmpty()) {
            return true;
        }

        boolean[] usedActual = new boolean[actualTokens.size()];
        boolean foundSemanticSubstitution = false;
        for (String expectedToken : expectedTokens) {
            int bestIndex = -1;
            double bestScore = 0.0;

            for (int i = 0; i < actualTokens.size(); i++) {
                if (usedActual[i]) {
                    continue;
                }
                double candidate = tokenSimilarity(expectedToken, actualTokens.get(i));
                if (candidate > bestScore) {
                    bestScore = candidate;
                    bestIndex = i;
                }
            }

            if (bestIndex < 0 || bestScore < 0.98) {
                return false;
            }
            if (!expectedToken.equals(actualTokens.get(bestIndex))) {
                foundSemanticSubstitution = true;
            }
            usedActual[bestIndex] = true;
        }

        return foundSemanticSubstitution;
    }

    private static double tokenSimilarity(String left, String right) {
        if (left.equals(right)) {
            return 1.0;
        }
        if (areSynonyms(left, right)) {
            return 1.0;
        }

        int maxLength = Math.max(left.length(), right.length());
        int distance = LEVENSHTEIN.apply(left, right);
        double normalizedLevenshtein = 1.0 - ((double) distance / maxLength);
        double jaroWinkler = JARO_WINKLER.apply(left, right);
        return clamp((normalizedLevenshtein * 0.45) + (jaroWinkler * 0.55));
    }

    private static double ngramScore(String expected, String actual) {
        if (expected.isEmpty() && actual.isEmpty()) {
            return 1.0;
        }
        if (expected.isEmpty() || actual.isEmpty()) {
            return 0.0;
        }

        double bigram = BIGRAM_COSINE.similarity(expected, actual);
        double trigram = TRIGRAM_COSINE.similarity(expected, actual);
        return clamp((bigram * 0.45) + (trigram * 0.55));
    }

    private static double[] weightsFor(int tokenCount, int characterLength) {
        if (tokenCount <= 2 || characterLength <= 12) {
            return new double[]{0.55, 0.25, 0.20};
        }
        if (tokenCount <= 5 || characterLength <= 32) {
            return new double[]{0.38, 0.34, 0.28};
        }
        return new double[]{0.24, 0.40, 0.36};
    }

    private static List<String> tokenizeKeywords(String text) {
        String cleaned = normalizeToWords(text);
        if (cleaned.isEmpty()) {
            return List.of();
        }

        String[] pieces = cleaned.split(" ");
        List<String> tokens = new ArrayList<>(pieces.length);
        for (String piece : pieces) {
            if (!piece.isBlank() && !STOPWORDS.contains(piece)) {
                tokens.add(normalizeToken(piece));
            }
        }
        return tokens;
    }

    private static String normalizeToken(String token) {
        String singular = token.endsWith("s") && token.length() > 3
                ? token.substring(0, token.length() - 1)
                : token;
        return SYNONYM_NORMALIZATION.getOrDefault(token,
                SYNONYM_NORMALIZATION.getOrDefault(singular, singular));
    }

    private static double contradictionPenalty(List<String> expectedTokens, List<String> actualTokens) {
        if (expectedTokens.isEmpty() || actualTokens.isEmpty()) {
            return 0.0;
        }

        double penalty = 0.0;
        for (String expectedToken : expectedTokens) {
            if (STOPWORDS.contains(expectedToken)) {
                continue;
            }

            String bestActualToken = null;
            double bestScore = -1.0;
            for (String actualToken : actualTokens) {
                double candidate = tokenSimilarity(expectedToken, actualToken);
                if (candidate > bestScore) {
                    bestScore = candidate;
                    bestActualToken = actualToken;
                }
            }

            if (bestActualToken == null || expectedToken.equals(bestActualToken)) {
                continue;
            }

            if (areAntonyms(expectedToken, bestActualToken)) {
                double importance = tokenImportance(expectedToken);
                double tokenPenalty = 0.18 + Math.min(0.18, (importance - 1.0) * 0.12);
                penalty += tokenPenalty;
            }
        }

        return Math.min(0.40, penalty);
    }

    private static double tokenImportance(String token) {
        if (token.isBlank()) {
            return 0.0;
        }
        if (STOPWORDS.contains(token)) {
            return 0.05;
        }

        double score = 1.0;
        int length = token.length();

        if (length >= 6) {
            score += 0.35;
        } else if (length >= 4) {
            score += 0.15;
        }

        if (WORDNET_DICTIONARY != null) {
            int senses = countWordNetSenses(token);
            if (senses == 0) {
                score += 0.15;
            } else if (senses <= 2) {
                score += 0.20;
            } else if (senses >= 8) {
                score -= 0.10;
            }
        }

        return Math.max(0.2, score);
    }

    private static int countWordNetSenses(String token) {
        int senses = 0;
        for (POS pos : WORDNET_PARTS_OF_SPEECH) {
            try {
                IndexWord indexWord = WORDNET_DICTIONARY.lookupIndexWord(pos, token);
                if (indexWord != null) {
                    senses += indexWord.getSenses().size();
                }
            } catch (JWNLException ignored) {
                return 0;
            }
        }
        return senses;
    }

    private static boolean areSynonyms(String left, String right) {
        if (left.length() < 3 || right.length() < 3 || WORDNET_DICTIONARY == null) {
            return false;
        }

        String cacheKey = left.compareTo(right) <= 0 ? left + "|" + right : right + "|" + left;
        return SYNONYM_CACHE.computeIfAbsent(cacheKey, ignored -> lookupSynonymRelationship(left, right));
    }

    private static boolean areAntonyms(String left, String right) {
        if (left.length() < 3 || right.length() < 3 || WORDNET_DICTIONARY == null) {
            return false;
        }

        String cacheKey = left.compareTo(right) <= 0 ? left + "|" + right : right + "|" + left;
        return ANTONYM_CACHE.computeIfAbsent(cacheKey, ignored -> lookupAntonymRelationship(left, right));
    }

    private static boolean lookupSynonymRelationship(String left, String right) {
        for (POS pos : WORDNET_PARTS_OF_SPEECH) {
            Set<String> leftLemmas = lookupLemmas(pos, left);
            Set<String> rightLemmas = lookupLemmas(pos, right);
            if (leftLemmas.isEmpty() || rightLemmas.isEmpty()) {
                continue;
            }

            if (!Collections.disjoint(collectSynonyms(pos, leftLemmas), rightLemmas)) {
                return true;
            }
            if (!Collections.disjoint(collectSynonyms(pos, rightLemmas), leftLemmas)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lookupAntonymRelationship(String left, String right) {
        for (POS pos : WORDNET_PARTS_OF_SPEECH) {
            Set<String> leftLemmas = lookupLemmas(pos, left);
            Set<String> rightLemmas = lookupLemmas(pos, right);
            if (leftLemmas.isEmpty() || rightLemmas.isEmpty()) {
                continue;
            }

            if (!Collections.disjoint(collectAntonyms(pos, leftLemmas), rightLemmas)) {
                return true;
            }
            if (!Collections.disjoint(collectAntonyms(pos, rightLemmas), leftLemmas)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> collectSynonyms(POS pos, Set<String> lemmas) {
        Set<String> synonyms = new HashSet<>();
        for (String lemma : lemmas) {
            try {
                IndexWord indexWord = WORDNET_DICTIONARY.lookupIndexWord(pos, lemma);
                if (indexWord == null) {
                    continue;
                }
                for (Synset sense : indexWord.getSenses()) {
                    for (Word word : sense.getWords()) {
                        synonyms.add(normalizeLemma(word.getLemma()));
                    }
                }
            } catch (JWNLException ignored) {
                return Set.of();
            }
        }
        return synonyms;
    }

    private static Set<String> collectAntonyms(POS pos, Set<String> lemmas) {
        Set<String> antonyms = new HashSet<>();
        for (String lemma : lemmas) {
            try {
                IndexWord indexWord = WORDNET_DICTIONARY.lookupIndexWord(pos, lemma);
                if (indexWord == null) {
                    continue;
                }
                for (Synset sense : indexWord.getSenses()) {
                    for (Word word : sense.getWords()) {
                        for (Pointer pointer : word.getPointers()) {
                            if (pointer.getType() != PointerType.ANTONYM) {
                                continue;
                            }
                            if (pointer.getTarget() instanceof Word targetWord) {
                                antonyms.add(normalizeLemma(targetWord.getLemma()));
                            }
                        }
                    }
                }
            } catch (JWNLException ignored) {
                return Set.of();
            }
        }
        return antonyms;
    }

    private static Set<String> lookupLemmas(POS pos, String token) {
        try {
            IndexWord direct = WORDNET_DICTIONARY.lookupIndexWord(pos, token);
            if (direct != null) {
                return Set.of(normalizeLemma(direct.getLemma()));
            }

            IndexWord underscored = WORDNET_DICTIONARY.lookupIndexWord(pos, token.replace(' ', '_'));
            if (underscored != null) {
                return Set.of(normalizeLemma(underscored.getLemma()));
            }
        } catch (JWNLException ignored) {
            return Set.of();
        }
        return Set.of();
    }

    private static String normalizeLemma(String lemma) {
        return lemma == null ? "" : lemma.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
    }

    private static Dictionary loadWordNetDictionary() {
        try (InputStream resourceStream = AnswerChecker.class.getResourceAsStream(
                "/net/sf/extjwnl/data/wordnet/wn31/res_properties.xml")) {
            if (resourceStream != null) {
                return Dictionary.getInstance(resourceStream);
            }
        } catch (Exception ignored) {
            // Fall through to fallback resource.
        }

        try (InputStream fallbackStream = AnswerChecker.class.getResourceAsStream("/extjwnl_resource_properties.xml")) {
            if (fallbackStream != null) {
                return Dictionary.getInstance(fallbackStream);
            }
        } catch (Exception ignored) {
            // Keep WordNet optional.
        }

        return null;
    }

    private static String normalizeForCharacters(String text) {
        String normalized = stripAccents(text).toLowerCase(Locale.ROOT);
        normalized = PUNCTUATION.matcher(normalized).replaceAll(" ");
        normalized = NON_ALNUM_SPACE.matcher(normalized).replaceAll(" ");
        normalized = MULTISPACE.matcher(normalized).replaceAll(" ").trim();
        return normalized;
    }

    private static String normalizeToWords(String text) {
        return normalizeForCharacters(text);
    }

    private static String stripAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return COMBINING_MARKS.matcher(normalized).replaceAll("");
    }

    private static String verdictFor(double score) {
        if (score >= 0.85) {
            return "CORRECT";
        }
        if (score >= 0.60) {
            return "CLOSE";
        }
        return "INCORRECT";
    }

    private static double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }

    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}

