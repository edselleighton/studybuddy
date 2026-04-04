package com.studyapp.view;

import java.util.List;
import java.util.Map;

import com.studyapp.controller.DeckController;
import com.studyapp.controller.DeckController.DeckStats;
import com.studyapp.data.AppContext;
import com.studyapp.model.Deck;

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

    public static VBox create(BorderPane mainLayout) {
        DeckController deckController = AppContext.decks();
        List<Deck> decks = deckController.getAllDecks();
        Map<Integer, DeckStats> deckStats = deckController.getDeckStats();

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

        Button newBtn = new Button("New");
        newBtn.setStyle("-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-text-fill: black; -fx-padding: 5 20; -fx-cursor: hand;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search decks");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");

        Label searchIcon = new Label("??");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(Font.font("Serif", 16));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Name");
        sortCombo.setValue("Newest");
        sortCombo.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");
        sortCombo.setPrefWidth(120);

        toolbar.getChildren().addAll(newBtn, searchField, searchIcon, spacer, sortLabel, sortCombo);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deckList = new VBox(15);
        deckList.setPadding(new Insets(5, 15, 5, 5));
        deckList.setStyle("-fx-background-color: white;");

        for (Deck deck : decks) {
            deckList.getChildren().add(createDeckItem(mainLayout, deck, deckStats.getOrDefault(deck.getDeckID(), new DeckStats(0, 0))));
        }

        scrollPane.setContent(deckList);
        mainContent.getChildren().addAll(header, toolbar, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private static HBox createDeckItem(BorderPane mainLayout, Deck deck, DeckStats stats) {
        HBox row = new HBox(20);
        row.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 15;");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox leftInfo = new VBox(5);
        leftInfo.setPrefWidth(250);
        Label idLbl = new Label("ID: " + deck.getDeckID());
        idLbl.setFont(Font.font("Serif", 14));
        Label titleLbl = new Label(deck.getName());
        titleLbl.setFont(Font.font("Serif", 18));
        leftInfo.getChildren().addAll(idLbl, titleLbl);

        VBox middleInfo = new VBox(5);
        Label cardsLbl = new Label("Cards: " + stats.getCardCount());
        cardsLbl.setFont(Font.font("Serif", 14));
        Label progLbl = new Label(String.format("Progress: %.0f%%", stats.getProgressPercent()));
        progLbl.setFont(Font.font("Serif", 14));
        middleInfo.getChildren().addAll(cardsLbl, progLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("SELECT");
        String defaultStyle = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
        selectBtn.setStyle(defaultStyle);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(hoverStyle));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(defaultStyle));
        selectBtn.setOnAction(e -> {
            VBox originalSidebar = (VBox) mainLayout.getLeft();
            DeckDetailPanel.show(mainLayout, originalSidebar, deck);
        });

        row.getChildren().addAll(leftInfo, middleInfo, spacer, selectBtn);
        return row;
    }
}
