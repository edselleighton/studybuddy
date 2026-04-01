package com.studyapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardPanel {

    // Modern Color Palette
    private static final String BG_COLOR = "#f8fafc";       // Light grayish-blue background
    private static final String ACCENT_COLOR = "#4f46e5";   // Modern Indigo
    private static final String TEXT_MAIN = "#1e293b";      // Dark Slate
    private static final String TEXT_MUTED = "#64748b";     // Light Slate
    
    // Base card style
    private static final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 12;";

    public static VBox create() {

        VBox mainContent = new VBox(30); // Spacing between rows
        mainContent.setPadding(new Insets(40)); // Outer padding
        mainContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        // Ensure the main container fills the space provided by the BorderPane
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label dashHeader = createHeaderLabel("Dashboard");

        // --- Stats Row ---
        HBox statsRow = new HBox(20);
        
        VBox stat1 = createStatCard("Accuracy", "92.3%");
        VBox stat2 = createStatCard("Cards Reviewed", "112 / 150");
        VBox stat3 = createStatCard("Study Time", "1h 12m");
        
        // Make stat cards stretch evenly across the width
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);
        
        statsRow.getChildren().addAll(stat1, stat2, stat3);

        // --- Bottom Content Area ---
        HBox bottomContent = new HBox(30);
        VBox.setVgrow(bottomContent, Priority.ALWAYS); // Let bottom content stretch vertically

        // 1. Recent Decks Column
        VBox recentDecksWrapper = new VBox(20);
        recentDecksWrapper.setStyle(CARD_STYLE);
        recentDecksWrapper.setPadding(new Insets(25));
        recentDecksWrapper.setEffect(createSubtleShadow());
        
        // Let the decks wrapper take up available horizontal space
        HBox.setHgrow(recentDecksWrapper, Priority.ALWAYS);

        Label recentHeader = new Label("Recent Decks");
        recentHeader.setFont(Font.font("System", FontWeight.BOLD, 18));
        recentHeader.setTextFill(Color.web(TEXT_MAIN));

        VBox deckList = new VBox(15);
        deckList.getChildren().addAll(
                createDeckItem("CMSC127 - LE2", "40 Cards"),
                createDeckItem("MATH55 - LE4", "35 Cards"),
                createDeckItem("BIOLOGY - QUIZ", "25 Cards")
        );

        recentDecksWrapper.getChildren().addAll(recentHeader, deckList);

        // 2. Chart Column
        VBox chartBox = new VBox(15);
        chartBox.setStyle(CARD_STYLE);
        chartBox.setPadding(new Insets(25));
        chartBox.setAlignment(Pos.TOP_CENTER);
        chartBox.setEffect(createSubtleShadow());
        
        // Set chart to take up a proportional amount of space (e.g., slightly smaller than decks list)
        HBox.setHgrow(chartBox, Priority.SOMETIMES);
        chartBox.setMinWidth(400); // Prevent it from getting squished too much

        Label chartTitle = new Label("Card Distribution");
        chartTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        chartTitle.setTextFill(Color.web(TEXT_MAIN));
        
        Label chartSubtitle = new Label("Total Flashcards: 140");
        chartSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        chartSubtitle.setTextFill(Color.web(TEXT_MUTED));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Easy", 40),
                new PieChart.Data("Medium", 70),
                new PieChart.Data("Hard", 30)
        );

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(false);
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        chart.setStyle("-fx-background-color: transparent;"); 
        
        // Let chart expand vertically to fill the card
        VBox.setVgrow(chart, Priority.ALWAYS);

        chartBox.getChildren().addAll(chartTitle, chartSubtitle, chart);

        bottomContent.getChildren().addAll(recentDecksWrapper, chartBox);

        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);

        return mainContent;
    }

    // --- UI Helper Methods ---

    private static Label createHeaderLabel(String text) {
        Label header = new Label(text);
        header.setFont(Font.font("System", FontWeight.BOLD, 32)); // Slightly larger for full screen
        header.setTextFill(Color.web(TEXT_MAIN));
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER_LEFT); 
        return header;
    }

    private static VBox createStatCard(String title, String value) {
        VBox box = new VBox(10);
        box.setStyle(CARD_STYLE);
        box.setPadding(new Insets(25));
        box.setEffect(createSubtleShadow());
        // Max width ensures they don't look awkwardly long on ultra-wide monitors
        box.setMaxWidth(Double.MAX_VALUE); 

        Label titleLbl = new Label(title.toUpperCase());
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        titleLbl.setTextFill(Color.web(TEXT_MUTED));

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("System", FontWeight.BOLD, 28)); // Larger number
        valLbl.setTextFill(Color.web(ACCENT_COLOR));

        box.getChildren().addAll(titleLbl, valLbl);
        return box;
    }

    private static HBox createDeckItem(String name, String subText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 15 20 15 20; -fx-cursor: hand;");
        
        VBox textData = new VBox(5);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLbl.setTextFill(Color.web(TEXT_MAIN));
        
        Label subLbl = new Label(subText);
        subLbl.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subLbl.setTextFill(Color.web(TEXT_MUTED));
        
        textData.getChildren().addAll(nameLbl, subLbl);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(textData, spacer);

        row.setOnMouseEntered(e -> 
            row.setStyle("-fx-background-color: #eef2ff; -fx-background-radius: 8; -fx-padding: 15 20 15 20; -fx-cursor: hand;")
        );
        row.setOnMouseExited(e -> 
            row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 15 20 15 20; -fx-cursor: hand;")
        );

        return row;
    }

    private static DropShadow createSubtleShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04)); 
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        return shadow;
    }
}