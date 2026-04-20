package com.studyapp.view;

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

public class AllCardsPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String DECK_ROW_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 15; -fx-cursor: hand;";
    private static final String DECK_ROW_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 15; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_STYLE = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_HOVER_STYLE = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";

    public static VBox create(BorderPane mainLayout, MainController mc) {
        return create(mainLayout, null, mc);
    }

    public static VBox create(BorderPane mainLayout, Deck deck, MainController mc) {
        List<Flashcard> cards = deck == null
                ? mc.allFlashcards()
                : mc.getFlashcardsByDeck(deck.getDeckID());

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label(deck == null ? "All Cards" : deck.getName());
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search decks");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");

        Label searchIcon = new Label("Search");
        searchIcon.setFont(Font.font("Serif", 14));
        searchIcon.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(Font.font("Serif", 16));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Question");
        sortCombo.setValue("Newest");
        sortCombo.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");
        sortCombo.setPrefWidth(120);

        toolbar.getChildren().addAll(searchField, searchIcon, spacer, sortLabel, sortCombo);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cardsBox = new VBox(15);
        cardsBox.setPadding(new Insets(5, 15, 5, 5));
        cardsBox.setStyle("-fx-background-color: white;");

        updateCardList(cardsBox, cards, "", "Newest", mainLayout, mc);

        // Search functionality - filter as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCardList(cardsBox, cards, newValue, sortCombo.getValue(), mainLayout, mc);
        });

        // Sort functionality - re-sort when dropdown changes
        sortCombo.setOnAction(e -> {
            updateCardList(cardsBox, cards, searchField.getText(), sortCombo.getValue(), mainLayout, mc);
        });

        scrollPane.setContent(cardsBox);
        mainContent.getChildren().addAll(header, toolbar, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private static VBox createCard(Flashcard flashcard, BorderPane mainLayout, MainController mc) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));
        card.setSpacing(8);
        card.setStyle(DECK_ROW_STYLE);

        VBox textContainer = new VBox(5);
        Label question = new Label(flashcard.getQuestion());
        question.setFont(Font.font("Serif", 18));
        question.setTextFill(Color.BLACK);
        question.setWrapText(true);

        Label answer = new Label("Answer: " + flashcard.getAnswer());
        answer.setFont(Font.font("Serif", 15));
        answer.setTextFill(Color.web("#475569"));

        Label difficulty = new Label("Difficulty: " + flashcard.getDifficulty());
        difficulty.setFont(Font.font("Serif", 14));
        difficulty.setTextFill(Color.web(PRIMARY_BLUE));

        textContainer.getChildren().addAll(question, answer, difficulty);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("OPEN");
        selectBtn.setStyle(OPEN_BUTTON_STYLE);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(OPEN_BUTTON_HOVER_STYLE));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(OPEN_BUTTON_STYLE));
        selectBtn.setOnAction(e -> CardDetailPanel.show(mainLayout, flashcard, mc));

        HBox mainContent = new HBox();
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.getChildren().addAll(textContainer, spacer, selectBtn);

        card.getChildren().add(mainContent);

        card.setOnMouseEntered(e -> card.setStyle(DECK_ROW_HOVER_STYLE));
        card.setOnMouseExited(e -> card.setStyle(DECK_ROW_STYLE));

        return card;
    }

    private static void updateCardList(VBox cardsBox, List<Flashcard> flashcards, String searchQuery,
                                       String sortOption, BorderPane mainLayout, MainController mc) {
        // Filter decks by search query
        String query = searchQuery == null ? "" : searchQuery.toLowerCase().trim();
        List<Flashcard> filteredCards = new java.util.ArrayList<>(flashcards.stream()
                .filter(flashcard -> {
                    if (query.isEmpty()) return true;
                    String question = flashcard.getQuestion().toLowerCase();
                    String answer = flashcard.getAnswer().toLowerCase();
                    return question.contains(query) || answer.contains(query);
                })
                .toList());

        switch (sortOption) {
            case "Oldest":
                filteredCards.sort(java.util.Comparator.comparing(Flashcard::getCreatedAt));
                break;
            case "Name":
                filteredCards.sort(java.util.Comparator.comparing(Flashcard::getQuestion, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Newest":
            default:
                filteredCards.sort(java.util.Comparator.comparing(Flashcard::getCreatedAt).reversed());
                break;
        }

        // Clear and repopulate the deck list
        cardsBox.getChildren().clear();
        if (filteredCards.isEmpty()) {
            Label emptyLabel = new Label("No cards found");
            emptyLabel.setFont(Font.font("Serif", 16));
            emptyLabel.setTextFill(Color.GRAY);
            emptyLabel.setPadding(new Insets(20));
            cardsBox.getChildren().add(emptyLabel);
        } else {
            for (Flashcard flashcard : filteredCards) {
                cardsBox.getChildren().add(createCard(flashcard, mainLayout, mc));
            }
        }
    }
}
