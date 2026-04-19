package com.studyapp.view;

import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ResultPanel {

    public static VBox build(StudyPanel sp, boolean isCorrect, Flashcard card, String answer, Deck deck) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox resultArea = new VBox(20);
        resultArea.setPadding(new Insets(20));
        resultArea.setStyle(StudyPanel.BORDER_STYLE);
        resultArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(resultArea, Priority.ALWAYS);

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

        TextArea answerInput = new TextArea(answer);
        answerInput.setEditable(false);
        answerInput.setMaxWidth(550);
        answerInput.setPrefHeight(100);
        answerInput.setWrapText(true);
        answerInput.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 5; -fx-background-radius: 5;" +
                        " -fx-focus-color: transparent;"
        );

        VBox answerSection = new VBox(8);
        answerSection.setAlignment(Pos.CENTER);
        answerSection.getChildren().addAll(prompt, answerInput);

        // ── result label — no correct answer revealed if wrong ────────────────
        Label resultLabel = new Label(isCorrect ? "CORRECT" : "INCORRECT");
        resultLabel.setFont(Font.font("Serif", FontWeight.BOLD, 36));
        resultLabel.setTextFill(isCorrect ? Color.web("#2e7d32") : Color.web("#c62828"));

        // ── nav buttons ───────────────────────────────────────────────────────
        Button prevBtn  = new Button("PREVIOUS");
        Button retryBtn = new Button("RETRY");
        Button nextBtn  = new Button(sp.isLastCard() ? "FINISH" : "NEXT");

        String navDefault = "-fx-background-color: #e6eaf5; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; -fx-font-size: 13;";
        String navHover = "-fx-background-color: #c9d4ef; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; -fx-font-size: 13;";

        for (Button b : new Button[]{prevBtn, retryBtn, nextBtn}) {
            b.setPrefWidth(130);
            b.setPrefHeight(38);
            b.setStyle(navDefault);
            b.setOnMouseEntered(e -> b.setStyle(navHover));
            b.setOnMouseExited(e  -> b.setStyle(navDefault));
        }

        prevBtn.setDisable(sp.isFirstCard());

        prevBtn.setOnAction(e  -> sp.goPrevious());
        retryBtn.setOnAction(e -> sp.goRetry());
        nextBtn.setOnAction(e  -> sp.goNext());

        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER);
        navRow.getChildren().addAll(prevBtn, retryBtn, nextBtn);

        Region bottomFiller = new Region();
        VBox.setVgrow(bottomFiller, Priority.ALWAYS);

        resultArea.getChildren().addAll(header, questionBox, answerSection, resultLabel, bottomFiller, navRow);
        wrapper.getChildren().add(resultArea);
        return wrapper;
    }
}