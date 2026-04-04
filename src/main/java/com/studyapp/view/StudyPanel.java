package com.studyapp.view;

import java.util.List;

import com.studyapp.data.AppContext;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

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
        return create(mainLayout, null);
    }

    public static VBox create(BorderPane mainLayout, Deck deck) {
        List<Flashcard> cards = AppContext.study().getCards(deck);
        Flashcard currentCard = cards.isEmpty() ? null : cards.get(0);

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        mainContent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        String title = deck == null ? "Study Session" : "Study Session: " + deck.getName();
        Label header = new Label(title);
        header.setFont(Font.font("System", FontWeight.BOLD, 32));
        header.setTextFill(Color.web(TEXT_MAIN));
        header.setMaxWidth(Double.MAX_VALUE);

        VBox flashcard = new VBox(20);
        flashcard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 50;");
        flashcard.setAlignment(Pos.CENTER);
        flashcard.setMaxWidth(600);
        flashcard.setMinHeight(300);

        Label question = new Label(currentCard == null ? "No cards available for this study session yet." : currentCard.getQuestion());
        question.setFont(Font.font("System", 24));
        question.setWrapText(true);

        Label answer = new Label(currentCard == null ? "" : "Answer: " + currentCard.getAnswer());
        answer.setFont(Font.font("System", 16));
        answer.setTextFill(Color.web("#475569"));
        answer.setWrapText(true);

        flashcard.getChildren().addAll(question, answer);

        Button leaveBtn = new Button("Leave Study Session");
        leaveBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        leaveBtn.setOnAction(e -> mainLayout.setCenter(AllCardsPanel.create(mainLayout, deck)));

        mainContent.getChildren().addAll(header, flashcard, leaveBtn);
        return mainContent;
    }
}
