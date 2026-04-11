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
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";

    // ── Shared hardcoded data ─────────────────────────────────────────────────

    public record DeckData(Deck deck, int cardCount, int progressPercent, List<Flashcard> cards) {}

    public static List<DeckData> getHardcodedDecks() {
        Deck d100 = new Deck(100, "CMSC127 - LE2",
                "Covers SQL, ER diagrams, and normalization.",
                LocalDateTime.of(2026, 3, 31, 15, 42, 10));
        Deck d101 = new Deck(101, "MATH55 - LE4",
                "Covers limits, derivatives, and integrals.",
                LocalDateTime.of(2026, 4, 1, 10, 0, 0));
        Deck d102 = new Deck(102, "BIOLOGY - QUIZ",
                "General biology concepts and processes.",
                LocalDateTime.of(2026, 4, 2, 14, 0, 0));
        Deck d103 = new Deck(103, "CMSC130 - LE3",
                "Data structures and algorithm basics.",
                LocalDateTime.of(2026, 4, 5, 9, 0, 0));

        List<Flashcard> cards100 = List.of(
                new Flashcard(1, d100, "What does JDBC stand for?",
                        "Java Database Connectivity", "Medium", LocalDateTime.of(2026, 3, 31, 15, 42, 10)),
                new Flashcard(2, d100, "What is normalization?",
                        "Organizing a database to reduce redundancy and improve data integrity.",
                        "Hard", LocalDateTime.of(2026, 3, 31, 15, 42, 10)),
                new Flashcard(3, d100, "What is a Primary Key?",
                        "A unique identifier for a record in a table.",
                        "Easy", LocalDateTime.of(2026, 3, 31, 15, 42, 10)),
                new Flashcard(4, d100, "What does SQL stand for?",
                        "Structured Query Language",
                        "Easy", LocalDateTime.of(2026, 3, 31, 15, 42, 10)));

        List<Flashcard> cards101 = List.of(
                new Flashcard(5, d101, "What is a derivative?",
                        "The rate of change of a function.", "Medium", LocalDateTime.of(2026, 4, 1, 10, 0, 0)),
                new Flashcard(6, d101, "What is a limit?",
                        "The value a function approaches as the input approaches a point.",
                        "Medium", LocalDateTime.of(2026, 4, 1, 10, 0, 0)),
                new Flashcard(7, d101, "What is an integral?",
                        "The area under a curve.", "Hard", LocalDateTime.of(2026, 4, 1, 10, 0, 0)),
                new Flashcard(8, d101, "What is the chain rule?",
                        "A formula for computing the derivative of a composite function.",
                        "Hard", LocalDateTime.of(2026, 4, 1, 10, 0, 0)));

        List<Flashcard> cards102 = List.of(
                new Flashcard(9, d102, "What is photosynthesis?",
                        "The process by which plants convert sunlight into food.",
                        "Easy", LocalDateTime.of(2026, 4, 2, 14, 0, 0)),
                new Flashcard(10, d102, "What is mitosis?",
                        "Cell division producing two identical daughter cells.",
                        "Medium", LocalDateTime.of(2026, 4, 2, 14, 0, 0)),
                new Flashcard(11, d102, "What is meiosis?",
                        "Cell division producing four genetically distinct gametes.",
                        "Hard", LocalDateTime.of(2026, 4, 2, 14, 0, 0)),
                new Flashcard(12, d102, "What is osmosis?",
                        "Movement of water across a semipermeable membrane.",
                        "Easy", LocalDateTime.of(2026, 4, 2, 14, 0, 0)));

        List<Flashcard> cards103 = List.of(
                new Flashcard(13, d103, "What is a stack?",
                        "A LIFO data structure.", "Easy", LocalDateTime.of(2026, 4, 5, 9, 0, 0)),
                new Flashcard(14, d103, "What is a queue?",
                        "A FIFO data structure.", "Easy", LocalDateTime.of(2026, 4, 5, 9, 0, 0)),
                new Flashcard(15, d103, "What is Big-O notation?",
                        "A way to describe algorithm time/space complexity.",
                        "Medium", LocalDateTime.of(2026, 4, 5, 9, 0, 0)),
                new Flashcard(16, d103, "What is a binary search tree?",
                        "A tree where each node's left children are smaller and right children are larger.",
                        "Hard", LocalDateTime.of(2026, 4, 5, 9, 0, 0)));

        return List.of(
                new DeckData(d100, 40, 98, cards100),
                new DeckData(d101, 30, 75, cards101),
                new DeckData(d102, 50, 88, cards102),
                new DeckData(d103, 30, 100, cards103));
    }

    // ── Panel builder ─────────────────────────────────────────────────────────

    public static VBox create(BorderPane mainLayout) {
        List<DeckData> decks = getHardcodedDecks();

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Header
        Label header = new Label("My Decks");
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE
                + "; -fx-background-radius: 8; -fx-padding: 10;");

        // Action row: new | search | sort by
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Button newBtn = new Button("new");
        newBtn.setDisable(true);
        newBtn.setFont(Font.font("Serif", 14));
        newBtn.setStyle(
                "-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 20;"
                + " -fx-background-radius: 20; -fx-text-fill: #22c55e; -fx-padding: 5 18;");

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setDisable(true);
        searchField.setPrefWidth(220);
        searchField.setStyle(
                "-fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 5 10;");

        Label searchIcon = new Label("\uD83D\uDD0D");
        searchIcon.setFont(Font.font(16));

        Label sortLabel = new Label("sort by:");
        sortLabel.setFont(Font.font("Serif", 14));

        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.setDisable(true);
        sortBox.setPrefWidth(130);

        actionRow.getChildren().addAll(newBtn, leftSpacer, searchField, searchIcon, sortLabel, sortBox);

        // Deck list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0;"
                + " -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deckList = new VBox(15);
        deckList.setPadding(new Insets(5, 15, 5, 5));
        deckList.setStyle("-fx-background-color: white;");

        for (DeckData dd : decks) {
            deckList.getChildren().add(createDeckRow(dd, mainLayout));
        }

        scrollPane.setContent(deckList);
        mainContent.getChildren().addAll(header, actionRow, scrollPane);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    // ── Deck row ──────────────────────────────────────────────────────────────

    private static HBox createDeckRow(DeckData dd, BorderPane mainLayout) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-border-color: " + PRIMARY_BLUE
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: white;");

        VBox nameInfo = new VBox(3);
        HBox.setHgrow(nameInfo, Priority.ALWAYS);

        Label idLbl = new Label("ID: " + dd.deck().getDeckID());
        idLbl.setFont(Font.font("Serif", 12));
        idLbl.setTextFill(Color.web("#6b7280"));

        Label nameLbl = new Label(dd.deck().getName());
        nameLbl.setFont(Font.font("Serif", 20));
        nameLbl.setTextFill(Color.BLACK);

        nameInfo.getChildren().addAll(idLbl, nameLbl);

        VBox stats = new VBox(4);
        stats.setAlignment(Pos.CENTER_LEFT);

        Label cardsLbl = new Label("Cards: " + dd.cardCount());
        cardsLbl.setFont(Font.font("Serif", 13));
        cardsLbl.setTextFill(Color.web("#475569"));

        Label progressLbl = new Label("Progress: " + dd.progressPercent() + "%");
        progressLbl.setFont(Font.font("Serif", 13));
        progressLbl.setTextFill(Color.web("#475569"));

        stats.getChildren().addAll(cardsLbl, progressLbl);

        Button selectBtn = new Button("SELECT");
        selectBtn.setFont(Font.font("Serif", 14));
        String selDefault = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE
                + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-text-fill: black; -fx-padding: 10 20; -fx-cursor: hand;";
        String selHover = "-fx-background-color: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE
                + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand;";
        selectBtn.setStyle(selDefault);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(selHover));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(selDefault));
        selectBtn.setOnAction(e -> DeckDetailPanel.show(mainLayout, dd));

        row.getChildren().addAll(nameInfo, stats, selectBtn);
        return row;
    }
}