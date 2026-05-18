package com.studyapp.view;

import com.studyapp.util.UiScale;

import java.util.List;

import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DashboardPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final Insets PAGE_PADDING = new Insets(12);
    private static final Insets CONTENT_PADDING = new Insets(14);
    private static final int CONTENT_SPACING = 12;
    private static final String EASY_PIE_COLOR = "#16a34a";
    private static final String MEDIUM_PIE_COLOR = "#d97706";
    private static final String HARD_PIE_COLOR = "#dc2626";
    private static final String DECK_ITEM_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: white; -fx-padding: 20 22; -fx-cursor: hand;";
    private static final String DECK_ITEM_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 20 22; -fx-cursor: hand;";

    public static VBox create(BorderPane mainLayout, MainController mainController) {
        DashboardStats stats = loadStats(mainController);

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

        Label dashHeader = createHeaderLabel("Dashboard");

        HBox statsRow = new HBox(18);
        VBox stat1 = createStatCard("Accuracy", stats.accuracy());
        VBox stat2 = createStatCard("Cards Reviewed", stats.cardsReviewed());
        VBox stat3 = createStatCard("Study Time", stats.studyTime());
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);
        statsRow.getChildren().addAll(stat1, stat2, stat3);

        HBox bottomContent = new HBox(24);
        bottomContent.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox recentDecks = new VBox(16);
        HBox.setHgrow(recentDecks, Priority.ALWAYS);

        Label recentHeader = new Label("Recent Decks");
        recentHeader.setFont(UiScale.headingFont(42));
        recentHeader.setTextFill(Color.web(PRIMARY_BLUE));

        VBox deckList = new VBox(16);
        if (stats.recentDecks().isEmpty()) {
            Label emptyState = new Label("No decks available yet.");
            emptyState.setFont(UiScale.bodyFont(22));
            emptyState.setTextFill(Color.web("#475569"));
            deckList.getChildren().add(emptyState);
        } else {
            for (Deck deck : stats.recentDecks()) {
                deckList.getChildren().add(createDeckItem(mainLayout, deck, mainController));
            }
        }
        recentDecks.getChildren().addAll(recentHeader, deckList);

        VBox chartBox = new VBox(15);
        chartBox.setStyle(BORDER_STYLE);
        chartBox.setPadding(new Insets(15));
        chartBox.setAlignment(Pos.TOP_CENTER);
        chartBox.setMinWidth(UiScale.size(520));
        chartBox.setPrefWidth(UiScale.size(560));
        chartBox.setMaxWidth(UiScale.size(600));
        chartBox.setMaxHeight(UiScale.size(540));

        Label chartTitle = new Label("Card Difficulty Mix");
        chartTitle.setFont(UiScale.headingFont(36));
        chartTitle.setTextFill(Color.web(PRIMARY_BLUE));

        PieChart.Data easyData = new PieChart.Data("Easy", stats.easyCount() > 0 ? stats.easyCount() : 0.001);
        PieChart.Data mediumData = new PieChart.Data("Medium", stats.mediumCount() > 0 ? stats.mediumCount() : 0.001);
        PieChart.Data hardData = new PieChart.Data("Hard", stats.hardCount() > 0 ? stats.hardCount() : 0.001);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(easyData, mediumData, hardData);
        bindPieSliceColor(easyData, EASY_PIE_COLOR);
        bindPieSliceColor(mediumData, MEDIUM_PIE_COLOR);
        bindPieSliceColor(hardData, HARD_PIE_COLOR);

        HBox customLegend = new HBox(18);
        customLegend.setAlignment(Pos.CENTER);
        customLegend.getChildren().addAll(
                createLegendItem("Easy", EASY_PIE_COLOR),
                createLegendItem("Medium", MEDIUM_PIE_COLOR),
                createLegendItem("Hard", HARD_PIE_COLOR));

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setPrefSize(UiScale.size(460), UiScale.size(380));
        chart.setMaxSize(UiScale.size(480), UiScale.size(400));
        VBox.setVgrow(chart, Priority.NEVER);

        chartBox.getChildren().addAll(chartTitle, customLegend, chart);

        bottomContent.getChildren().addAll(recentDecks, chartBox);
        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private record DashboardStats(
            String accuracy,
            String cardsReviewed,
            String studyTime,
            List<Deck> recentDecks,
            int easyCount,
            int mediumCount,
            int hardCount) {
    }

    private static DashboardStats loadStats(MainController mainController) {
        int reviewCount = mainController.getAllCardReviews().size();
        String accuracy = reviewCount == 0 ? "--" : mainController.getAccuracy() + "%";

        return new DashboardStats(
                accuracy,
                mainController.getCardsReviewedProgress(),
                mainController.getTotalStudyTime(),
                mainController.getRecentDecks(),
                mainController.getEasyFlashcards().size(),
                mainController.getMediumFlashcards().size(),
                mainController.getHardFlashcards().size());
    }

    private static void bindPieSliceColor(PieChart.Data data, String color) {
        data.nodeProperty().addListener((observable, oldNode, newNode) -> applyPieSliceColor(newNode, color));
        applyPieSliceColor(data.getNode(), color);
    }

    private static void applyPieSliceColor(Node node, String color) {
        if (node != null) {
            node.setStyle("-fx-pie-color: " + color + ";");
        }
    }

    private static HBox createLegendItem(String name, String colorHex) {
        HBox item = new HBox(7);
        item.setAlignment(Pos.CENTER);

        Circle dot = new Circle(UiScale.size(8), Color.web(colorHex));

        Label lbl = new Label(name);
        lbl.setFont(UiScale.bodyFont(16));
        lbl.setTextFill(Color.web("#333333"));

        item.getChildren().addAll(dot, lbl);
        return item;
    }

    private static Label createHeaderLabel(String text) {
        Label header = new Label(text);
        header.setFont(UiScale.titleFont(64));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");
        return header;
    }

    private static VBox createStatCard(String title, String value) {
        VBox box = new VBox(8);
        box.setStyle(BORDER_STYLE);
        box.setPadding(UiScale.insets(18, 24, 18, 24));
        box.setMinHeight(UiScale.size(112));
        box.setMaxWidth(Double.MAX_VALUE);

        Label titleLbl = new Label(title);
        titleLbl.setFont(UiScale.headingFont(22));
        titleLbl.setTextFill(Color.BLACK);

        Label valLbl = new Label(value);
        valLbl.setFont(UiScale.emphasisFont(32));
        valLbl.setTextFill(Color.web(PRIMARY_BLUE));

        box.getChildren().addAll(titleLbl, valLbl);
        return box;
    }

    private static VBox createDeckItem(BorderPane mainLayout, Deck deck, MainController mc) {
        VBox item = new VBox(8);
        item.setMinHeight(UiScale.size(92));
        item.setMaxWidth(Double.MAX_VALUE);
        item.setStyle(DECK_ITEM_STYLE);

        Label title = new Label(deck.getName());
        title.setFont(UiScale.headingFont(24));
        title.setTextFill(Color.BLACK);
        title.setWrapText(true);

        Label details = new Label("Cards: " + mc.getFlashcardsByDeck(deck.getDeckID()).size()
                + "    Progress: " + mc.getDeckProgress(deck.getDeckID()) + "%");
        details.setFont(UiScale.bodyFont(18));
        details.setTextFill(Color.web(PRIMARY_BLUE));

        item.getChildren().addAll(title, details);
        item.setOnMouseEntered(e -> item.setStyle(DECK_ITEM_HOVER_STYLE));
        item.setOnMouseExited(e -> item.setStyle(DECK_ITEM_STYLE));
        item.setOnMouseClicked(e -> DeckDetailPanel.show(
                mainLayout,
                deck,
                mc,
                () -> mainLayout.setCenter(DashboardPanel.create(mainLayout, mc))));
        return item;
    }
}

