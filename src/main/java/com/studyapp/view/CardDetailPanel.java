package com.studyapp.view;

import java.time.format.DateTimeFormatter;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

/**
 * CardDetailPanel
 *
 * Displays the full detail view of a single Flashcard.
 *
 * Responsibilities (Person 3 – Flashcard UI):
 *  - Render question, answer, and metadata in a styled panel.
 *  - Provide an Edit flow: clicking "Edit" makes the question, answer,
 *    and difficulty fields editable; "Save Changes" persists the update.
 *  - Provide a Delete flow: clicking "DELETE" shows a confirmation popup;
 *    confirming with "YES" removes the card via MainController.
 *  - After either operation, the onNavigateBack callback is invoked so the
 *    calling list panel (e.g. AllCardsPanel) rebuilds itself from fresh data.
 *
 * Does NOT own MainController or any DAO — it delegates to mc.updateFlashcard()
 * and mc.deleteFlashcard() as required by the conflict-reduction rules.
 */
public class CardDetailPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE  = "#41729f";
    private static final String BORDER_STYLE =
            "-fx-border-color: "      + PRIMARY_BLUE
            + "; -fx-border-radius: 14"
            + "; -fx-background-radius: 10"
            + "; -fx-background-color: white;";

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Replaces the main layout's center and sidebar with the card-detail view.
     *
     * @param mainLayout     the root BorderPane of the application
     * @param flashcard      the card to display / edit / delete
     * @param mc             the central MainController
     * @param onNavigateBack called after a successful save or delete so the
     *                       previous list panel can refresh its content;
     *                       if null, the raw prevContent node is restored instead
     */
    public static void show(BorderPane mainLayout, Flashcard flashcard,
                            MainController mc, Runnable onNavigateBack) {

        Node savedSidebar = mainLayout.getLeft();
        Node prevContent  = mainLayout.getCenter();

        // Shared editable fields — created once and referenced by both
        // the sidebar buttons and the content panel.
        TextArea  questionArea = buildTextArea(flashcard.getQuestion(), HEADER_BLUE, "white");
        TextArea  answerArea   = buildTextArea(flashcard.getAnswer(), "white", "#333333");
        TextField diffField    = buildDiffField(flashcard.getDifficulty());

        VBox content = buildContent(flashcard, questionArea, answerArea, diffField);
        VBox sidebar = buildSidebar(
                mainLayout, savedSidebar, prevContent,
                flashcard, mc, questionArea, answerArea, diffField,
                onNavigateBack);

        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(content);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sidebar
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the left sidebar with Edit, DELETE, Save Changes, and BACK buttons.
     *
     * Edit mode is tracked via a single-element boolean array (editMode[0]) so
     * it can be mutated inside lambda expressions.
     */
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

        // ── Edit toggle logic ─────────────────────────────────────────────────
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

        // ── Save Changes logic ────────────────────────────────────────────────
        saveBtn.setOnAction(ev -> {
            String newQuestion   = questionArea.getText().trim();
            String newAnswer     = answerArea.getText().trim();
            String newDifficulty = diffField.getText().trim().toUpperCase();

            // Basic validation
            if (newQuestion.isEmpty() || newAnswer.isEmpty() || newDifficulty.isEmpty()) {
                showAlert("Validation Error",
                        "Question, answer, and difficulty cannot be empty.");
                return;
            }
            if (!newDifficulty.equals("EASY")
                    && !newDifficulty.equals("MEDIUM")
                    && !newDifficulty.equals("HARD")) {
                showAlert("Validation Error",
                        "Difficulty must be EASY, MEDIUM, or HARD.");
                return;
            }

            Flashcard updated = new Flashcard(
                    flashcard.getCardID(),
                    flashcard.getDeckID(),
                    newQuestion,
                    newAnswer,
                    newDifficulty,
                    flashcard.getCreatedAt());

            try {
                mc.updateFlashcard(updated);
                navigateBack(mainLayout, savedSidebar, prevContent, onNavigateBack);
            } catch (CustomException ex) {
                showAlert("Save Failed", ex.getMessage());
            }
        });

        // ── DELETE logic ──────────────────────────────────────────────────────
        deleteBtn.setOnAction(ev ->
                showDeleteConfirm(flashcard, mc, mainLayout,
                        savedSidebar, prevContent, onNavigateBack));

        buttonBox.getChildren().addAll(editBtn, deleteBtn, saveBtn, spacer, backBtn);
        sidebar.getChildren().addAll(title, buttonBox);
        return sidebar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Content panel
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the center content panel.
     *
     * The question and answer areas are TextAreas styled to look like the
     * original Labels. They are non-editable by default; the Edit button
     * in the sidebar enables them without rebuilding this panel.
     *
     * The difficulty TextField sits inline in the info box next to a
     * "Difficulty:" label, also non-editable by default.
     */
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

        Label frontLabel = new Label("Front:");
        frontLabel.setFont(Font.font("Serif", 16));
        frontLabel.setTextFill(Color.WHITE);

        questionArea.setMaxWidth(Double.MAX_VALUE);
        questionArea.setWrapText(true);
        questionArea.setFont(Font.font("Serif", 26));
        questionArea.setStyle(
                "-fx-control-inner-background: " + HEADER_BLUE + ";"
                + " -fx-text-fill: white;"
                + " -fx-border-color: transparent;"
                + " -fx-background-color: transparent;");
        VBox.setVgrow(questionArea, Priority.ALWAYS);

        questionSection.getChildren().addAll(frontLabel, questionArea);

        // ── Answer section ────────────────────────────────────────────────────
        VBox answerSection = new VBox(4);
        answerSection.setPrefHeight(120);
        answerSection.setStyle(BORDER_STYLE);
        answerSection.setPadding(new Insets(10));

        Label backLabel = new Label("Back:");
        backLabel.setFont(Font.font("Serif", 16));

        answerArea.setMaxWidth(Double.MAX_VALUE);
        answerArea.setWrapText(true);
        answerArea.setFont(Font.font("Serif", 22));
        answerArea.setStyle(
                "-fx-control-inner-background: white;"
                + " -fx-border-color: transparent;"
                + " -fx-background-color: transparent;");
        VBox.setVgrow(answerArea, Priority.ALWAYS);

        answerSection.getChildren().addAll(backLabel, answerArea);

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

    // ─────────────────────────────────────────────────────────────────────────
    // Delete confirmation popup
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Shows a modal "Delete Card?" popup.
     *
     * Layout matches the provided mockup:
     *  - Rounded light-blue container
     *  - "Delete Card?" title in PRIMARY_BLUE
     *  - Description text
     *  - Salmon-red YES button
     *  - Light-green NO button
     *
     * YES → mc.deleteFlashcard() → close popup → navigate back (refreshed list)
     * NO  → close popup, no side effects
     */
    private static void showDeleteConfirm(
            Flashcard flashcard, MainController mc,
            BorderPane mainLayout, Node savedSidebar, Node prevContent,
            Runnable onNavigateBack) {

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.setTitle("Confirm Delete");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 50, 40, 50));
        root.setStyle(
                "-fx-background-color: #eef1fa;"
                + " -fx-background-radius: 20;"
                + " -fx-border-color: #c5cce8;"
                + " -fx-border-radius: 20;"
                + " -fx-border-width: 2;");

        Label titleLbl = new Label("Delete Card?");
        titleLbl.setFont(Font.font("Serif", 28));
        titleLbl.setTextFill(Color.web(PRIMARY_BLUE));

        Label msgLbl = new Label(
                "This will permanently\nremove this card from the\ndeck. Are you sure?");
        msgLbl.setFont(Font.font("Serif", 15));
        msgLbl.setTextFill(Color.web(PRIMARY_BLUE));
        msgLbl.setTextAlignment(TextAlignment.CENTER);
        msgLbl.setAlignment(Pos.CENTER);

        // YES button — salmon-red
        final String yesDefault =
                "-fx-background-color: #e08080; -fx-text-fill: #6b0000;"
                + " -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand;";
        final String yesHover =
                "-fx-background-color: #c86060; -fx-text-fill: white;"
                + " -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand;";

        Button yesBtn = new Button("YES");
        yesBtn.setPrefWidth(180);
        yesBtn.setFont(Font.font("Serif", 18));
        yesBtn.setStyle(yesDefault);
        yesBtn.setOnMouseEntered(e -> yesBtn.setStyle(yesHover));
        yesBtn.setOnMouseExited(e  -> yesBtn.setStyle(yesDefault));
        yesBtn.setOnAction(e -> {
            try {
                mc.deleteFlashcard(flashcard.getCardID());
                popup.close();
                navigateBack(mainLayout, savedSidebar, prevContent, onNavigateBack);
            } catch (CustomException ex) {
                popup.close();
                showAlert("Delete Failed", ex.getMessage());
            }
        });

        // NO button — light green
        final String noDefault =
                "-fx-background-color: #b8e6b0; -fx-text-fill: #1a5c14;"
                + " -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand;";
        final String noHover =
                "-fx-background-color: #8fcf87; -fx-text-fill: #0d3d08;"
                + " -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand;";

        Button noBtn = new Button("NO");
        noBtn.setPrefWidth(180);
        noBtn.setFont(Font.font("Serif", 18));
        noBtn.setStyle(noDefault);
        noBtn.setOnMouseEntered(e -> noBtn.setStyle(noHover));
        noBtn.setOnMouseExited(e  -> noBtn.setStyle(noDefault));
        noBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(titleLbl, msgLbl, yesBtn, noBtn);

        Scene scene = new Scene(root, 320, 360);
        scene.setFill(Color.web("#eef1fa"));
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates back after a save or delete.
     * If onNavigateBack is provided, it is called (so the caller can rebuild
     * a fresh list). Otherwise, the stale prevContent node is restored.
     */
    private static void navigateBack(BorderPane mainLayout, Node savedSidebar,
                                     Node prevContent, Runnable onNavigateBack) {
        mainLayout.setLeft(savedSidebar);
        if (onNavigateBack != null) {
            onNavigateBack.run();
        } else {
            mainLayout.setCenter(prevContent);
        }
    }

    /**
     * Creates a non-editable TextArea pre-filled with the given text.
     * Background and text colors can be configured for question vs answer.
     */
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

    /**
     * Creates a non-editable TextField pre-filled with the difficulty value.
     */
    private static TextField buildDiffField(String difficulty) {
        TextField tf = new TextField(difficulty);
        tf.setEditable(false);
        tf.setFocusTraversable(false);
        return tf;
    }

    /** Styled info label matching the original design. */
    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 21));
        return lbl;
    }

    /** Displays a simple error/info dialog. */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}