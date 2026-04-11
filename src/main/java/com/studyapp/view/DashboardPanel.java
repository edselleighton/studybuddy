package com.studyapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
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
        statsRow.getChildren().addAll(
                createStatCard("Accuracy", "92%"),
                createStatCard("Cards Reviewed", "148"),
                createStatCard("Study Time", "5.4 hrs"));
        for (Node node : statsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        HBox bottomContent = new HBox(20);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox flowPanel = new VBox(15);
        flowPanel.setStyle(BORDER_STYLE);
        flowPanel.setPadding(new Insets(18));
        flowPanel.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(flowPanel, Priority.ALWAYS);

        Label flowHeader = new Label("Prototype Flow");
        flowHeader.setFont(Font.font("Serif", 22));
        flowHeader.setTextFill(Color.web(PRIMARY_BLUE));

        Label flowText = new Label("This branch keeps the navigation path: Setup Panel to Dashboard to My Cards to All Cards.");
        flowText.setFont(Font.font("Serif", 16));
        flowText.setWrapText(true);
        flowText.setTextFill(Color.web("#475569"));

        VBox flowSteps = new VBox(10);
        flowSteps.getChildren().addAll(
                createFlowStep("1", "Setup Panel"),
                createFlowStep("2", "Dashboard"),
                createFlowStep("3", "My Cards"),
                createFlowStep("4", "All Cards"));

                Button nextButton = new Button("Open My Decks");
                String buttonStyle = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 20; -fx-cursor: hand;";
                String hoverStyle = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 20; -fx-cursor: hand;";
                nextButton.setStyle(buttonStyle);
                nextButton.setOnMouseEntered(e -> nextButton.setStyle(hoverStyle));
                nextButton.setOnMouseExited(e -> nextButton.setStyle(buttonStyle));
                nextButton.setOnAction(e -> {
                    MainFrame.activateMyDecks();
                    mainLayout.setCenter(MyDeckPanel.create(mainLayout));
                });

        flowPanel.getChildren().addAll(flowHeader, flowText, flowSteps, nextButton);

        VBox chartBox = new VBox(15);
        chartBox.setStyle(BORDER_STYLE);
        chartBox.setPadding(new Insets(15));
        chartBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(chartBox, Priority.SOMETIMES);
        chartBox.setMinWidth(350);

        Label chartTitle = new Label("Card Difficulty Mix");
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

        bottomContent.getChildren().addAll(flowPanel, chartBox);
        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static HBox createFlowStep(String stepNumber, String label) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox markerWrap = new VBox(new Label(stepNumber));
        markerWrap.setAlignment(Pos.CENTER);
        markerWrap.setPrefSize(24, 24);
        markerWrap.setMaxSize(24, 24);
        markerWrap.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 999;");
        ((Label) markerWrap.getChildren().get(0)).setTextFill(Color.WHITE);
        ((Label) markerWrap.getChildren().get(0)).setFont(Font.font("SansSerif", 12));

        Label text = new Label(label);
        text.setFont(Font.font("Serif", 16));
        text.setTextFill(Color.BLACK);

        row.getChildren().addAll(markerWrap, text);
        return row;
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
}
