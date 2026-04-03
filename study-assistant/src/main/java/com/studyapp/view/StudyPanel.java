package com.studyapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StudyPanel {

    private static final String BG_COLOR = "#f8fafc";
    private static final String TEXT_MAIN = "#1e293b";

    public static VBox create(BorderPane mainLayout) {
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        mainContent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("Study Session");
        header.setFont(Font.font("System", FontWeight.BOLD, 32));
        header.setTextFill(Color.web(TEXT_MAIN));
        header.setMaxWidth(Double.MAX_VALUE);

        // Flashcard Placeholder
        VBox flashcard = new VBox(20);
        flashcard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 50;");
        flashcard.setAlignment(Pos.CENTER);
        flashcard.setMaxWidth(600);
        flashcard.setMinHeight(300);
        
        Label question = new Label("What does SQL stand for?");
        question.setFont(Font.font("System", 24));
        
        flashcard.getChildren().add(question);

        // Return to all cards button
        Button leaveBtn = new Button("Leave Study Session");
        leaveBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        leaveBtn.setOnAction(e -> mainLayout.setCenter(AllCardsPanel.create(mainLayout)));

        mainContent.getChildren().addAll(header, flashcard, leaveBtn);

        return mainContent;
    }
}