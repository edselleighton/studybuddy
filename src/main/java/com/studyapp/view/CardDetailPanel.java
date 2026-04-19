package com.studyapp.view;

import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.Flashcard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CardDetailPanel {
    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 14; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String INACTIVE_STYLE = "-fx-background-color: white; -fx-text-fill: black;"
            + " -fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15;";
    private static final String ACTIVE_BTN_STYLE = "-fx-background-color: #e6eaf5; -fx-text-fill: black;"
            + " -fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";

    public static void show(BorderPane mainLayout, Flashcard flashcard, MainController mc) {
        Node savedSidebar = mainLayout.getLeft();
        Node prevContent = mainLayout.getCenter();
        mainLayout.setLeft(buildSidebar(mainLayout, savedSidebar, prevContent, flashcard, mc));
        mainLayout.setCenter(buildContent(flashcard, mc));
    }

    private static VBox buildSidebar(BorderPane mainLayout, Node savedSidebar, Node prevContent,Flashcard flashcard, MainController mc) {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Study Assistant\nApplication");
        title.setFont(Font.font("Serif", 18));
        title.setTextFill(Color.web(PRIMARY_BLUE));
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        VBox buttonBox = new VBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle(BORDER_STYLE);
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        Button editBtn = createDisabledBtn("Edit");

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setFont(Font.font("Serif", 16));
        deleteBtn.setDisable(true);
        deleteBtn.setStyle("-fx-background-color: white; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("BACK");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setFont(Font.font("Serif", 16));
        String backDefault = "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        String backHover = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        backBtn.setStyle(backDefault);
        backBtn.setOnMouseEntered(ev -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(ev -> backBtn.setStyle(backDefault));
        backBtn.setOnAction(ev -> {
            mainLayout.setLeft(savedSidebar);
            mainLayout.setCenter(prevContent);
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn, spacer, backBtn);
        sidebar.getChildren().addAll(title, buttonBox);
        return sidebar;
    }

    private static Button createDisabledBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Serif", 16));
        btn.setDisable(true);
        btn.setStyle(INACTIVE_STYLE);
        return btn;
    }

    private static VBox buildContent(Flashcard flashcard, MainController mc) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label question = new Label("Question: \n    " + flashcard.getQuestion());
        question.setFont(Font.font("Serif", 31));
        question.setTextFill(Color.WHITE);
        question.setMaxWidth(Double.MAX_VALUE);
        question.setAlignment(Pos.CENTER_LEFT);
        question.setWrapText(true);
        question.setPrefHeight(270);
        question.setStyle("-fx-background-color: " + HEADER_BLUE
                + "; -fx-background-radius: 15; -fx-padding: 10;");

        Label answer = new Label("Answer: \n    " + flashcard.getAnswer());
        answer.setFont(Font.font("Serif", 24));
        answer.setMaxWidth(Double.MAX_VALUE);
        answer.setAlignment(Pos.CENTER_LEFT);
        answer.setWrapText(true);
        answer.setPrefHeight(120);
        answer.setPadding(new Insets(10));
        answer.setStyle(BORDER_STYLE);
        VBox.setVgrow(answer, Priority.ALWAYS);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VBox infoBox = new VBox(12);
        infoBox.setPadding(new Insets(10));
        infoBox.setPrefHeight(200);
        infoBox.getChildren().addAll(
                infoLabel("\nID: " + flashcard.getCardID()),
                infoLabel("Deck: " + flashcard.getDeck().getDeckID()),
                infoLabel("Difficulty: " + flashcard.getDifficulty()),
                infoLabel("Created at: " + flashcard.getCreatedAt().format(fmt)));
        infoBox.setStyle(BORDER_STYLE);
        VBox.setMargin(infoBox, new Insets(0, 0, 0, 0));

        mainContent.getChildren().addAll(question, answer, infoBox);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 21));
        return lbl;
    }
}
