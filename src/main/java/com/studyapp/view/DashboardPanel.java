package com.studyapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";

    public static VBox create() {

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
        
        VBox stat1 = createStatCard("Accuracy", "92.3%");
        VBox stat2 = createStatCard("Cards Reviewed", "112 / 150");
        VBox stat3 = createStatCard("Study Time", "1 hr 12m");
        
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
        deckList.getChildren().addAll(
                createDeckItem("CMSC127 - LE2"),
                createDeckItem("MATH55 - LE4"),
                createDeckItem("BIOLOGY - QUIZ")
        );

        recentDecks.getChildren().addAll(recentHeader, deckList);

        VBox chartBox = new VBox(5);
        chartBox.setStyle(BORDER_STYLE);
        chartBox.setPadding(new Insets(10));
        chartBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(chartBox, Priority.SOMETIMES);
        chartBox.setMinWidth(350); 

        Label chartTitle = new Label("Total Flashcards");
        chartTitle.setFont(Font.font("SansSerif", 14));
        chartTitle.setTextFill(Color.BLACK);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Easy", 40),
                new PieChart.Data("Medium", 70),
                new PieChart.Data("Hard", 30)
        );

        PieChart chart = new PieChart(pieChartData);
        chart.setLabelsVisible(false);
        chart.setLegendSide(javafx.geometry.Side.TOP);
        chart.setStyle("-fx-background-color: transparent;"); 
        
        VBox.setVgrow(chart, Priority.ALWAYS);

        chartBox.getChildren().addAll(chartTitle, chart);

        bottomContent.getChildren().addAll(recentDecks, chartBox);
        mainContent.getChildren().addAll(dashHeader, statsRow, bottomContent);
        wrapper.getChildren().add(mainContent);

        return wrapper;
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

    private static Label createDeckItem(String name) {
        Label lbl = new Label(name);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 15 20 15 20; -fx-cursor: hand;");
        lbl.setFont(Font.font("Serif", 16));
        lbl.setTextFill(Color.BLACK);

        lbl.setOnMouseEntered(e -> 
            lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: #f0f4f8; -fx-padding: 15 20 15 20; -fx-cursor: hand;")
        );
        lbl.setOnMouseExited(e -> 
            lbl.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white; -fx-padding: 15 20 15 20; -fx-cursor: hand;")
        );

        return lbl;
    }
}
