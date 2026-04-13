package com.studyapp.view;

import java.util.List;

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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class DashboardPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String EASY_PIE_COLOR = "#16a34a";
    private static final String MEDIUM_PIE_COLOR = "#d97706";
    private static final String HARD_PIE_COLOR = "#dc2626";
    private static final String DECK_ITEM_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 15 20 15 20; -fx-cursor: hand;";
    private static final String DECK_ITEM_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: #f0f4f8; -fx-padding: 15 20 15 20; -fx-cursor: hand;";

    public static VBox create(BorderPane mainLayout) {
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
        VBox stat1 = createStatCard("Accuracy", "92%");
        VBox stat2 = createStatCard("Cards Reviewed", "148");
        VBox stat3 = createStatCard("Study Time", "5.4 hrs");
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
        List<String> recent = List.of("Java Foundations", "SQL Essentials", "UI Navigation");
        for (String deckName : recent) {
            deckList.getChildren().add(createDeckItem(deckName));
        }
        recentDecks.getChildren().addAll(recentHeader, deckList);

        VBox chartBox = new VBox(15);
        chartBox.setStyle(BORDER_STYLE);
        chartBox.setPadding(new Insets(15));
        chartBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(chartBox, Priority.SOMETIMES);
        chartBox.setMinWidth(350);

        Label chartTitle = new Label("Total Flashcards");
        chartTitle.setFont(Font.font("SansSerif", 16));
        chartTitle.setTextFill(Color.BLACK);

        PieChart.Data easyData = new PieChart.Data("Easy", 48);
        PieChart.Data mediumData = new PieChart.Data("Medium", 31);
        PieChart.Data hardData = new PieChart.Data("Hard", 21);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(easyData, mediumData, hardData);
        bindPieSliceColor(easyData, EASY_PIE_COLOR);
        bindPieSliceColor(mediumData, MEDIUM_PIE_COLOR);
        bindPieSliceColor(hardData, HARD_PIE_COLOR);

        HBox customLegend = new HBox(15);
        customLegend.setAlignment(Pos.CENTER);
        customLegend.getChildren().addAll(
                createLegendItem("Easy", EASY_PIE_COLOR),
                createLegendItem("Medium", MEDIUM_PIE_COLOR),
                createLegendItem("Hard", HARD_PIE_COLOR));

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(chart, Priority.ALWAYS);

        chartBox.getChildren().addAll(chartTitle, customLegend, chart);

        bottomContent.getChildren().addAll(recentDecks, chartBox);
        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);
        wrapper.getChildren().add(mainContent);

        return wrapper;
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
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);

        Circle dot = new Circle(6, Color.web(colorHex));

        Label lbl = new Label(name);
        lbl.setFont(Font.font("SansSerif", 13));
        lbl.setTextFill(Color.web("#333333"));

        item.getChildren().addAll(dot, lbl);
        return item;
    }

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

    private static Label createDeckItem(String deckName) {
        Label lbl = new Label(deckName);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle(DECK_ITEM_STYLE);
        lbl.setFont(Font.font("Serif", 16));
        lbl.setTextFill(Color.BLACK);
        lbl.setOnMouseEntered(e -> lbl.setStyle(DECK_ITEM_HOVER_STYLE));
        lbl.setOnMouseExited(e -> lbl.setStyle(DECK_ITEM_STYLE));
        return lbl;
    }
}
