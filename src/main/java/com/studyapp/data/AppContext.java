package com.studyapp.data;

import com.studyapp.controller.DeckController;
import com.studyapp.controller.ProgressController;
import com.studyapp.controller.StudyController;

public final class AppContext {

    private static DataAccessProvider provider;
    private static DeckController deckController;
    private static ProgressController progressController;
    private static StudyController studyController;

    static {
        useInMemoryData();
    }

    private AppContext() {
    }

    public static void useInMemoryData() {
        configure(new InMemoryDataAccessProvider());
    }

    public static void useMySqlData() {
        configure(new JdbcDataAccessProvider());
    }

    private static void configure(DataAccessProvider newProvider) {
        provider = newProvider;
        deckController = new DeckController(provider.decks(), provider.flashcards(), provider.cardReviews());
        progressController = new ProgressController(provider.flashcards(), provider.cardReviews(), provider.studySessions());
        studyController = new StudyController(provider.flashcards());
    }

    public static DeckController decks() {
        return deckController;
    }

    public static ProgressController progress() {
        return progressController;
    }

    public static StudyController study() {
        return studyController;
    }
}
