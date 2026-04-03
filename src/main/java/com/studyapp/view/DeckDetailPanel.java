package com.studyapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DeckDetailPanel {

    private static final String BG_COLOR = "#f8fafc";
    private static final String TEXT_MAIN = "#1e293b";

    public static VBox create() {
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("Deck Details");
        header.setFont(Font.font("System", FontWeight.BOLD, 32));
        header.setTextFill(Color.web(TEXT_MAIN));
        header.setAlignment(Pos.CENTER_LEFT);

        // Placeholder content
        VBox contentBox = new VBox(20);
        contentBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 30;");
        contentBox.getChildren().add(new Label("Your deck management UI will go here."));

        mainContent.getChildren().addAll(header, contentBox);

        return mainContent;
    }
}