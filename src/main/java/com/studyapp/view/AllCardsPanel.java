package com.studyapp.view;

import com.studyapp.util.UiScale;

import java.util.List;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AllCardsPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String TOOLBAR_BUTTON_STYLE = "-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: black; -fx-padding: 10 28; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String TOOLBAR_BUTTON_HOVER_STYLE = "-fx-background-color: #eafbf1; -fx-border-color: #22c55e; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: black; -fx-padding: 10 28; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String DECK_ROW_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 20 22; -fx-cursor: hand;";
    private static final String DECK_ROW_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 20 22; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_STYLE = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 24; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_HOVER_STYLE = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 24; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String TOOLBAR_INPUT_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-background-color: white; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-padding: 6 12;";
    private static final Insets PAGE_PADDING = new Insets(12);
    private static final Insets CONTENT_PADDING = new Insets(14);
    private static final int CONTENT_SPACING = 12;

    // ── NEW: page size constant ──────────────────────────────────────────────
    private static final int PAGE_SIZE = 5;

    private static double Xoffset = 0;
    private static double Yoffset = 0;

    public static VBox create(BorderPane mainLayout, MainController mc) {
        return create(mainLayout, null, mc);
    }

    public static VBox create(BorderPane mainLayout, Deck deck, MainController mc) {
        List<Flashcard> cards = deck == null
                ? mc.allFlashcards()
                : mc.getFlashcardsByDeck(deck.getDeckID());

        VBox wrapper = new VBox();
        wrapper.setPadding(PAGE_PADDING);
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(CONTENT_SPACING);
        mainContent.setPadding(CONTENT_PADDING);
        mainContent.setStyle(BORDER_STYLE);
        mainContent.setMaxWidth(Double.MAX_VALUE);
        mainContent.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label(deck == null ? "All Cards" : deck.getName());
        header.setFont(UiScale.titleFont(64));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        HBox toolbar = new HBox(18);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(4, 0, 6, 0));

        Button newBtn = createToolbarButton("New");
        newBtn.setOnAction(e -> {
            if (mc.allDecks().isEmpty()) {
                MainFrame.showErrorDialog("No decks available. Create a deck first.");
                return;
            }
            showCreateCardDialog(mainLayout, deck, mc);
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search cards");
        searchField.setPrefWidth(UiScale.size(460));
        searchField.setPrefHeight(UiScale.size(44));
        searchField.setStyle(TOOLBAR_INPUT_STYLE);

        Label searchIcon = new Label("Search");
        searchIcon.setFont(UiScale.bodyFont(20));
        searchIcon.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(UiScale.bodyFont(20));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Question");
        sortCombo.setValue("Newest");
        sortCombo.setStyle(TOOLBAR_INPUT_STYLE);
        sortCombo.setPrefWidth(UiScale.size(180));
        sortCombo.setPrefHeight(UiScale.size(44));

        toolbar.getChildren().addAll(newBtn, searchField, searchIcon, spacer, sortLabel, sortCombo);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cardsBox = new VBox(15);
        cardsBox.setPadding(new Insets(5, 15, 5, 5));
        cardsBox.setStyle("-fx-background-color: white;");

        HBox paginationBar = new HBox(10);
        paginationBar.setAlignment(Pos.CENTER);
        paginationBar.setPadding(new Insets(10, 0, 4, 0));

        // ── NEW: single mutable page tracker shared across all listeners ─────
        int[] currentPage = {0};

        updateCardList(cardsBox, paginationBar, cards, "", "Newest", currentPage[0], deck, mainLayout, mc);

        // Search listener — reset to page 0 on new query
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentPage[0] = 0;
            updateCardList(cardsBox, paginationBar, cards, newValue, sortCombo.getValue(), currentPage[0], deck, mainLayout, mc);
        });

        // Sort listener — reset to page 0 on new sort
        sortCombo.setOnAction(e -> {
            currentPage[0] = 0;
            updateCardList(cardsBox, paginationBar, cards, searchField.getText(), sortCombo.getValue(), currentPage[0], deck, mainLayout, mc);
        });

        scrollPane.setContent(cardsBox);
        mainContent.getChildren().addAll(header, toolbar, scrollPane, paginationBar);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    // ── UPDATED: added pageIndex param; builds card slice + pagination bar ──
    private static void updateCardList(VBox cardsBox, HBox paginationBar, List<Flashcard> flashcards,
                                       String searchQuery, String sortOption,
                                       int pageIndex,
                                       Deck deck,
                                       BorderPane mainLayout, MainController mc) {
        String query = searchQuery == null ? "" : searchQuery.toLowerCase().trim();

        List<Flashcard> filteredCards = new java.util.ArrayList<>(flashcards.stream()
                .filter(flashcard -> {
                    if (query.isEmpty()) return true;
                    String question = flashcard.getQuestion().toLowerCase();
                    String answer   = flashcard.getAnswer().toLowerCase();
                    return question.contains(query) || answer.contains(query);
                })
                .toList());

        switch (sortOption) {
            case "Oldest":
                filteredCards.sort(java.util.Comparator.comparing(Flashcard::getCreatedAt));
                break;
            case "Question":
                filteredCards.sort(java.util.Comparator.comparing(
                        Flashcard::getQuestion, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Newest":
            default:
                filteredCards.sort(java.util.Comparator.comparing(
                        Flashcard::getCreatedAt).reversed());
                break;
        }

        cardsBox.getChildren().clear();
        paginationBar.getChildren().clear();

        if (filteredCards.isEmpty()) {
            Label emptyLabel = new Label("No cards found");
            emptyLabel.setFont(UiScale.bodyFont(16));
            emptyLabel.setTextFill(Color.GRAY);
            emptyLabel.setPadding(new Insets(20));
            cardsBox.getChildren().add(emptyLabel);
            paginationBar.setVisible(false);
            paginationBar.setManaged(false);
            return;
        }

        paginationBar.setVisible(true);
        paginationBar.setManaged(true);

        // ── Slice to current page ────────────────────────────────────────────
        int totalPages = (int) Math.ceil((double) filteredCards.size() / PAGE_SIZE);
        int safePage   = Math.max(0, Math.min(pageIndex, totalPages - 1));
        int fromIndex  = safePage * PAGE_SIZE;
        int toIndex    = Math.min(fromIndex + PAGE_SIZE, filteredCards.size());

        List<Flashcard> pageCards = filteredCards.subList(fromIndex, toIndex);
        for (Flashcard flashcard : pageCards) {
            cardsBox.getChildren().add(createCard(flashcard, deck, mainLayout, mc));
        }

        // ── Pagination bar ───────────────────────────────────────────────────
        Button prevBtn = new Button("< Prev");
        prevBtn.setStyle(OPEN_BUTTON_STYLE);
        prevBtn.setDisable(safePage == 0);
        prevBtn.setOnMouseEntered(e -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        prevBtn.setOnMouseExited(e  -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_STYLE); });

        Label pageLabel = new Label("Page " + (safePage + 1) + " of " + totalPages);
        pageLabel.setFont(UiScale.bodyFont(18));
        pageLabel.setTextFill(Color.web(PRIMARY_BLUE));

        Button nextBtn = new Button("Next >");
        nextBtn.setStyle(OPEN_BUTTON_STYLE);
        nextBtn.setDisable(safePage >= totalPages - 1);
        nextBtn.setOnMouseEntered(e -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        nextBtn.setOnMouseExited(e  -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_STYLE); });

        prevBtn.setOnAction(e -> {
            updateCardList(cardsBox, paginationBar, flashcards, searchQuery, sortOption, safePage - 1, deck, mainLayout, mc);
        });
        nextBtn.setOnAction(e -> {
            updateCardList(cardsBox, paginationBar, flashcards, searchQuery, sortOption, safePage + 1, deck, mainLayout, mc);
        });

        paginationBar.getChildren().addAll(prevBtn, pageLabel, nextBtn);
    }


    private static void showCreateCardDialog(BorderPane mainLayout, Deck currentDeck, MainController mc) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Create");

        VBox container = new VBox(4);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: #2a548f; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: #f8fafc;");

        container.setOnMousePressed(event -> {
            Xoffset = event.getSceneX();
            Yoffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - Xoffset);
            stage.setY(event.getScreenY() - Yoffset);
        });

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);

        Button closeBtn = new Button("X");
        String xBarNormal = "-fx-background-color: transparent; -fx-text-fill: #1A438E; -fx-font-size: 18; -fx-cursor: hand;";
        String xBarHover = "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 0 10 0 0;";

        closeBtn.setStyle(xBarNormal);
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(xBarHover));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(xBarNormal));

        topBar.getChildren().add(closeBtn);
        VBox.setMargin(topBar, new Insets(5, -30, 0, 0));

        Label title = new Label("Create Card");
        title.setFont(UiScale.headingFont(38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label deckLabel = new Label("Choose Deck:");
        deckLabel.setFont(UiScale.bodyFont(17));
        deckLabel.setTextFill(Color.web(PRIMARY_BLUE));

        List<Deck> availableDecks = mc.allDecks();

        ComboBox<Deck> deckCombo = new ComboBox<>();
        deckCombo.getItems().addAll(availableDecks);
        deckCombo.setMaxWidth(Double.MAX_VALUE);
        deckCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");
        deckCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Deck item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName().toUpperCase());
            }
        });
        deckCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Deck item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName().toUpperCase());
            }
        });
        Deck initialDeck = currentDeck != null ? currentDeck : availableDecks.get(0);
        deckCombo.setValue(initialDeck);

        Label diffLabel = new Label("Difficulty");
        diffLabel.setFont(UiScale.bodyFont(17));
        diffLabel.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<String> diffCombo = new ComboBox<>();
        diffCombo.getItems().addAll("EASY", "MEDIUM", "HARD");
        diffCombo.setValue("MEDIUM");
        diffCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox diffRow = new HBox(12, diffLabel, new Region(), diffCombo);
        diffRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(diffRow.getChildren().get(1), Priority.ALWAYS);

        Label frontLabel = new Label("Enter Front");
        frontLabel.setFont(UiScale.bodyFont(17));
        frontLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextArea frontArea = new TextArea();
        frontArea.setWrapText(true);
        frontArea.setPrefHeight(176);
        frontArea.setStyle("-fx-control-inner-background: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-padding: 8;");

        Label backLabel = new Label("Enter Back");
        backLabel.setFont(UiScale.bodyFont(17));
        backLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextArea backArea = new TextArea();
        backArea.setWrapText(true);
        backArea.setPrefHeight(154);
        backArea.setStyle("-fx-control-inner-background: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-padding: 8;");

        Button createBtn = new Button("CREATE");
        createBtn.setPrefWidth(250);
        createBtn.setPrefHeight(56);
        createBtn.setStyle("-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; " +
                "-fx-font-size: 17; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-background-radius: 28; " +
                "-fx-cursor: hand;");
        createBtn.setOnMouseEntered(e ->
                createBtn.setStyle("-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; " +
                        "-fx-font-size: 17; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-background-radius: 28; " +
                        "-fx-cursor: hand;"));
        createBtn.setOnMouseExited(e ->
                createBtn.setStyle("-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; " +
                        "-fx-font-size: 17; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-background-radius: 28; " +
                        "-fx-cursor: hand;"));

        createBtn.setOnAction(e -> {
            Deck selectedDeck = deckCombo.getValue();
            String question = frontArea.getText().trim();
            String answer = backArea.getText().trim();
            String difficulty = diffCombo.getValue();

            try {
                mc.createFlashcard(selectedDeck.getDeckID(), question, answer, difficulty);
                dialog.close();
                MainFrame.showSuccessDialog("Card created successfully!");
                mainLayout.setCenter(AllCardsPanel.create(mainLayout, currentDeck, mc));
            } catch (CustomException ex) {
                MainFrame.showErrorDialog("Creation failed: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(createBtn);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));
        VBox.setMargin(buttonBox, new Insets(6, 0, 0, 0));

        container.getChildren().addAll(topBar, title, deckLabel, deckCombo, diffRow, frontLabel, frontArea, backLabel, backArea, buttonBox);

        Scene scene = new Scene(container, 360, 680);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static Button createToolbarButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(UiScale.size(44));
        button.setStyle(TOOLBAR_BUTTON_STYLE);
        button.setOnMouseEntered(e -> button.setStyle(TOOLBAR_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(TOOLBAR_BUTTON_STYLE));
        return button;
    }

    private static VBox createCard(Flashcard flashcard, Deck deck,
                                   BorderPane mainLayout, MainController mc) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinHeight(UiScale.size(124));
        card.setPadding(UiScale.insets(22));
        card.setSpacing(10);
        card.setStyle(DECK_ROW_STYLE);

        VBox textContainer = new VBox(8);
        Label question = new Label(flashcard.getQuestion());
        question.setFont(UiScale.headingFont(24));
        question.setTextFill(Color.BLACK);
        question.setWrapText(true);

        Label answer = new Label("Answer: " + flashcard.getAnswer());
        answer.setFont(UiScale.bodyFont(20));
        answer.setTextFill(Color.web("#475569"));

        Label difficulty = new Label("Difficulty: " + flashcard.getDifficulty().toUpperCase());
        difficulty.setFont(UiScale.bodyFont(18));
        difficulty.setTextFill(Color.web(PRIMARY_BLUE));

        textContainer.getChildren().addAll(question, answer, difficulty);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("OPEN");
        selectBtn.setPrefWidth(UiScale.size(120));
        selectBtn.setPrefHeight(UiScale.size(48));
        selectBtn.setStyle(OPEN_BUTTON_STYLE);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(OPEN_BUTTON_HOVER_STYLE));
        selectBtn.setOnMouseExited(e  -> selectBtn.setStyle(OPEN_BUTTON_STYLE));

        selectBtn.setOnAction(e -> CardDetailPanel.show(
                mainLayout, flashcard, mc,
                () -> mainLayout.setCenter(AllCardsPanel.create(mainLayout, deck, mc))));

        HBox mainContent = new HBox();
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.getChildren().addAll(textContainer, spacer, selectBtn);

        card.getChildren().add(mainContent);
        card.setOnMouseEntered(e -> card.setStyle(DECK_ROW_HOVER_STYLE));
        card.setOnMouseExited(e  -> card.setStyle(DECK_ROW_STYLE));

        return card;
    }
}

