package com.studyapp.view;

import java.util.List;

import com.studyapp.data.AppContext;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AllCardsPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";

    public static VBox create(BorderPane mainLayout) {
        return create(mainLayout, null);
    }

    public static VBox create(BorderPane mainLayout, Deck deck) {
        List<Flashcard> cards = AppContext.study().getCards(deck);

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        String title = deck == null ? "All Cards" : deck.getName() + " Cards";
        Label header = new Label(title);
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        Button studyBtn = new Button("Start Study Session");
        studyBtn.setFont(Font.font("Serif", 16));
        studyBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 30; -fx-cursor: hand;");
        studyBtn.setOnMouseEntered(e -> studyBtn.setStyle("-fx-background-color: #eafbf1; -fx-text-fill: black; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 30; -fx-cursor: hand;"));
        studyBtn.setOnMouseExited(e -> studyBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 30; -fx-cursor: hand;"));
        studyBtn.setOnAction(e -> mainLayout.setCenter(StudyPanel.create(mainLayout, deck)));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cardsBox = new VBox(15);
        cardsBox.setPadding(new Insets(5, 15, 5, 5));
        cardsBox.setStyle("-fx-background-color: white;");

        for (Flashcard card : cards) {
            cardsBox.getChildren().add(createCard(card));
        }

        if (cards.isEmpty()) {
            cardsBox.getChildren().add(new Label("No cards available yet."));
        }

        scrollPane.setContent(cardsBox);
        mainContent.getChildren().addAll(header, studyBtn, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    private static VBox createCard(Flashcard flashcard) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));
        card.setSpacing(8);
        card.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-color: white;");
        VBox.setVgrow(card, Priority.NEVER);

        Label question = new Label(flashcard.getQuestion());
        question.setFont(Font.font("Serif", 18));
        question.setTextFill(Color.BLACK);
        question.setWrapText(true);

        Label answer = new Label("Answer: " + flashcard.getAnswer());
        answer.setFont(Font.font("Serif", 15));
        answer.setTextFill(Color.web("#475569"));
        answer.setWrapText(true);

        Label difficulty = new Label("Difficulty: " + flashcard.getDifficulty());
        difficulty.setFont(Font.font("Serif", 14));
        difficulty.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        spacer.setMinHeight(2);

        card.getChildren().addAll(question, answer, difficulty, spacer);
        return card;
    }
}
