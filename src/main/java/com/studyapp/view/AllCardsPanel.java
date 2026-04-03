package com.studyapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AllCardsPanel {

    private static final String BG_COLOR = "#f8fafc";
    private static final String TEXT_MAIN = "#1e293b";
    private static final String TEXT_MUTED = "#64748b";

    public static VBox create(BorderPane mainLayout) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40));
        mainContent.setSpacing(20);
        mainContent.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label header = new Label("All Cards");
        header.setFont(Font.font("System", FontWeight.BOLD, 32));
        header.setTextFill(Color.web(TEXT_MAIN));

        Label subtitle = new Label("Browse your cards and start a study session.");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web(TEXT_MUTED));

        VBox cardsBox = new VBox(12);
        cardsBox.getChildren().addAll(
                createCard("What does SQL stand for?"),
                createCard("What is a primary key?"),
                createCard("What does JDBC do?")
        );

        Button studyBtn = new Button("Start Study Session");
        studyBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        studyBtn.setOnAction(e -> mainLayout.setCenter(StudyPanel.create(mainLayout)));

        mainContent.getChildren().addAll(header, subtitle, cardsBox, studyBtn);
        return mainContent;
    }

    private static VBox createCard(String text) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));
        card.setSpacing(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        VBox.setVgrow(card, Priority.NEVER);

        Label question = new Label(text);
        question.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        question.setTextFill(Color.web(TEXT_MAIN));
        question.setWrapText(true);

        Region spacer = new Region();
        spacer.setMinHeight(2);

        card.getChildren().addAll(question, spacer);
        return card;
    }
}
