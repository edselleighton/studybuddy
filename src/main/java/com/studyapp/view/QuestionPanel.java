package com.studyapp.view;

import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class QuestionPanel {

    public static VBox build(StudyPanel sp, Flashcard card, Deck deck) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox quizArea = new VBox(20);
        quizArea.setPadding(new Insets(20));
        quizArea.setStyle(StudyPanel.BORDER_STYLE);
        quizArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(quizArea, Priority.ALWAYS);

        // ── header ────────────────────────────────────────────────────────────
        Label header = new Label(deck.getName());
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle(
                "-fx-background-color: " + StudyPanel.HEADER_BLUE +
                        "; -fx-background-radius: 8; -fx-padding: 10;"
        );

        // ── question ──────────────────────────────────────────────────────────
        Label questionBox = new Label("Q: " + card.getQuestion());
        questionBox.setFont(Font.font("Serif", 20));
        questionBox.setWrapText(true);
        questionBox.setMinHeight(150);
        questionBox.setAlignment(Pos.TOP_LEFT);
        questionBox.setMaxWidth(Double.MAX_VALUE);
        questionBox.setPadding(new Insets(20));
        questionBox.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 8; -fx-background-color: #f8fbff;"
        );

        // ── answer input ──────────────────────────────────────────────────────
        Label prompt = new Label("Enter Answer:");
        prompt.setFont(Font.font("Serif", 14));

        TextArea answerInput = new TextArea();
        answerInput.setMaxWidth(550);
        answerInput.setPrefHeight(100);
        answerInput.setWrapText(true);
        answerInput.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 5; -fx-background-radius: 5;" +
                        " -fx-focus-color: transparent;"
        );

        // Ctrl+Enter to submit without leaving the text area
        answerInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                sp.handleSubmit(answerInput.getText());
            }
        });

        VBox answerSection = new VBox(8);
        answerSection.setAlignment(Pos.CENTER);
        answerSection.getChildren().addAll(prompt, answerInput);

        // ── submit button ─────────────────────────────────────────────────────
        Button submitBtn = new Button("SUBMIT");
        submitBtn.setPrefWidth(150);
        submitBtn.setPrefHeight(40);
        String submitDefault = "-fx-background-color: #e6eaf5; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; -fx-font-weight: bold;";
        String submitHover = "-fx-background-color: #c9d4ef; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; -fx-font-weight: bold;";
        submitBtn.setStyle(submitDefault);
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(submitHover));
        submitBtn.setOnMouseExited(e  -> submitBtn.setStyle(submitDefault));
        submitBtn.setOnAction(e -> sp.handleSubmit(answerInput.getText()));

        Region filler = new Region();
        VBox.setVgrow(filler, Priority.ALWAYS);

        quizArea.getChildren().addAll(header, questionBox, answerSection, filler, submitBtn);
        wrapper.getChildren().add(quizArea);
        return wrapper;
    }
}