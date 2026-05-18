package com.studyapp.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.service.CardJson;
import com.studyapp.util.UiScale;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Full-screen modal dialog for importing flashcard questions from a JSON or CSV file.
 *
 * <p>Layout overview:
 * <pre>
 * ┌───────────────────────────────────────────────────────────────┐
 * │                    IMPORT QUESTIONS (header)                  │
 * ├──────────────────────────┬────────────────────────────────────┤
 * │ ADD TO DECK:             │                  [SELECT ALL □]    │
 * │  ○ Add to Existing Deck  │  ╔══════════════════════════════╗  │
 * │    [deck dropdown]       │  ║ placeholder / editable table ║  │
 * │  ○ Add to New Deck       │  ╚══════════════════════════════╝  │
 * │    [name field]          │                                    │
 * │    [desc field]          │                                    │
 * │  [IMPORT FROM CSV]       │                                    │
 * │  [IMPORT FROM JSON]      │                                    │
 * │  [IMPORT TO SYSTEM]      │                                    │
 * │  [CANCEL]                │                                    │
 * └──────────────────────────┴────────────────────────────────────┘
 * </pre>
 */
public class ImportDialogPanel {

    /* ── colour constants (match the app's existing blue palette) ─── */
    private static final String HEADER_DARK  = "#3a5a8a";
    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String PANEL_BG     = "#e8e8f5";
    private static final String PANEL_BORDER = "#6a7acd";
    private static final String PAGE_BG      = "#f0f2f8";

    // ────────────────────────────────────────────────────────────────
    // Inner data model
    // ────────────────────────────────────────────────────────────────

    /**
     * Represents one flashcard candidate in the preview table.
     *
     * <p>Each field is a JavaFX property so table cells can bind to it directly.
     * A {@code null} difficulty means the user has not yet assigned one
     * (displayed as "--Select Difficulty--").
     */
    public static class CardRowData {

        private final BooleanProperty selected   = new SimpleBooleanProperty(false);
        private final StringProperty  question   = new SimpleStringProperty("");
        private final StringProperty  answer     = new SimpleStringProperty("");
        /**
         * {@code null}  → "--Select Difficulty--" (user must choose before importing).
         * {@code "Easy"} / {@code "Medium"} / {@code "Hard"}  → pre-filled from file.
         */
        private final StringProperty  difficulty = new SimpleStringProperty(null);

        /**
         * @param q    raw question string from the parsed file (trimmed internally)
         * @param a    raw answer string from the parsed file (trimmed internally)
         * @param diff raw difficulty from the file; null or unrecognised → stored as null
         */
        public CardRowData(String q, String a, String diff) {
            question.set(q    != null ? q.trim() : "");
            answer.set(a      != null ? a.trim() : "");
            difficulty.set(validateDifficulty(diff));
        }

        /**
         * Returns "Easy", "Medium", or "Hard" when recognised; {@code null} otherwise.
         * Unlike the import-service normaliser, unrecognised values are NOT defaulted
         * to "Medium" — they are left as null so the UI can prompt the user.
         */
        private static String validateDifficulty(String raw) {
            if (raw == null || raw.isBlank()) return null;
            return switch (raw.trim().toLowerCase()) {
                case "easy"   -> "Easy";
                case "medium" -> "Medium";
                case "hard"   -> "Hard";
                default       -> null;
            };
        }

        public BooleanProperty selectedProperty()   { return selected; }
        public StringProperty  questionProperty()   { return question; }
        public StringProperty  answerProperty()     { return answer; }
        public StringProperty  difficultyProperty() { return difficulty; }

        public boolean isSelected()    { return selected.get(); }
        public String  getQuestion()   { return question.get(); }
        public String  getAnswer()     { return answer.get(); }
        /** Returns {@code null} when no difficulty has been assigned. */
        public String  getDifficulty() { return difficulty.get(); }
    }

    // ────────────────────────────────────────────────────────────────
    // Public entry-point
    // ────────────────────────────────────────────────────────────────

    /**
     * Opens the Import Questions dialog as a maximised, application-modal stage.
     * All import logic, preview, and post-import navigation are handled internally.
     *
     * @param mainLayout root {@link BorderPane} of the main window
     * @param mc         controller for all business/data operations
     */
    public static void show(BorderPane mainLayout, MainController mc) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(mainLayout.getScene().getWindow());
        dialog.setTitle("Import Questions");

        /* Shared list backing the preview TableView. */
        ObservableList<CardRowData> cardRows = FXCollections.observableArrayList();

        // ── root ──────────────────────────────────────────────────────
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + PAGE_BG + ";");

        // ── title header bar ──────────────────────────────────────────
        Label titleLbl = new Label("IMPORT QUESTIONS");
        titleLbl.setFont(UiScale.titleFont(32));
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setMaxWidth(Double.MAX_VALUE);
        titleLbl.setAlignment(Pos.CENTER);
        titleLbl.setPadding(new Insets(16, 20, 16, 20));
        titleLbl.setStyle("-fx-background-color: " + HEADER_DARK + ";");

        // ── body: left panel | right panel ────────────────────────────
        HBox body = new HBox(20);
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setStyle("-fx-background-color: " + PAGE_BG + ";");
        VBox.setVgrow(body, Priority.ALWAYS);

        // ════════════════════════════════════════
        // LEFT PANEL
        // ════════════════════════════════════════
        VBox left = new VBox(14);
        left.setPrefWidth(300);
        left.setMinWidth(260);
        left.setMaxWidth(340);
        left.setPadding(new Insets(20, 20, 24, 20));
        left.setStyle(
            "-fx-background-color: " + PANEL_BG + "; " +
            "-fx-border-color: " + PANEL_BORDER + "; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 10; -fx-background-radius: 10;"
        );

        // "ADD TO DECK:" heading
        Label addToDeckLbl = new Label("ADD TO DECK:");
        addToDeckLbl.setFont(UiScale.headingFont(18));
        addToDeckLbl.setTextFill(Color.web(PRIMARY_BLUE));

        ToggleGroup deckToggle = new ToggleGroup();

        // ── "Add to Existing Deck" branch ─────────────────────────────
        RadioButton existingRadio = new RadioButton("Add to Existing Deck:");
        existingRadio.setToggleGroup(deckToggle);
        existingRadio.setFont(UiScale.bodyFont(16));
        existingRadio.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<Deck> existingCombo = new ComboBox<>();
        existingCombo.getItems().addAll(mc.allDecks());
        existingCombo.setMaxWidth(Double.MAX_VALUE);
        existingCombo.setPromptText("-- Select a Deck --");
        existingCombo.setStyle(comboStyle());
        existingCombo.setCellFactory(lv -> deckCell());
        existingCombo.setButtonCell(deckCell());
        existingCombo.setVisible(false);
        existingCombo.setManaged(false);

        // ── "Add to New Deck" branch ───────────────────────────────────
        RadioButton newDeckRadio = new RadioButton("Add to New Deck:");
        newDeckRadio.setToggleGroup(deckToggle);
        newDeckRadio.setFont(UiScale.bodyFont(16));
        newDeckRadio.setTextFill(Color.web(PRIMARY_BLUE));

        TextField nameField = new TextField();
        nameField.setPromptText("Deck Name");
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.setStyle(inputStyle());
        nameField.setVisible(false);
        nameField.setManaged(false);

        TextField descField = new TextField();
        descField.setPromptText("Deck Description");
        descField.setMaxWidth(Double.MAX_VALUE);
        descField.setStyle(inputStyle());
        descField.setVisible(false);
        descField.setManaged(false);

        // Toggle visibility of the deck sub-controls when a radio is selected
        existingRadio.selectedProperty().addListener((obs, old, sel) -> {
            existingCombo.setVisible(sel);
            existingCombo.setManaged(sel);
        });
        newDeckRadio.selectedProperty().addListener((obs, old, sel) -> {
            nameField.setVisible(sel);
            nameField.setManaged(sel);
            descField.setVisible(sel);
            descField.setManaged(sel);
        });

        // Default: use "Existing" when decks are available, else "New"
        if (mc.allDecks().isEmpty()) {
            newDeckRadio.setSelected(true);
        } else {
            existingRadio.setSelected(true);
        }

        // ── file-import buttons ────────────────────────────────────────
        Button csvBtn  = fileImportButton("IMPORT FROM CSV");
        Button jsonBtn = fileImportButton("IMPORT FROM JSON");

        // ── main action buttons ────────────────────────────────────────
        Button importBtn = actionButton("IMPORT TO SYSTEM", "#8bc34a", "#7cb33a");
        importBtn.setDisable(true);   // enabled only when ≥1 row is checked

        Button cancelBtn = actionButton("CANCEL", "#e57373", "#d32f2f");

        // ── file-load handlers ─────────────────────────────────────────
        csvBtn.setOnAction(e -> {
            File f = chooseFile(dialog, "Import from CSV", "CSV Files", "*.csv");
            if (f != null) loadPreview(f, "CSV", cardRows, importBtn, mc);
        });

        jsonBtn.setOnAction(e -> {
            File f = chooseFile(dialog, "Import from JSON", "JSON Files", "*.json");
            if (f != null) loadPreview(f, "JSON", cardRows, importBtn, mc);
        });

        // ── cancel handler ─────────────────────────────────────────────
        cancelBtn.setOnAction(e -> {
            if (confirmCancel(dialog)) dialog.close();
        });

        // ── IMPORT TO SYSTEM handler ───────────────────────────────────
        importBtn.setOnAction(e -> {

            List<CardRowData> selected = cardRows.stream()
                .filter(CardRowData::isSelected)
                .toList();

            // Validate: no selected row may have a blank question or answer
            long blankCards = selected.stream()
                .filter(r -> r.getQuestion() == null || r.getQuestion().isBlank()
                          || r.getAnswer()   == null || r.getAnswer().isBlank())
                .count();

            if (blankCards > 0) {
                customWarning(dialog,
                    "Invalid\nCards!",
                    blankCards + " selected card(s) have a blank question or answer.\n"
                    + "Fill in or deselect them before importing.");
                return;
            }

            // Validate: every selected row must have a difficulty assigned
            long missingDiff = selected.stream()
                .filter(r -> r.getDifficulty() == null || r.getDifficulty().isBlank())
                .count();

            if (missingDiff > 0) {
                customWarning(dialog,
                    "Missing\nDifficulty!",
                    missingDiff + " selected card(s) have no difficulty set.\n"
                    + "Assign Easy, Medium, or Hard on each affected row before importing.");
                return;   // stay on the dialog so the user can fix it
            }

            // Create cards in memory (no DB write yet)
            try {
                if (existingRadio.isSelected()) {
                    Deck deck = existingCombo.getValue();
                    if (deck == null) {
                        MainFrame.showErrorDialog("Please select an existing deck.");
                        return;
                    }
                    mc.importCardsToExistingDeck(deck.getDeckID(), toCardJsonList(selected));
                } else {
                    String deckName = nameField.getText().trim();
                    if (deckName.isEmpty()) {
                        MainFrame.showErrorDialog("Please enter a deck name.");
                        return;
                    }
                    mc.importCardsToNewDeck(deckName, descField.getText().trim(), toCardJsonList(selected));
                }
            } catch (CustomException ex) {
                MainFrame.showErrorDialog("Import failed: " + ex.getMessage());
                return;
            }

            // Persist and navigate back to the deck dashboard
            dialog.close();
            MainFrame.runSaveTask(
                mainLayout.getScene().getWindow(),
                mc,
                "Saving imported cards...",
                () -> {
                    MainFrame.showSuccessDialog(selected.size() + " card(s) imported successfully!");
                    mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
                },
                errorMsg -> {
                    MainFrame.showErrorDialog("Autosave failed: " + errorMsg);
                    mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
                }
            );
        });

        left.getChildren().addAll(
            addToDeckLbl,
            existingRadio, existingCombo,
            newDeckRadio, nameField, descField,
            csvBtn, jsonBtn
        );

        // ════════════════════════════════════════
        // RIGHT PANEL: preview area
        // ════════════════════════════════════════
        VBox right = new VBox(8);
        HBox.setHgrow(right, Priority.ALWAYS);

        // "SELECT ALL" row
        CheckBox selectAllCb = new CheckBox("SELECT ALL");
        selectAllCb.setFont(UiScale.bodyFont(15));
        selectAllCb.setTextFill(Color.web(PRIMARY_BLUE));
        HBox selectAllRow = new HBox(selectAllCb);
        selectAllRow.setAlignment(Pos.CENTER_LEFT);
        selectAllRow.setPadding(new Insets(0, 0, 4, 4));

        // Placeholder (shown before any file is loaded)
        StackPane placeholder = new StackPane();
        placeholder.setStyle(previewBoxStyle());
        VBox.setVgrow(placeholder, Priority.ALWAYS);
        Label placeholderLbl = new Label("-QUESTIONS WILL BE PREVIEWED HERE-");
        placeholderLbl.setFont(UiScale.bodyFont(18));
        placeholderLbl.setTextFill(Color.web("#8888b8"));
        placeholder.getChildren().add(placeholderLbl);

        // Table (hidden until cards are loaded)
        TableView<CardRowData> table = buildTable(cardRows, selectAllCb, importBtn);
        table.setVisible(false);
        table.setManaged(false);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Swap placeholder ↔ table when cardRows changes
        cardRows.addListener((ListChangeListener<CardRowData>) c -> {
            boolean hasRows = !cardRows.isEmpty();
            placeholder.setVisible(!hasRows);
            placeholder.setManaged(!hasRows);
            table.setVisible(hasRows);
            table.setManaged(hasRows);
        });

        HBox actionRow = new HBox(16, importBtn, cancelBtn);
        HBox.setHgrow(importBtn, Priority.ALWAYS);
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        actionRow.setPadding(new Insets(12, 0, 0, 0));

        right.getChildren().addAll(selectAllRow, placeholder, table, actionRow);

        body.getChildren().addAll(left, right);
        root.getChildren().addAll(titleLbl, body);

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.setMaximized(true);
        dialog.showAndWait();
    }

    // ────────────────────────────────────────────────────────────────
    // Table builder
    // ────────────────────────────────────────────────────────────────

    /**
     * Builds the preview {@link TableView} with four columns:
     * {@code □ | QUESTION (editable) | ANSWER (editable) | DIFFICULTY (dropdown)}.
     *
     * <p>Edits to the question and answer fields are reflected immediately in the
     * backing {@link CardRowData} via bidirectional binding — no commit step needed.
     *
     * @param rows        observable list that backs the table
     * @param selectAllCb the "SELECT ALL" checkbox for bulk selection
     * @param importBtn   button whose disabled state tracks whether any row is selected
     */
    private static TableView<CardRowData> buildTable(
            ObservableList<CardRowData> rows,
            CheckBox selectAllCb,
            Button importBtn) {

        TableView<CardRowData> table = new TableView<>(rows);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(-1);
        table.setStyle(
            "-fx-background-color: " + PANEL_BG + "; " +
            "-fx-border-color: " + PANEL_BORDER + "; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-control-inner-background: " + PANEL_BG + ";"
        );

        // ── checkbox column ────────────────────────────────────────────
        TableColumn<CardRowData, Boolean> cbCol = new TableColumn<>("");
        cbCol.setPrefWidth(46);
        cbCol.setMinWidth(46);
        cbCol.setMaxWidth(46);
        cbCol.setSortable(false);
        cbCol.setEditable(true);
        cbCol.setCellValueFactory(cell -> cell.getValue().selectedProperty());
        cbCol.setCellFactory(CheckBoxTableCell.forTableColumn(cbCol));

        // ── question column ────────────────────────────────────────────
        TableColumn<CardRowData, String> qCol = new TableColumn<>("QUESTION");
        qCol.setSortable(false);
        qCol.setCellValueFactory(cell -> cell.getValue().questionProperty());
        qCol.setCellFactory(col -> alwaysEditableTextCell(CardRowData::questionProperty));

        // ── answer column ──────────────────────────────────────────────
        TableColumn<CardRowData, String> aCol = new TableColumn<>("ANSWER");
        aCol.setSortable(false);
        aCol.setCellValueFactory(cell -> cell.getValue().answerProperty());
        aCol.setCellFactory(col -> alwaysEditableTextCell(CardRowData::answerProperty));

        // ── difficulty column ──────────────────────────────────────────
        TableColumn<CardRowData, String> dCol = new TableColumn<>("DIFFICULTY");
        dCol.setPrefWidth(160);
        dCol.setMinWidth(140);
        dCol.setMaxWidth(180);
        dCol.setSortable(false);
        dCol.setCellValueFactory(cell -> cell.getValue().difficultyProperty());
        dCol.setCellFactory(col -> difficultyComboCell());

        table.getColumns().addAll(cbCol, qCol, aCol, dCol);

        // SELECT ALL bulk-toggles every row's selected state
        selectAllCb.setOnAction(e -> {
            boolean all = selectAllCb.isSelected();
            rows.forEach(r -> r.selectedProperty().set(all));
            importBtn.setDisable(!all || rows.isEmpty());
        });

        // Attach a per-row listener whenever new rows arrive (including via setAll())
        // so the import button tracks selection count correctly.
        rows.addListener((ListChangeListener<CardRowData>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (CardRowData row : change.getAddedSubList()) {
                        row.selectedProperty().addListener((obs, old, nv) ->
                            importBtn.setDisable(rows.stream().noneMatch(CardRowData::isSelected)));
                    }
                }
            }
        });

        return table;
    }

    /**
     * Creates a {@link TableCell} whose visual is always a {@link TextField} — the cell
     * is never in a plain-text "view" mode.  Edits flow directly into the model via a
     * bidirectional binding that is re-established whenever the cell is recycled for a
     * different row.
     *
     * @param propGetter extracts the {@link StringProperty} to bind from a row object
     */
    private static TableCell<CardRowData, String> alwaysEditableTextCell(
            Function<CardRowData, StringProperty> propGetter) {

        return new TableCell<CardRowData, String>() {

            private final TextArea ta = new TextArea();
            private CardRowData boundRow = null;

            {
                ta.setWrapText(true);
                ta.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: " + PANEL_BORDER + "; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; -fx-background-radius: 4; " +
                    UiScale.uiFontCss(17) + " -fx-padding: 4 6;"
                );
                ta.setMaxWidth(Double.MAX_VALUE);
                ta.setMinHeight(54);
                ta.textProperty().addListener((obs, old, nv) -> adjustHeight(nv));
            }

            private void adjustHeight(String text) {
                if (text == null) text = "";
                long explicit = text.chars().filter(c -> c == '\n').count();
                double approxWrapped = text.length() / 50.0;
                double lines = Math.max(1, explicit + 1 + approxWrapped);
                ta.setPrefHeight(Math.max(54, lines * 22 + 14));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                CardRowData row = (getTableRow() == null) ? null : getTableRow().getItem();

                if (empty || row == null) {
                    // Detach from the old row when the cell is cleared
                    if (boundRow != null) {
                        ta.textProperty().unbindBidirectional(propGetter.apply(boundRow));
                        boundRow = null;
                    }
                    setGraphic(null);
                    setText(null);
                } else {
                    // Re-bind only when the cell has been recycled for a different row
                    if (boundRow != row) {
                        if (boundRow != null) {
                            ta.textProperty().unbindBidirectional(propGetter.apply(boundRow));
                        }
                        boundRow = row;
                        ta.textProperty().bindBidirectional(propGetter.apply(row));
                    }
                    adjustHeight(ta.getText());
                    setGraphic(ta);
                    setText(null);
                }
            }
        };
    }

    /**
     * Creates a {@link TableCell} that always shows a difficulty {@link ComboBox} with:
     * "--Select Difficulty--", "Easy", "Medium", "Hard" (in that order).
     *
     * <p>Selecting "--Select Difficulty--" stores {@code null} in the model.
     * Selecting any other option stores the string value directly.
     */
    private static TableCell<CardRowData, String> difficultyComboCell() {

        return new TableCell<CardRowData, String>() {

            private final ComboBox<String> combo = new ComboBox<>();
            private CardRowData boundRow = null;

            {
                combo.getItems().addAll("--Select Difficulty--", "Easy", "Medium", "Hard");
                combo.setMaxWidth(Double.MAX_VALUE);
                combo.setStyle(
                    UiScale.uiFontCss(17) +
                    " -fx-background-color: white; " +
                    "-fx-border-color: " + PANEL_BORDER + "; " +
                    "-fx-border-radius: 4;"
                );
                combo.setOnAction(e -> {
                    if (boundRow != null) {
                        String val = combo.getValue();
                        boundRow.difficultyProperty().set(
                            "--Select Difficulty--".equals(val) ? null : val);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                CardRowData row = (getTableRow() == null) ? null : getTableRow().getItem();

                if (empty || row == null) {
                    boundRow = null;
                    setGraphic(null);
                    setText(null);
                } else {
                    boundRow = row;
                    String display = (item == null || item.isBlank())
                        ? "--Select Difficulty--" : item;
                    // Guard against triggering onAction while syncing the displayed value
                    if (!display.equals(combo.getValue())) {
                        combo.setValue(display);
                    }
                    setGraphic(combo);
                    setText(null);
                }
            }
        };
    }

    // ────────────────────────────────────────────────────────────────
    // Preview loading
    // ────────────────────────────────────────────────────────────────

    /**
     * Parses {@code file} via the appropriate service and replaces {@code cardRows}
     * with the resulting preview data.  The {@link ListChangeListener} wired in
     * {@link #buildTable} then attaches selection listeners to each new row.
     *
     * @param file      the file to parse
     * @param format    {@code "CSV"} or {@code "JSON"}
     * @param cardRows  observable list to populate (existing contents are replaced)
     * @param importBtn button whose disabled state depends on row selection
     * @param mc        controller that delegates to the service layer
     */
    private static void loadPreview(
            File file, String format,
            ObservableList<CardRowData> cardRows,
            Button importBtn,
            MainController mc) {

        try {
            List<CardJson> cards = "CSV".equals(format)
                ? mc.previewCsvCards(file)
                : mc.previewJsonCards(file);

            List<CardRowData> newRows = new ArrayList<>();
            for (CardJson c : cards) {
                newRows.add(new CardRowData(c.getQuestion(), c.getAnswer(), c.getDifficulty()));
            }
            cardRows.setAll(newRows);   // triggers ListChangeListener → per-row listeners attached

            // Reset the import button (no rows selected yet after a fresh load)
            importBtn.setDisable(true);

            if (cardRows.isEmpty()) {
                MainFrame.showErrorDialog(
                    "No valid questions found in the selected file.\n" +
                    "Ensure each row has a non-empty question and answer.");
            }
        } catch (CustomException ex) {
            MainFrame.showErrorDialog("Failed to load file: " + ex.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────
    // Small helpers
    // ────────────────────────────────────────────────────────────────

    /** Opens a file-chooser dialog and returns the chosen file, or {@code null} if cancelled. */
    private static File chooseFile(Stage owner, String title, String filterDesc, String ext) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterDesc, ext));
        return fc.showOpenDialog(owner);
    }

    // ── styled popup dialogs (match ExitPanel aesthetic) ─────────────────

    /**
     * Styled confirmation dialog — returns {@code true} if the user clicks Confirm.
     */
    private static boolean confirmCancel(Stage owner) {
        return customConfirm(owner,
            "Cancel\nImport?",
            "Any loaded questions will be discarded. Are you sure?",
            "STAY", "YES, EXIT");
    }

    private static boolean customConfirm(Stage owner, String title, String message,
                                         String cancelLabel, String confirmLabel) {
        boolean[] result = {false};

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox container = popupContainer();
        double[] drag = {0, 0};
        container.setOnMousePressed(ev -> { drag[0] = ev.getSceneX(); drag[1] = ev.getSceneY(); });
        container.setOnMouseDragged(ev -> {
            dialog.setX(ev.getScreenX() - drag[0]);
            dialog.setY(ev.getScreenY() - drag[1]);
        });

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getChildren().add(popupXButton(() -> dialog.close()));
        VBox.setMargin(topBar, new Insets(5, -25, 0, 0));

        Label titleLbl = new Label(title);
        titleLbl.setFont(UiScale.titleFont(42));
        titleLbl.setTextFill(Color.web(PRIMARY_BLUE));
        titleLbl.setWrapText(true);

        Label msgLbl = new Label(message);
        msgLbl.setFont(UiScale.bodyFont(16));
        msgLbl.setTextFill(Color.web(PRIMARY_BLUE));
        msgLbl.setWrapText(true);
        VBox.setMargin(msgLbl, new Insets(15, 10, 30, 0));

        Button cancelBtn = popupButton(cancelLabel, "#c5cae9", "#b3b9e0");
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = popupButton(confirmLabel, "#ff9999", "#ff6666");
        confirmBtn.setOnAction(e -> { result[0] = true; dialog.close(); });

        VBox buttons = new VBox(10, cancelBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER);

        container.getChildren().addAll(topBar, titleLbl, msgLbl, buttons);

        Scene scene = new Scene(container, 300, 490);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
        return result[0];
    }

    private static void customWarning(Stage owner, String title, String message) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox container = popupContainer();
        double[] drag = {0, 0};
        container.setOnMousePressed(ev -> { drag[0] = ev.getSceneX(); drag[1] = ev.getSceneY(); });
        container.setOnMouseDragged(ev -> {
            dialog.setX(ev.getScreenX() - drag[0]);
            dialog.setY(ev.getScreenY() - drag[1]);
        });

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getChildren().add(popupXButton(() -> dialog.close()));
        VBox.setMargin(topBar, new Insets(5, -25, 0, 0));

        Label titleLbl = new Label(title);
        titleLbl.setFont(UiScale.titleFont(42));
        titleLbl.setTextFill(Color.web(PRIMARY_BLUE));
        titleLbl.setWrapText(true);

        Label msgLbl = new Label(message);
        msgLbl.setFont(UiScale.bodyFont(16));
        msgLbl.setTextFill(Color.web(PRIMARY_BLUE));
        msgLbl.setWrapText(true);
        VBox.setMargin(msgLbl, new Insets(15, 10, 30, 0));

        Button okBtn = popupButton("GOT IT", "#ff9999", "#ff6666");
        okBtn.setOnAction(e -> dialog.close());

        VBox buttons = new VBox(10, okBtn);
        buttons.setAlignment(Pos.CENTER);

        container.getChildren().addAll(topBar, titleLbl, msgLbl, buttons);

        Scene scene = new Scene(container, 300, 440);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static VBox popupContainer() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(0, 35, 35, 35));
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + PRIMARY_BLUE + ";" +
            "-fx-border-radius: 12;"
        );
        return box;
    }

    private static Button popupXButton(Runnable onClose) {
        Button btn = new Button("X");
        String normal = "-fx-background-color: transparent; -fx-text-fill: #1A438E;" +
                        " -fx-font-size: " + (int) UiScale.size(20) + "px; -fx-cursor: hand;";
        String hover  = "-fx-background-color: transparent; -fx-text-fill: red;" +
                        " -fx-font-size: " + (int) UiScale.size(20) + "px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        btn.setOnAction(e -> onClose.run());
        return btn;
    }

    private static Button popupButton(String text, String normalColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(230);
        btn.setPrefHeight(45);
        String normal = "-fx-background-color: " + normalColor + "; -fx-text-fill: " + PRIMARY_BLUE + "; " +
                        UiScale.buttonFontCss(17) +
                        " -fx-background-radius: 25; -fx-cursor: hand;";
        String hover  = "-fx-background-color: " + hoverColor  + "; -fx-text-fill: " + PRIMARY_BLUE + "; " +
                        UiScale.buttonFontCss(17) +
                        " -fx-background-radius: 25; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        return btn;
    }

    /** Converts a list of selected {@link CardRowData} objects to plain {@link CardJson} DTOs. */
    private static List<CardJson> toCardJsonList(List<CardRowData> rows) {
        List<CardJson> out = new ArrayList<>();
        for (CardRowData r : rows) {
            out.add(new CardJson(r.getQuestion(), r.getAnswer(), r.getDifficulty()));
        }
        return out;
    }

    /** Creates a reusable {@link ListCell} that displays a {@link Deck} by name. */
    private static ListCell<Deck> deckCell() {
        return new ListCell<Deck>() {
            @Override
            protected void updateItem(Deck item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }

    // ── inline CSS string helpers ──────────────────────────────────

    private static String comboStyle() {
        return "-fx-background-color: white; " +
               "-fx-border-color: " + PANEL_BORDER + "; " +
               "-fx-border-width: 1.5; -fx-border-radius: 5; -fx-background-radius: 5; " +
               UiScale.uiFontCss(15);
    }

    private static String inputStyle() {
        return "-fx-background-color: white; " +
               "-fx-border-color: " + PANEL_BORDER + "; " +
               "-fx-border-width: 1.5; -fx-border-radius: 5; -fx-background-radius: 5; " +
               UiScale.uiFontCss(15) + " -fx-padding: 6 8;";  
    }

    private static String previewBoxStyle() {
        return "-fx-background-color: " + PANEL_BG + "; " +
               "-fx-border-color: " + PANEL_BORDER + "; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 10; -fx-background-radius: 10;";
    }

    /**
     * Creates a rounded pill-shaped button in the "IMPORT FROM CSV / JSON" style.
     */
    private static Button fileImportButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);
        String normal = "-fx-background-color: #d5d5e8; -fx-text-fill: " + PRIMARY_BLUE + "; " +
                        UiScale.buttonFontCss(15) +
                        " -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 6 14;";
        String hover  = "-fx-background-color: #bbbbd8; -fx-text-fill: " + PRIMARY_BLUE + "; " +
                        UiScale.buttonFontCss(15) +
                        " -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 6 14;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(normal));
        return btn;
    }

    /**
     * Creates a large pill-shaped action button (e.g., "IMPORT TO SYSTEM" / "CANCEL").
     * Automatically applies a greyed-out disabled style when the button is disabled.
     *
     * @param text         button label
     * @param normalColour fill colour (hex) when enabled and not hovered
     * @param hoverColour  fill colour (hex) on hover
     */
    private static Button actionButton(String text, String normalColour, String hoverColour) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(62);
        String normal   = "-fx-background-color: " + normalColour + "; -fx-text-fill: white; " +
                          UiScale.buttonFontCss(20) +
                          " -fx-background-radius: 28; -fx-cursor: hand;";
        String hover    = "-fx-background-color: " + hoverColour  + "; -fx-text-fill: white; " +
                          UiScale.buttonFontCss(20) +
                          " -fx-background-radius: 28; -fx-cursor: hand;";
        String disabled = "-fx-background-color: #b0b0b0; -fx-text-fill: #e8e8e8; " +
                          UiScale.buttonFontCss(20) +
                          " -fx-background-radius: 28;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) btn.setStyle(hover);  });
        btn.setOnMouseExited(e  -> { if (!btn.isDisabled()) btn.setStyle(normal); });
        btn.disabledProperty().addListener((obs, old, dis) ->
            btn.setStyle(dis ? disabled : normal));
        return btn;
    }
}