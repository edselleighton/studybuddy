package com.studyapp.view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.studyapp.controller.DeckController;
import com.studyapp.data.AppContext;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DeckDetailPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String BTN_DEFAULT = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
    private static final String BTN_HOVER = "-fx-background-color: #f0f4f8; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void show(BorderPane mainLayout, VBox originalSidebar, Deck deck) {
        DeckController deckController = AppContext.decks();
        List<Flashcard> previewCards = deckController.getPreviewCards(deck.getDeckID(), 4);
        int cardCount = deckController.getCardCount(deck.getDeckID());
        double progress = deckController.getProgressPercent(deck.getDeckID());

        VBox deckSidebar = new VBox(15);
        deckSidebar.setPadding(new Insets(20, 20, 20, 20));
        deckSidebar.setPrefWidth(250);
        deckSidebar.setMinWidth(250);
        deckSidebar.setMaxWidth(250);
        deckSidebar.setStyle("-fx-background-color: transparent;");

        Label appTitleLabel = new Label("Study Assistant\nApplication");
        appTitleLabel.setFont(Font.font("Serif", 18));
        appTitleLabel.setTextFill(Color.web(PRIMARY_BLUE));
        VBox.setMargin(appTitleLabel, new Insets(0, 0, 10, 0));

        VBox buttonBox = new VBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle(BORDER_STYLE);
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        Button editBtn = createSidebarBtn("Edit");
        Button cardsBtn = createSidebarBtn("Cards");
        Button studyBtn = createSidebarBtn("Study");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setFont(Font.font("Serif", 16));
        String delDefault = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ff9999; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        String delHover = "-fx-background-color: #ffe6e6; -fx-text-fill: #cc0000; -fx-border-color: #ff9999; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        deleteBtn.setStyle(delDefault);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(delHover));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(delDefault));

        Button backBtn = new Button("BACK");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setFont(Font.font("Serif", 16));
        String backDefault = "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        String backHover = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        backBtn.setStyle(backDefault);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backDefault));
        backBtn.setOnAction(e -> {
            mainLayout.setLeft(originalSidebar);
            mainLayout.setCenter(MyDeckPanel.create(mainLayout));
        });

        cardsBtn.setOnAction(e -> mainLayout.setCenter(AllCardsPanel.create(mainLayout, deck)));
        studyBtn.setOnAction(e -> mainLayout.setCenter(StudyPanel.create(mainLayout, deck)));

        buttonBox.getChildren().addAll(editBtn, cardsBtn, studyBtn, spacer, deleteBtn, backBtn);
        deckSidebar.getChildren().addAll(appTitleLabel, buttonBox);

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label(deck.getName());
        header.setFont(Font.font("Serif", 36));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 10; -fx-padding: 15;");

        HBox infoBox = new HBox(60);
        infoBox.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-padding: 20;");

        VBox leftInfo = new VBox(10);
        leftInfo.getChildren().addAll(
                createDetailLabel("ID: " + deck.getDeckID()),
                createDetailLabel("Cards: " + cardCount),
                createDetailLabel("Created at: " + formatCreatedAt(deck))
        );

        VBox rightInfo = new VBox(10);
        rightInfo.getChildren().addAll(
                createDetailLabel("Description:"),
                createDetailLabel(deck.getDescription() == null || deck.getDescription().isBlank() ? "No description available." : deck.getDescription())
        );

        infoBox.getChildren().addAll(leftInfo, rightInfo);

        VBox progressSection = new VBox(10);
        Label progLabel = new Label("Progress:");
        progLabel.setFont(Font.font("Serif", 20));

        StackPane barPane = new StackPane();
        barPane.setAlignment(Pos.CENTER_LEFT);

        Rectangle bgRect = new Rectangle();
        bgRect.setHeight(45);
        bgRect.setArcWidth(45);
        bgRect.setArcHeight(45);
        bgRect.setFill(Color.web("#e6eaf5"));
        bgRect.widthProperty().bind(mainContent.widthProperty().subtract(60));

        Rectangle fillRect = new Rectangle();
        fillRect.setHeight(45);
        fillRect.setArcWidth(45);
        fillRect.setArcHeight(45);
        fillRect.setFill(Color.web(HEADER_BLUE));
        fillRect.widthProperty().bind(bgRect.widthProperty().multiply(progress / 100.0));

        Label pctLabel = new Label(String.format("%.0f%%", progress));
        pctLabel.setTextFill(Color.WHITE);
        pctLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        StackPane.setAlignment(fillRect, Pos.CENTER_LEFT);
        StackPane.setAlignment(pctLabel, Pos.CENTER);
        barPane.getChildren().addAll(bgRect, fillRect, pctLabel);

        progressSection.getChildren().addAll(progLabel, barPane);
        progressSection.setAlignment(Pos.CENTER_LEFT);

        VBox previewSection = new VBox(15);
        previewSection.setAlignment(Pos.CENTER);

        Label previewTitle = new Label("Cards Preview");
        previewTitle.setFont(Font.font("Serif", 24));

        GridPane previewGrid = new GridPane();
        previewGrid.setHgap(20);
        previewGrid.setVgap(15);
        previewGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < previewCards.size(); i++) {
            Flashcard card = previewCards.get(i);
            previewGrid.add(createPreviewCard(card.getQuestion()), i % 2, i / 2);
        }

        previewSection.getChildren().addAll(previewTitle, previewGrid);
        mainContent.getChildren().addAll(header, infoBox, progressSection, previewSection);
        wrapper.getChildren().add(mainContent);

        mainLayout.setLeft(deckSidebar);
        mainLayout.setCenter(wrapper);
    }

    private static String formatCreatedAt(Deck deck) {
        return deck.getCreatedAt() == null ? "Unknown" : DATE_FORMAT.format(deck.getCreatedAt());
    }

    private static Button createSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Serif", 16));
        btn.setStyle(BTN_DEFAULT);
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_HOVER));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_DEFAULT));
        return btn;
    }

    private static Label createDetailLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 16));
        lbl.setWrapText(true);
        return lbl;
    }

    private static Label createPreviewCard(String text) {
        Label card = new Label(text);
        card.setPrefWidth(450);
        card.setStyle("-fx-border-color: " + HEADER_BLUE + "; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: white;");
        card.setFont(Font.font("Serif", 16));
        card.setWrapText(true);
        return card;
    }
}
