package com.studyapp.view;

import java.time.format.DateTimeFormatter;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CardDetailPanel {
    private static double delXOffset = 0;
    private static double delYOffset = 0;

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE  = "#41729f";
    private static final String BORDER_STYLE =
            "-fx-border-color: "      + PRIMARY_BLUE
            + "; -fx-border-radius: 14"
            + "; -fx-background-radius: 10"
            + "; -fx-background-color: white;";

    public static void show(BorderPane mainLayout, Flashcard flashcard,
                            MainController mc, Runnable onNavigateBack) {

        Node savedSidebar = mainLayout.getLeft();
        Node prevContent  = mainLayout.getCenter();

        TextArea  questionArea = buildTextArea(flashcard.getQuestion(), HEADER_BLUE, "white");
        TextArea  answerArea   = buildTextArea(flashcard.getAnswer(), "white", "#333333");
        TextField diffField    = buildDiffField(flashcard.getDifficulty().toUpperCase());

        VBox content = buildContent(flashcard, questionArea, answerArea, diffField);
        VBox sidebar = buildSidebar( mainLayout, savedSidebar, prevContent, flashcard, mc, questionArea, answerArea, diffField, onNavigateBack);

        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(content);
    }

    private static VBox buildSidebar(
            BorderPane mainLayout, Node savedSidebar, Node prevContent,
            Flashcard flashcard, MainController mc,
            TextArea questionArea, TextArea answerArea, TextField diffField,
            Runnable onNavigateBack) {

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

        // ── Edit button ───────────────────────────────────────────────────────
        final String editIdleStyle =
                "-fx-background-color: #e6eaf5; -fx-text-fill: black;"
                + " -fx-border-color: " + PRIMARY_BLUE
                + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        final String editActiveStyle =
                "-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white;"
                + " -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";

        Button editBtn = new Button("Edit");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setFont(Font.font("Serif", 16));
        editBtn.setStyle(editIdleStyle);

        // ── DELETE button ─────────────────────────────────────────────────────
        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setFont(Font.font("Serif", 16));
        deleteBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 5;"
                + " -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;");

        // ── Save Changes button (hidden until edit mode is active) ────────────
        Button saveBtn = new Button("Save Changes");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setFont(Font.font("Serif", 16));
        saveBtn.setVisible(false);
        saveBtn.setManaged(false);   // takes no layout space when hidden
        saveBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #2d7a2d;"
                + " -fx-border-color: #2d7a2d; -fx-border-radius: 5;"
                + " -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── BACK button ───────────────────────────────────────────────────────
        final String backDefault =
                "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        final String backHover =
                "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";

        Button backBtn = new Button("BACK");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setFont(Font.font("Serif", 16));
        backBtn.setStyle(backDefault);
        backBtn.setOnMouseEntered(ev -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(ev  -> backBtn.setStyle(backDefault));
        backBtn.setOnAction(ev -> {
            mainLayout.setLeft(savedSidebar);
            mainLayout.setCenter(prevContent);
        });

        boolean[] editMode = {false};

        editBtn.setOnAction(ev -> {
            editMode[0] = !editMode[0];
            boolean on = editMode[0];

            // Enable / disable fields
            questionArea.setEditable(on);
            questionArea.setFocusTraversable(on);
            answerArea.setEditable(on);
            answerArea.setFocusTraversable(on);
            diffField.setEditable(on);
            diffField.setFocusTraversable(on);

            // Highlight editable fields when active
            String editableAreaHighlight =
                    "-fx-control-inner-background: " + (on ? "#d8e4f5" : HEADER_BLUE) + ";"
                    + " -fx-text-fill: " + (on ? "black" : "white") + ";"
                    + " -fx-border-color: transparent; -fx-background-color: transparent;";
            questionArea.setStyle(on ? editableAreaHighlight
                    : "-fx-control-inner-background: " + HEADER_BLUE + ";"
                      + " -fx-text-fill: white; -fx-border-color: transparent;"
                      + " -fx-background-color: transparent;");

            diffField.setStyle(
                    "-fx-font-size: 18px; -fx-font-family: Serif;"
                    + " -fx-background-color: " + (on ? "#f0f4ff" : "transparent") + ";"
                    + " -fx-border-color: " + (on ? PRIMARY_BLUE : "transparent") + ";"
                    + " -fx-border-radius: 3;");

            // Show / hide Save Changes
            saveBtn.setVisible(on);
            saveBtn.setManaged(on);

            editBtn.setStyle(on ? editActiveStyle : editIdleStyle);
        });

        saveBtn.setOnAction(ev -> {
            String newQuestion   = questionArea.getText().trim();
            String newAnswer     = answerArea.getText().trim();
            String newDifficulty = diffField.getText().trim().toUpperCase();

            try {
                saveChanges(flashcard, mc, newQuestion, newAnswer, newDifficulty);
                navigateBack(mainLayout, savedSidebar, prevContent, onNavigateBack);
            } catch (CustomException ex) {
                MainFrame.showErrorDialog("Save Failed: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(ev -> showDeleteCardDialog(mainLayout, mc, flashcard, savedSidebar, onNavigateBack));

        buttonBox.getChildren().addAll(editBtn, deleteBtn, saveBtn, spacer, backBtn);
        sidebar.getChildren().addAll(title, buttonBox);
        return sidebar;
    }

    private static VBox buildContent(Flashcard flashcard,
                                     TextArea questionArea,
                                     TextArea answerArea,
                                     TextField diffField) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // ── Question section (blue card) ──────────────────────────────────────
        VBox questionSection = new VBox(4);
        questionSection.setPrefHeight(270);
        questionSection.setStyle(
                "-fx-background-color: " + HEADER_BLUE + ";"
                + " -fx-background-radius: 15; -fx-padding: 10;");

        Label questionLabel = new Label("Question:");
        questionLabel.setFont(Font.font("Serif", 16));
        questionLabel.setTextFill(Color.WHITE);

        questionArea.setMaxWidth(Double.MAX_VALUE);
        questionArea.setWrapText(true);
        questionArea.setFont(Font.font("Serif", 26));
        questionArea.setStyle(
                "-fx-control-inner-background: " + HEADER_BLUE + ";"
                + " -fx-text-fill: white;"
                + " -fx-border-color: transparent;"
                + " -fx-background-color: transparent;");
        VBox.setVgrow(questionArea, Priority.ALWAYS);

        questionSection.getChildren().addAll(questionLabel, questionArea);

        // ── Answer section ────────────────────────────────────────────────────
        VBox answerSection = new VBox(4);
        answerSection.setPrefHeight(120);
        answerSection.setStyle(BORDER_STYLE);
        answerSection.setPadding(new Insets(10));

        Label answerLabel = new Label("Answer:");
        answerLabel.setFont(Font.font("Serif", 16));

        answerArea.setMaxWidth(Double.MAX_VALUE);
        answerArea.setWrapText(true);
        answerArea.setFont(Font.font("Serif", 22));
        answerArea.setStyle(
                "-fx-control-inner-background: white;"
                + " -fx-border-color: transparent;"
                + " -fx-background-color: transparent;");
        VBox.setVgrow(answerArea, Priority.ALWAYS);

        answerSection.getChildren().addAll(answerLabel, answerArea);

        // ── Info box ──────────────────────────────────────────────────────────
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VBox infoBox = new VBox(12);
        infoBox.setPadding(new Insets(10));
        infoBox.setPrefHeight(200);
        infoBox.setStyle(BORDER_STYLE);

        // Difficulty row: static label + editable TextField side by side
        Label diffLabel = infoLabel("Difficulty: ");
        diffField.setFont(Font.font("Serif", 21));
        diffField.setPrefWidth(150);
        diffField.setStyle(
                "-fx-font-size: 18px; -fx-font-family: Serif;"
                + " -fx-background-color: transparent;"
                + " -fx-border-color: transparent;");

        HBox diffRow = new HBox(0, diffLabel, diffField);
        diffRow.setAlignment(Pos.CENTER_LEFT);

        infoBox.getChildren().addAll(
                infoLabel("\nID: "      + flashcard.getCardID()),
                infoLabel("Deck: "     + flashcard.getDeckID()),
                diffRow,
                infoLabel("Created at: " + flashcard.getCreatedAt().format(fmt)));

        mainContent.getChildren().addAll(questionSection, answerSection, infoBox);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static void saveChanges(Flashcard flashcard, MainController mc, String newQuestion, String newAnswer, String newDifficulty) throws CustomException{
        String oldQuestion = flashcard.getQuestion();
        String oldAnswer = flashcard.getAnswer();
        String oldDifficulty = flashcard.getDifficulty();

        if(!newQuestion.equals(oldQuestion) || !newAnswer.equals(oldAnswer) || !newDifficulty.equals(oldDifficulty)){
            flashcard.setQuestion(newQuestion);
            flashcard.setAnswer(newAnswer);
            flashcard.setDifficulty(newDifficulty);
            try{
                mc.updateFlashcard(flashcard);
            }catch(CustomException e){
                flashcard.setQuestion(oldQuestion);
                flashcard.setAnswer(oldAnswer);
                flashcard.setDifficulty(oldDifficulty);
                throw e;
            }
        }
    }

    private static void showDeleteCardDialog(
            BorderPane mainLayout,
            MainController mc,
            Flashcard flashcard,
            Node originalSidebar,
            Runnable returnAction) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Delete Deck");

        VBox container = new VBox(20);
        container.setPrefWidth(300);
        container.setPrefHeight(500);
        container.setSpacing(15);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: #2a548f; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: #f8fafc;");

        container.setOnMousePressed(event -> {
            delXOffset = event.getSceneX();
            delYOffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - delXOffset);
            stage.setY(event.getScreenY() - delYOffset);
        });

        Label title = new Label("Delete\nCard?");
        title.setFont(Font.font("Serif", 41));
        title.setTextFill(Color.web("#2a548f"));

        Label description = new Label("This will permanently delete this card and all records of its history. Are you sure you want to delete this card?");
        description.setFont(Font.font("Serif", 15));
        description.setTextFill(Color.web("#2a548f"));
        description.setWrapText(true);
        VBox.setMargin(description, new Insets(20, 20, 35, 0));

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Serif", 13));
        errorLabel.setTextFill(Color.web("#c0392b"));
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        VBox.setMargin(errorLabel, new Insets(-25, 0, 0, 0));

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setPrefWidth(250);
        cancelBtn.setPrefHeight(45);
        String normalStyle = "-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";
        String hoverStyleStr = "-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";

        cancelBtn.setStyle(normalStyle);
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(hoverStyleStr));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(normalStyle));
        cancelBtn.setOnAction(e -> dialog.close());

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setPrefWidth(250);
        deleteBtn.setPrefHeight(45);
        String delNormalStyle = "-fx-background-color: #ff9999; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";
        String delHoverStyle = "-fx-background-color: #ff6666; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";

        deleteBtn.setStyle(delNormalStyle);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(delHoverStyle));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(delNormalStyle));
        deleteBtn.setOnAction(e -> {
            try {
                mc.deleteFlashcard(flashcard.getCardID());
                MainFrame.showSuccessDialog("Card deleted successfully.");
                mainLayout.setLeft(originalSidebar);
                returnAction.run();
                dialog.close();
            } catch (CustomException ex) {
                errorLabel.setText((ex.getMessage() != null ? ex.getMessage() : "Failed to delete. Please try again."));
                errorLabel.setTextFill(Color.web("#ff9999"));
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelBtn, deleteBtn);

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);

        Button closeBtn = new Button("X");
        String xBarNormal = "-fx-background-color: transparent; -fx-text-fill: #1A438E; -fx-font-size: 18; -fx-cursor: hand;";
        String xBarHover = "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 0 10 0 0;";

        closeBtn.setStyle(xBarNormal);
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(xBarHover));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(xBarNormal));

        topBar.getChildren().add(closeBtn);
        VBox.setMargin(topBar, new Insets(5, -30, 0, 0));
        container.getChildren().addAll(topBar, title, description, errorLabel, buttonBox);

        Scene scene = new Scene(container, 300, 500);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    private static void navigateBack(BorderPane mainLayout, Node savedSidebar,
                                     Node prevContent, Runnable onNavigateBack) {
        mainLayout.setLeft(savedSidebar);
        if (onNavigateBack != null) {
            onNavigateBack.run();
        } else {
            mainLayout.setCenter(prevContent);
        }
    }

    private static TextArea buildTextArea(String text, String bgColor, String textColor) {
        TextArea ta = new TextArea(text);
        ta.setEditable(false);
        ta.setFocusTraversable(false);
        ta.setWrapText(true);
        ta.setStyle(
                "-fx-control-inner-background: " + bgColor + ";"
                + " -fx-text-fill: " + textColor + ";"
                + " -fx-border-color: transparent;"
                + " -fx-background-color: transparent;");
        return ta;
    }

   
    private static TextField buildDiffField(String difficulty) {
        TextField tf = new TextField(difficulty);
        tf.setEditable(false);
        tf.setFocusTraversable(false);
        return tf;
    }
    
    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 21));
        return lbl;
    }
}
