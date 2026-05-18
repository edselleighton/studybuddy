package com.studyapp.view;

import com.studyapp.util.UiScale;

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

public class QuestionPanel {

    public static VBox build(StudyPanel sp, Flashcard card, Deck deck, int cardNumber, int totalCards) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(12));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox quizArea = new VBox(18);
        quizArea.setPadding(new Insets(22));
        quizArea.setStyle(StudyPanel.BORDER_STYLE);
        quizArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(quizArea, Priority.ALWAYS);

        // ── header ────────────────────────────────────────────────────────────
        Label header = new Label(deck.getName());
        header.setFont(UiScale.titleFont(52));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle(
                "-fx-background-color: " + StudyPanel.HEADER_BLUE +
                        "; -fx-background-radius: 8; -fx-padding: 8;"
        );
        VBox.setMargin(header, new Insets(0, 0, 28, 0));

        // ── question ──────────────────────────────────────────────────────────
        Label questionBox = new Label("Q: " + card.getQuestion());
        questionBox.setFont(UiScale.bodyFont(28));
        questionBox.setWrapText(true);
        questionBox.setMinHeight(UiScale.size(150));
        questionBox.setAlignment(Pos.TOP_LEFT);
        questionBox.setMaxWidth(Double.MAX_VALUE);
        questionBox.setPadding(new Insets(18));
        questionBox.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 8; -fx-background-color: #f8fbff;"
        );
        VBox.setMargin(questionBox, new Insets(10, 0, 0, 0));

        // ── answer input ──────────────────────────────────────────────────────
        Label prompt = new Label("Enter Answer:");
        prompt.setFont(UiScale.headingFont(24));

        TextArea answerInput = new TextArea();
        answerInput.setMaxWidth(Double.MAX_VALUE);
        answerInput.setPrefHeight(UiScale.size(170));
        answerInput.setWrapText(true);
        answerInput.setFont(UiScale.bodyFont(32));
        answerInput.setStyle(
                "-fx-border-color: " + StudyPanel.PRIMARY_BLUE +
                        "; -fx-border-radius: 5; -fx-background-radius: 5;" +
                        " -fx-focus-color: transparent;" +
                        " " + UiScale.uiFontCss(32)
        );

        // Ctrl+Enter to submit without leaving the text area
        answerInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                sp.handleSubmit(answerInput.getText());
            }
        });

        VBox answerSection = new VBox(10);
        answerSection.setAlignment(Pos.CENTER);
        answerSection.setMaxWidth(Double.MAX_VALUE);
        answerSection.setStyle("-fx-background-color: #f8fbff; -fx-border-color: " + StudyPanel.PRIMARY_BLUE
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16 24;");
        answerSection.getChildren().addAll(prompt, answerInput);
        VBox.setMargin(answerSection, new Insets(18, 0, 0, 0));

        HBox cardInfo = new HBox(16);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setMaxWidth(Double.MAX_VALUE);
        cardInfo.setStyle("-fx-background-color: #f8fbff; -fx-border-color: " + StudyPanel.PRIMARY_BLUE
                + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");

        Label positionLabel = new Label("Card " + cardNumber + " of " + totalCards);
        positionLabel.setFont(UiScale.bodyFont(22));
        positionLabel.setTextFill(Color.web(StudyPanel.PRIMARY_BLUE));

        Label difficultyLabel = new Label("Difficulty: " + card.getDifficulty());
        difficultyLabel.setFont(UiScale.bodyFont(22));
        difficultyLabel.setTextFill(Color.web(StudyPanel.PRIMARY_BLUE));

        cardInfo.getChildren().addAll(positionLabel, difficultyLabel);

        // ── submit button ─────────────────────────────────────────────────────
        Button submitBtn = new Button("SUBMIT");
        submitBtn.setPrefWidth(UiScale.size(190));
        submitBtn.setPrefHeight(UiScale.size(56));
        submitBtn.setFont(UiScale.buttonFont(20));
        String submitDefault = "-fx-background-color: #e6eaf5; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; " + UiScale.buttonFontCss(20);
        String submitHover = "-fx-background-color: #c9d4ef; -fx-border-color: "
                + StudyPanel.PRIMARY_BLUE + "; -fx-border-radius: 8;"
                + " -fx-cursor: hand; " + UiScale.buttonFontCss(20);
        submitBtn.setStyle(submitDefault);
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(submitHover));
        submitBtn.setOnMouseExited(e  -> submitBtn.setStyle(submitDefault));
        submitBtn.setOnAction(e -> sp.handleSubmit(answerInput.getText()));

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        VBox.setMargin(cardInfo, new Insets(0, 0, 0, 0));

        quizArea.getChildren().addAll(header, questionBox, answerSection, cardInfo, bottomSpacer, submitBtn);
        wrapper.getChildren().add(quizArea);
        return wrapper;
    }
}

