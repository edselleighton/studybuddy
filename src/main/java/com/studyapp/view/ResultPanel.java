package com.studyapp.view;

import com.studyapp.util.UiScale;

import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ResultPanel {

    public static VBox build(StudyPanel sp, String result, Flashcard card, String answer, Deck deck) {
        String correctAnswer = card.getAnswer();
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(12));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox resultArea = new VBox(16);
        resultArea.setPadding(new Insets(18));
        resultArea.setStyle(StudyPanel.BORDER_STYLE);
        resultArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        Label header = new Label(deck.getName());
        header.setFont(UiScale.titleFont(52));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle(
                "-fx-background-color: " + StudyPanel.HEADER_BLUE +
                        "; -fx-background-radius: 8; -fx-padding: 8;"
        );

        Label questionBox = new Label("Q: " + card.getQuestion());
        questionBox.setFont(UiScale.bodyFont(24));
        questionBox.setWrapText(true);
        questionBox.setMinHeight(UiScale.size(130));
        questionBox.setAlignment(Pos.TOP_LEFT);
        questionBox.setMaxWidth(Double.MAX_VALUE);
        questionBox.setPadding(new Insets(14));
        questionBox.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 8; -fx-background-color: #f8fbff;"
        );

        Label prompt = new Label("Your Answer:");
        prompt.setFont(UiScale.headingFont(22));

        TextArea answerInput = new TextArea(answer == null ? "" : answer);
        answerInput.setEditable(false);
        answerInput.setMaxWidth(UiScale.size(860));
        answerInput.setPrefHeight(UiScale.size(125));
        answerInput.setWrapText(true);
        answerInput.setFont(UiScale.bodyFont(32));
        answerInput.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 5; -fx-background-radius: 5;" +
                        " -fx-focus-color: transparent;" +
                        " " + UiScale.uiFontCss(32)
        );

        VBox answerSection = new VBox(10);
        answerSection.setAlignment(Pos.CENTER);
        answerSection.setMaxWidth(UiScale.size(920));
        answerSection.setStyle("-fx-background-color: #f8fbff; -fx-border-color: " + StudyPanel.PRIMARY_BLUE
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 18;");
        answerSection.getChildren().addAll(prompt, answerInput);

        boolean exactMatch = answer != null
                && correctAnswer != null
                && answer.trim().equalsIgnoreCase(correctAnswer.trim());

        Label feedbackLabel = new Label(feedbackMessage(result, exactMatch));
        feedbackLabel.setFont(UiScale.bodyFont(22));
        feedbackLabel.setTextFill(Color.web("#334155"));
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(UiScale.size(860));
        answerSection.getChildren().add(feedbackLabel);

        if (!exactMatch) {
            Label expectedAnswerLabel = new Label("Expected answer: " + correctAnswer);
            expectedAnswerLabel.setFont(UiScale.emphasisFont(22));
            expectedAnswerLabel.setTextFill(Color.web("#2a548f"));
            expectedAnswerLabel.setWrapText(true);
            expectedAnswerLabel.setMaxWidth(UiScale.size(860));
            expectedAnswerLabel.setStyle("-fx-font-weight: bold;");
            answerSection.getChildren().add(expectedAnswerLabel);
        }

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        Label resultLabel = new Label(result);
        resultLabel.setFont(UiScale.emphasisFont(52));
        Paint textColor = Color.web("#2e7d32");
        if (result.equals("INCORRECT")) {
            textColor = Color.web("#c62828");
        } else if (result.equals("CLOSE")) {
            textColor = Color.web("#f9a825");
        }
        resultLabel.setTextFill(textColor);

        Button prevBtn = new Button("PREVIOUS");
        Button retryBtn = new Button("RETRY");
        Button nextBtn = new Button(sp.isLastCard() ? "FINISH" : "NEXT");

        String navDefault = "-fx-background-color: #e6eaf5; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; " + UiScale.buttonFontCss(18);
        String navHover = "-fx-background-color: #c9d4ef; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; " + UiScale.buttonFontCss(18);

        for (Button button : new Button[]{prevBtn, retryBtn, nextBtn}) {
            button.setPrefWidth(UiScale.size(175));
            button.setPrefHeight(UiScale.size(52));
            button.setFont(UiScale.buttonFont(18));
            button.setStyle(navDefault);
            button.setOnMouseEntered(e -> button.setStyle(navHover));
            button.setOnMouseExited(e -> button.setStyle(navDefault));
        }

        prevBtn.setDisable(sp.isFirstCard());
        prevBtn.setOnAction(e -> sp.goPrevious());
        retryBtn.setOnAction(e -> sp.goRetry());
        nextBtn.setOnAction(e -> sp.goNext());

        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER);
        navRow.getChildren().addAll(prevBtn, retryBtn, nextBtn);

        VBox bottomResult = new VBox(14);
        bottomResult.setAlignment(Pos.CENTER);
        bottomResult.getChildren().addAll(resultLabel, navRow);

        resultArea.getChildren().addAll(header, questionBox, answerSection, bottomSpacer, bottomResult);
        wrapper.getChildren().add(resultArea);
        return wrapper;
    }

    private static String feedbackMessage(String result, boolean exactMatch) {
        if ("CORRECT".equals(result) && exactMatch) {
            return "Correct. Your answer matches the expected answer.";
        }
        if ("CORRECT".equals(result)) {
            return "Accepted. The checker allows minor spelling differences, so review the expected answer to make sure the meaning is still right.";
        }
        if ("CLOSE".equals(result)) {
            return "Close. Your answer is similar, but it may be missing an important detail. Compare it with the expected answer before moving on.";
        }
        return "Incorrect. Review the expected answer, then retry the card when you are ready.";
    }
}

