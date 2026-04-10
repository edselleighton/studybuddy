package com.studyapp.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MyDeckPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String CARD_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: white; -fx-padding: 15;";

    public static VBox create(BorderPane mainLayout) {
        List<CardPreview> cards = List.of(
                new CardPreview("What is encapsulation?", "Bundling data and behavior inside one class.", "Easy"),
                new CardPreview("What does a LEFT JOIN return?", "All rows from the left table and matches from the right.", "Medium"),
                new CardPreview("Why isolate navigation in nav-ui?", "To focus on screen flow without live features.", "Easy"),
                new CardPreview("What is a BorderPane used for?", "A top-level layout for arranging app sections.", "Medium"));

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("My Cards");
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        Label helperLabel = new Label("This screen shows local preview cards only. Editing, searching, and studying are stripped out in this branch.");
        helperLabel.setFont(Font.font("Serif", 15));
        helperLabel.setTextFill(Color.web("#0f766e"));
        helperLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cardList = new VBox(15);
        cardList.setPadding(new Insets(5, 15, 5, 5));
        cardList.setStyle("-fx-background-color: white;");

        for (CardPreview card : cards) {
            cardList.getChildren().add(createPreviewCard(card));
        }

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button nextButton = new Button("Go To All Cards");
        String buttonStyle = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 20; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 20; -fx-cursor: hand;";
        nextButton.setStyle(buttonStyle);
        nextButton.setOnMouseEntered(e -> nextButton.setStyle(hoverStyle));
        nextButton.setOnMouseExited(e -> nextButton.setStyle(buttonStyle));
        nextButton.setOnAction(e -> mainLayout.setCenter(AllCardsPanel.create(mainLayout)));

        footer.getChildren().addAll(spacer, nextButton);

        scrollPane.setContent(cardList);
        mainContent.getChildren().addAll(header, helperLabel, scrollPane, footer);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    public static VBox create(BorderPane mainLayout, String statusMessage, String statusColor) {
        return create(mainLayout);
    }

    private static VBox createPreviewCard(CardPreview card) {
        VBox box = new VBox(8);
        box.setStyle(CARD_STYLE);

        Label titleLabel = new Label(card.question());
        titleLabel.setFont(Font.font("Serif", 18));
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setWrapText(true);

        Label answerLabel = new Label("Preview Answer: " + card.answer());
        answerLabel.setFont(Font.font("Serif", 14));
        answerLabel.setTextFill(Color.web("#475569"));
        answerLabel.setWrapText(true);

        Label difficultyLabel = new Label("Difficulty: " + card.difficulty());
        difficultyLabel.setFont(Font.font("Serif", 14));
        difficultyLabel.setTextFill(Color.web(PRIMARY_BLUE));

        box.getChildren().addAll(titleLabel, answerLabel, difficultyLabel);
        return box;
    }

    private record CardPreview(String question, String answer, String difficulty) {
    }
}
