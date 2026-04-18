package com.studyapp.view;

import java.time.LocalDateTime;
import java.util.List;

import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AllCardsPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";

    public static VBox create(BorderPane mainLayout) {
        return create(mainLayout, null);
    }

    public static VBox create(BorderPane mainLayout, Deck deck) {
        List<Flashcard> cards = deck == null
                ? createAllCards()
                : createAllCards().stream()
                        .filter(card -> card.getDeck().getDeckID() == deck.getDeckID())
                        .toList();

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

        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Label helperLabel = new Label(deck == null
                ? "This is the last screen in the stripped-down flow. All cards shown here are hardcoded locally."
                : "Prototype card list for the selected deck.");
        helperLabel.setFont(Font.font("Serif", 15));
        helperLabel.setTextFill(Color.web("#0f766e"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button studyBtn = new Button("Study Disabled");
        studyBtn.setDisable(true);
        studyBtn.setFont(Font.font("Serif", 16));
        studyBtn.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #94a3b8; -fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 30;");

        actionRow.getChildren().addAll(helperLabel, spacer, studyBtn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cardsBox = new VBox(15);
        cardsBox.setPadding(new Insets(5, 15, 5, 5));
        cardsBox.setStyle("-fx-background-color: white;");

        for (Flashcard card : cards) {
            cardsBox.getChildren().add(createCard(card));
        }

        if (cards.isEmpty()) {
            cardsBox.getChildren().add(new Label("No cards available yet."));
        }

        scrollPane.setContent(cardsBox);
        mainContent.getChildren().addAll(header, actionRow, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private static List<Flashcard> createAllCards() {
        Deck javaDeck = createDeck(101, "Java Foundations");
        Deck sqlDeck = createDeck(102, "SQL Essentials");
        Deck navDeck = createDeck(103, "UI Navigation");
        Deck dsDeck = createDeck(104, "Data Structures");

        return List.of(
                createCard(1, javaDeck, "What does JVM stand for?", "Java Virtual Machine", "Easy"),
                createCard(2, javaDeck, "What collection keeps insertion order?", "LinkedHashMap", "Medium"),
                createCard(3, javaDeck, "What keyword prevents inheritance?", "final", "Easy"),
                createCard(4, sqlDeck, "What clause filters grouped rows?", "HAVING", "Medium"),
                createCard(5, sqlDeck, "What does a LEFT JOIN keep from the first table?", "All rows from the left table", "Easy"),
                createCard(6, navDeck, "Which layout is used for the main shell?", "BorderPane", "Easy"),
                createCard(7, navDeck, "What panel opens from the Dashboard next button?", "My Decks", "Medium"),
                createCard(8, dsDeck, "Which data structure uses FIFO?", "Queue", "Easy"),
                createCard(9, dsDeck, "Which structure supports LIFO?", "Stack", "Easy"));
    }

    private static Deck createDeck(int id, String name) {
        return new Deck(id, name, "", LocalDateTime.of(2026, 4, 10, 9, 0));
    }

    private static Flashcard createCard(int cardId, Deck deck, String question, String answer, String difficulty) {
        return new Flashcard(cardId, deck, question, answer, difficulty, LocalDateTime.of(2026, 4, 10, 9, 0));
    }

    private static VBox createCard(Flashcard flashcard) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));
        card.setSpacing(8);
        card.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white;");
        VBox.setVgrow(card, Priority.NEVER);

        Label question = new Label(flashcard.getQuestion());
        question.setFont(Font.font("Serif", 18));
        question.setTextFill(Color.BLACK);
        question.setWrapText(true);

        Label answer = new Label("Answer: " + flashcard.getAnswer());
        answer.setFont(Font.font("Serif", 15));
        answer.setTextFill(Color.web("#475569"));
        answer.setWrapText(true);

        Label difficulty = new Label("Difficulty: " + flashcard.getDifficulty());
        difficulty.setFont(Font.font("Serif", 14));
        difficulty.setTextFill(Color.web(PRIMARY_BLUE));

        card.getChildren().addAll(question, answer, difficulty);
        return card;
    }
}
