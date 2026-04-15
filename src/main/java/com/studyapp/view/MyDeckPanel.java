package com.studyapp.view;

import java.time.LocalDateTime;
import java.util.List;

import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MyDeckPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String TOOLBAR_BUTTON_STYLE = "-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-text-fill: black; -fx-padding: 5 20; -fx-cursor: hand;";
    private static final String TOOLBAR_BUTTON_HOVER_STYLE = "-fx-background-color: #eafbf1; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-text-fill: black; -fx-padding: 5 20; -fx-cursor: hand;";
    private static final String DECK_ROW_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 15; -fx-cursor: hand;";
    private static final String DECK_ROW_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 15; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_STYLE = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_HOVER_STYLE = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";

    public record DeckData(Deck deck, int cardCount, int progressPercent, List<Flashcard> cards) {
    }

    public static VBox create(BorderPane mainLayout) {
        return create(mainLayout, "Manage decks, or import/export your deck data as JSON.", PRIMARY_BLUE);
    }

    public static VBox create(BorderPane mainLayout, String statusMessage, String statusColor) {
        List<DeckData> decks = createPrototypeDecks();

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("My Decks");
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button newBtn = createToolbarButton("New");
        Button importBtn = createToolbarButton("Import");
        Button exportBtn = createToolbarButton("Export");

        TextField searchField = new TextField();
        searchField.setPromptText("Search decks");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");

        Label searchIcon = new Label("Search");
        searchIcon.setFont(Font.font("Serif", 14));
        searchIcon.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(Font.font("Serif", 16));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Name");
        sortCombo.setValue("Newest");
        sortCombo.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");
        sortCombo.setPrefWidth(120);

        toolbar.getChildren().addAll(newBtn, importBtn, exportBtn, searchField, searchIcon, spacer, sortLabel, sortCombo);

        Label statusLabel = new Label(statusMessage);
        statusLabel.setFont(Font.font("Serif", 15));
        statusLabel.setTextFill(Color.web(statusColor));
        statusLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deckList = new VBox(15);
        deckList.setPadding(new Insets(5, 15, 5, 5));
        deckList.setStyle("-fx-background-color: white;");

        for (DeckData deck : decks) {
            deckList.getChildren().add(createDeckItem(deck, mainLayout));
        }

        scrollPane.setContent(deckList);
        mainContent.getChildren().addAll(header, toolbar, statusLabel, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private static Button createToolbarButton(String text) {
        Button button = new Button(text);
        button.setStyle(TOOLBAR_BUTTON_STYLE);
        button.setOnMouseEntered(e -> button.setStyle(TOOLBAR_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(TOOLBAR_BUTTON_STYLE));
        return button;
    }

    private static HBox createDeckItem(DeckData deck, BorderPane mainLayout) {
        HBox row = new HBox(20);
        row.setStyle(DECK_ROW_STYLE);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle(DECK_ROW_HOVER_STYLE));
        row.setOnMouseExited(e -> row.setStyle(DECK_ROW_STYLE));

        VBox leftInfo = new VBox(5);
        leftInfo.setPrefWidth(250);
        Label idLbl = new Label("ID: " + deck.deck().getDeckID());
        idLbl.setFont(Font.font("Serif", 14));
        Label titleLbl = new Label(deck.deck().getName());
        titleLbl.setFont(Font.font("Serif", 18));
        leftInfo.getChildren().addAll(idLbl, titleLbl);

        VBox middleInfo = new VBox(5);
        Label cardsLbl = new Label("Cards: " + deck.cardCount());
        cardsLbl.setFont(Font.font("Serif", 14));
        Label progLbl = new Label(String.format("Progress: %d%%", deck.progressPercent()));
        progLbl.setFont(Font.font("Serif", 14));
        middleInfo.getChildren().addAll(cardsLbl, progLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("OPEN");
        selectBtn.setStyle(OPEN_BUTTON_STYLE);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(OPEN_BUTTON_HOVER_STYLE));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(OPEN_BUTTON_STYLE));
        selectBtn.setOnAction(e -> DeckDetailPanel.show(mainLayout, deck));

        row.getChildren().addAll(leftInfo, middleInfo, spacer, selectBtn);
        return row;
    }

    private static List<DeckData> createPrototypeDecks() {
        Deck javaDeck = createDeck(101, "Java Foundations", "Core Java syntax, OOP, and collections.", 75);
        Deck sqlDeck = createDeck(102, "SQL Essentials", "Query basics, joins, and schema design.", 60);
        Deck navDeck = createDeck(103, "UI Navigation", "Prototype routing, panels, and layout flow.", 90);
        Deck dsDeck = createDeck(104, "Data Structures", "Arrays, linked lists, stacks, and trees.", 40);

        List<Flashcard> javaCards = List.of(
                createCard(1, javaDeck, "What does JVM stand for?", "Java Virtual Machine", "Easy"),
                createCard(2, javaDeck, "What collection keeps insertion order?", "LinkedHashMap", "Medium"),
                createCard(3, javaDeck, "What keyword prevents inheritance?", "final", "Easy"),
                createCard(4, javaDeck, "Which interface supports lambda expressions?", "Functional interface", "Medium"));

        List<Flashcard> sqlCards = List.of(
                createCard(5, sqlDeck, "What clause filters grouped rows?", "HAVING", "Medium"),
                createCard(6, sqlDeck, "What does a LEFT JOIN keep?", "All rows from the left table", "Easy"),
                createCard(7, sqlDeck, "What command removes a table?", "DROP TABLE", "Easy"));

        List<Flashcard> navCards = List.of(
                createCard(8, navDeck, "Which layout is used for the main shell?", "BorderPane", "Easy"),
                createCard(9, navDeck, "Which panel opens from Dashboard next?", "My Decks", "Medium"));

        List<Flashcard> dsCards = List.of(
                createCard(10, dsDeck, "Which data structure uses FIFO?", "Queue", "Easy"),
                createCard(11, dsDeck, "Which traversal visits root-left-right?", "Preorder", "Medium"),
                createCard(12, dsDeck, "Which structure gives O(1) average lookup?", "Hash table", "Hard"),
                createCard(13, dsDeck, "Which structure supports LIFO?", "Stack", "Easy"),
                createCard(14, dsDeck, "Which tree keeps values ordered?", "Binary search tree", "Medium"));

        return List.of(
                new DeckData(javaDeck, javaCards.size(), 75, javaCards),
                new DeckData(sqlDeck, sqlCards.size(), 60, sqlCards),
                new DeckData(navDeck, navCards.size(), 90, navCards),
                new DeckData(dsDeck, dsCards.size(), 40, dsCards));
    }

    private static Deck createDeck(int id, String name, String description, int progressPercent) {
        return new Deck(id, name, description, LocalDateTime.of(2026, 4, 10 + (id - 101), 9 + progressPercent % 3, 0));
    }

    private static Flashcard createCard(int cardId, Deck deck, String question, String answer, String difficulty) {
        return new Flashcard(cardId, deck, question, answer, difficulty, deck.getCreatedAt());
    }
}
