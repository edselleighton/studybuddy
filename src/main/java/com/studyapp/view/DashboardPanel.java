package com.studyapp.view;

import java.util.List;
import java.util.Map;

import com.studyapp.controller.DeckController;
import com.studyapp.controller.ProgressController;
import com.studyapp.controller.StudyController;
import com.studyapp.data.AppContext;
import com.studyapp.model.Deck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle; // Added import for custom legend dots
import javafx.scene.text.Font;

public class DashboardPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    
    // Explicit color mapping
    private static final String EASY_PIE_COLOR = "#16a34a";   // Green
    private static final String MEDIUM_PIE_COLOR = "#d97706"; // Orange
    private static final String HARD_PIE_COLOR = "#dc2626";   // Red

    public static VBox create(BorderPane mainLayout) {
        DeckController deckController = AppContext.decks();
        ProgressController progressController = AppContext.progress();
        StudyController studyController = AppContext.study();

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label dashHeader = createHeaderLabel("Dashboard");

        HBox statsRow = new HBox(20);
        VBox stat1 = createStatCard("Accuracy", progressController.getAccuracyLabel());
        VBox stat2 = createStatCard("Cards Reviewed", progressController.getReviewedCardsLabel());
        VBox stat3 = createStatCard("Study Time", progressController.getStudyTimeLabel());
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);
        statsRow.getChildren().addAll(stat1, stat2, stat3);

        HBox bottomContent = new HBox(20);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox recentDecks = new VBox(15);
        HBox.setHgrow(recentDecks, Priority.ALWAYS);

        Label recentHeader = new Label("Recent Decks");
        recentHeader.setFont(Font.font("Serif", 22));
        recentHeader.setTextFill(Color.web(PRIMARY_BLUE));

        VBox deckList = new VBox(15);
        List<Deck> recent = deckController.getRecentDecks(3);
        for (Deck deck : recent) {
            deckList.getChildren().add(createDeckItem(mainLayout, deck));
        }
        recentDecks.getChildren().addAll(recentHeader, deckList);

        VBox chartBox = new VBox(15); // Increased spacing slightly for legend
        chartBox.setStyle(BORDER_STYLE);
        chartBox.setPadding(new Insets(15));
        chartBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(chartBox, Priority.SOMETIMES);
        chartBox.setMinWidth(350);

        Label chartTitle = new Label("Total Flashcards");
        chartTitle.setFont(Font.font("SansSerif", 16));
        chartTitle.setTextFill(Color.BLACK);

        Map<String, Integer> breakdown = studyController.getDifficultyBreakdown();
        PieChart.Data easyData = new PieChart.Data("Easy", breakdown.getOrDefault("Easy", 0));
        PieChart.Data mediumData = new PieChart.Data("Medium", breakdown.getOrDefault("Medium", 0));
        PieChart.Data hardData = new PieChart.Data("Hard", breakdown.getOrDefault("Hard", 0));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                easyData, mediumData, hardData
        );

        // Keep this to color the actual pie slices dynamically
        bindPieSliceColor(easyData, EASY_PIE_COLOR);
        bindPieSliceColor(mediumData, MEDIUM_PIE_COLOR);
        bindPieSliceColor(hardData, HARD_PIE_COLOR);

        // --- THE FIX: Custom Legend ---
        HBox customLegend = new HBox(15);
        customLegend.setAlignment(Pos.CENTER);
        customLegend.getChildren().addAll(
            createLegendItem("Easy", EASY_PIE_COLOR),
            createLegendItem("Medium", MEDIUM_PIE_COLOR),
            createLegendItem("Hard", HARD_PIE_COLOR)
        );

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false); // Turn off the buggy native legend entirely
        chart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(chart, Priority.ALWAYS);

        // Add Title, Custom Legend, and Chart
        chartBox.getChildren().addAll(chartTitle, customLegend, chart);
        
        bottomContent.getChildren().addAll(recentDecks, chartBox);
        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    // --- Chart Color Helpers ---

    private static void bindPieSliceColor(PieChart.Data data, String color) {
        data.nodeProperty().addListener((observable, oldNode, newNode) -> applyPieSliceColor(newNode, color));
        applyPieSliceColor(data.getNode(), color);
    }

    private static void applyPieSliceColor(Node node, String color) {
        if (node != null) {
            node.setStyle("-fx-pie-color: " + color + ";");
        }
    }

    // Custom Legend Builder
    private static HBox createLegendItem(String name, String colorHex) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);
        
        Circle dot = new Circle(6, Color.web(colorHex));
        
        Label lbl = new Label(name);
        lbl.setFont(Font.font("SansSerif", 13));
        lbl.setTextFill(Color.web("#333333"));
        
        item.getChildren().addAll(dot, lbl);
        return item;
    }

    // --- UI Helpers ---

    private static Label createHeaderLabel(String text) {
        Label header = new Label(text);
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");
        return header;
    }

    private static VBox createStatCard(String title, String value) {
        VBox box = new VBox(5);
        box.setStyle(BORDER_STYLE);
        box.setPadding(new Insets(15, 20, 15, 20));
        box.setMaxWidth(Double.MAX_VALUE);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Serif", 18));
        titleLbl.setTextFill(Color.BLACK);

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Serif", 22));
        valLbl.setTextFill(Color.web(PRIMARY_BLUE));

        box.getChildren().addAll(titleLbl, valLbl);
        return box;
    }

    private static Label createDeckItem(BorderPane mainLayout, Deck deck) {
        Label lbl = new Label(deck.getName());
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 15 20 15 20; -fx-cursor: hand;");
        lbl.setFont(Font.font("Serif", 16));
        lbl.setTextFill(Color.BLACK);
        
        lbl.setOnMouseEntered(e -> lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: #f0f4f8; -fx-padding: 15 20 15 20; -fx-cursor: hand;"));
        lbl.setOnMouseExited(e -> lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 15 20 15 20; -fx-cursor: hand;"));
        
        lbl.setOnMouseClicked(e -> {
            VBox originalSidebar = (VBox) mainLayout.getLeft();
            DeckDetailPanel.show(mainLayout, originalSidebar, deck);
        });
        return lbl;
    }
}
