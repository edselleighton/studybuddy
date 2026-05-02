package com.studyapp.view;

import java.io.File;
import java.util.List;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyDeckPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String TOOLBAR_BUTTON_STYLE = "-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-text-fill: black; -fx-padding: 5 20; -fx-cursor: hand;";
    private static final String TOOLBAR_BUTTON_HOVER_STYLE = "-fx-background-color: #eafbf1; -fx-border-color: #22c55e; -fx-border-radius: 5; -fx-text-fill: black; -fx-padding: 5 20; -fx-cursor: hand;";
    private static final String DECK_ROW_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 15; -fx-cursor: hand;";
    private static final String DECK_ROW_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 15; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_STYLE = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_HOVER_STYLE = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 8 20; -fx-cursor: hand;";
    private static final String DIALOG_BG = "#f8fafc";

    // ── NEW: page size constant ──────────────────────────────────────────────
    private static final int PAGE_SIZE = 5;
    private static double Xoffset = 0;
    private static double Yoffset = 0;

    public static VBox create(BorderPane mainLayout, MainController mc) {
        return create(mainLayout, "Manage decks, or import/export your deck data as JSON or CSV.", PRIMARY_BLUE, mc);
    }

    public static VBox create(BorderPane mainLayout, String statusMessage, String statusColor, MainController mc) {
        List<Deck> decks = mc.allDecks();

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("My Decks");
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button newBtn = createToolbarButton("New");
        Button importBtn = createToolbarButton("Import");
        Button exportBtn = createToolbarButton("Export");

        newBtn.setOnAction(e -> showCreateDeckDialog(mainLayout, mc));

        importBtn.setOnAction(e -> {
            String format = showImportFormatDialog(mainLayout);
            if (format == null) {
                return;
            }

            FileChooser fc = new FileChooser();
            if ("CSV".equals(format)) {
                fc.setTitle("Import Decks from CSV");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            } else {
                fc.setTitle("Import Decks from JSON");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            }

            File file = fc.showOpenDialog(mainLayout.getScene().getWindow());

            if (file != null) {
                try {
                    int count = "CSV".equals(format) ? mc.importFromCsv(file) : mc.importFromJson(file);
                    if (count > 0) {
                        mainLayout.setCenter(MyDeckPanel.create(
                                mainLayout,
                                count + " deck(s) imported successfully! Remember to save your changes.",
                                "#22c55e",
                                mc));
                    } else {
                        mainLayout.setCenter(MyDeckPanel.create(
                                mainLayout,
                                "No new decks were imported. The selected file may contain duplicate deck names or no valid decks.",
                                "#d97706",
                                mc));
                    }
                } catch (CustomException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Import Error");
                    alert.setHeaderText("Failed to import decks");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        exportBtn.setOnAction(e -> {
            List<Deck> allDecks = mc.allDecks();
            if (!allDecks.isEmpty()) {
                showExportDeckDialog(mainLayout, mc, allDecks);
            } else {
                MainFrame.showErrorDialog("No decks to export. Please add decks before exporting.");
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search decks");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");

        Label searchIcon = new Label("Search");
        searchIcon.setFont(Font.font("Serif", 14));
        searchIcon.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(Font.font("Serif", 16));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Name");
        sortCombo.setValue("Newest");
        sortCombo.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 0;");
        sortCombo.setPrefWidth(120);

        toolbar.getChildren().addAll(newBtn, importBtn, exportBtn, searchField, searchIcon, spacer, sortLabel, sortCombo);

        Label statusLabel = new Label(statusMessage);
        statusLabel.setFont(Font.font("Serif", 15));
        statusLabel.setTextFill(Color.web(statusColor));
        statusLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deckList = new VBox(15);
        deckList.setPadding(new Insets(5, 15, 5, 5));
        deckList.setStyle("-fx-background-color: white;");

        // ── NEW: shared mutable page tracker ────────────────────────────────
        int[] currentPage = {0};

        updateDeckList(deckList, decks, "", "Newest", currentPage[0], mainLayout, mc);

        // Search listener — reset to page 0 on new query
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentPage[0] = 0;
            updateDeckList(deckList, decks, newValue, sortCombo.getValue(), currentPage[0], mainLayout, mc);
        });

        // Sort listener — reset to page 0 on new sort
        sortCombo.setOnAction(e -> {
            currentPage[0] = 0;
            updateDeckList(deckList, decks, searchField.getText(), sortCombo.getValue(), currentPage[0], mainLayout, mc);
        });

        scrollPane.setContent(deckList);
        mainContent.getChildren().addAll(header, toolbar, statusLabel, scrollPane);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    // ── UPDATED: added pageIndex param; builds deck slice + pagination bar ──
    private static void updateDeckList(VBox deckList, List<Deck> allDecks, String searchQuery,
                                       String sortOption, int pageIndex,
                                       BorderPane mainLayout, MainController mc) {
        String query = searchQuery == null ? "" : searchQuery.toLowerCase().trim();

        List<Deck> filteredDecks = allDecks.stream()
                .filter(deck -> {
                    if (query.isEmpty()) return true;
                    String name = deck.getName().toLowerCase();
                    String desc = deck.getDescription() == null ? "" : deck.getDescription().toLowerCase();
                    return name.contains(query) || desc.contains(query);
                })
                .collect(java.util.stream.Collectors.toList());

        switch (sortOption) {
            case "Oldest":
                filteredDecks.sort(java.util.Comparator.comparing(Deck::getDeckID));
                break;
            case "Name":
                filteredDecks.sort(java.util.Comparator.comparing(Deck::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Newest":
            default:
                filteredDecks.sort(java.util.Comparator.comparing(Deck::getDeckID).reversed());
                break;
        }

        deckList.getChildren().clear();

        if (filteredDecks.isEmpty()) {
            Label emptyLabel = new Label("No decks found");
            emptyLabel.setFont(Font.font("Serif", 16));
            emptyLabel.setTextFill(Color.GRAY);
            emptyLabel.setPadding(new Insets(20));
            deckList.getChildren().add(emptyLabel);
            return;
        }

        // ── Slice to current page ────────────────────────────────────────────
        int totalPages = (int) Math.ceil((double) filteredDecks.size() / PAGE_SIZE);
        int safePage   = Math.max(0, Math.min(pageIndex, totalPages - 1));
        int fromIndex  = safePage * PAGE_SIZE;
        int toIndex    = Math.min(fromIndex + PAGE_SIZE, filteredDecks.size());

        List<Deck> pageDecks = filteredDecks.subList(fromIndex, toIndex);
        for (Deck deck : pageDecks) {
            deckList.getChildren().add(createDeckItem(deck, mainLayout, mc));
        }

        // ── Pagination bar ───────────────────────────────────────────────────
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(10, 0, 5, 0));

        Button prevBtn = new Button("← Prev");
        prevBtn.setStyle(OPEN_BUTTON_STYLE);
        prevBtn.setDisable(safePage == 0);
        prevBtn.setOnMouseEntered(e -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        prevBtn.setOnMouseExited(e  -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_STYLE); });

        Label pageLabel = new Label("Page " + (safePage + 1) + " of " + totalPages);
        pageLabel.setFont(Font.font("Serif", 14));
        pageLabel.setTextFill(Color.web(PRIMARY_BLUE));

        Button nextBtn = new Button("Next →");
        nextBtn.setStyle(OPEN_BUTTON_STYLE);
        nextBtn.setDisable(safePage >= totalPages - 1);
        nextBtn.setOnMouseEntered(e -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        nextBtn.setOnMouseExited(e  -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_STYLE); });

        int[] pageRef = {safePage};

        prevBtn.setOnAction(e -> {
            pageRef[0]--;
            updateDeckList(deckList, allDecks, searchQuery, sortOption, pageRef[0], mainLayout, mc);
        });
        nextBtn.setOnAction(e -> {
            pageRef[0]++;
            updateDeckList(deckList, allDecks, searchQuery, sortOption, pageRef[0], mainLayout, mc);
        });

        pagination.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        deckList.getChildren().add(pagination);
    }

    // ── Everything below is unchanged ────────────────────────────────────────

    private static Button createToolbarButton(String text) {
        Button button = new Button(text);
        button.setStyle(TOOLBAR_BUTTON_STYLE);
        button.setOnMouseEntered(e -> button.setStyle(TOOLBAR_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(TOOLBAR_BUTTON_STYLE));
        return button;
    }

    private static HBox createDeckItem(Deck deck, BorderPane mainLayout, MainController mc) {
        HBox row = new HBox(20);
        row.setStyle(DECK_ROW_STYLE);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle(DECK_ROW_HOVER_STYLE));
        row.setOnMouseExited(e -> row.setStyle(DECK_ROW_STYLE));

        VBox leftInfo = new VBox(5);
        leftInfo.setPrefWidth(250);
        Label idLbl = new Label("ID: " + deck.getDeckID());
        idLbl.setFont(Font.font("Serif", 14));
        Label titleLbl = new Label(deck.getName());
        titleLbl.setFont(Font.font("Serif", 18));
        leftInfo.getChildren().addAll(idLbl, titleLbl);

        VBox middleInfo = new VBox(5);
        Label cardsLbl = new Label("Cards: " + mc.getFlashcardsByDeck(deck.getDeckID()).size());
        cardsLbl.setFont(Font.font("Serif", 14));
        Label progLbl = new Label(String.format("Progress: %d%%", mc.getDeckProgress(deck.getDeckID())));
        progLbl.setFont(Font.font("Serif", 14));
        middleInfo.getChildren().addAll(cardsLbl, progLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("OPEN");
        selectBtn.setStyle(OPEN_BUTTON_STYLE);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(OPEN_BUTTON_HOVER_STYLE));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(OPEN_BUTTON_STYLE));
        selectBtn.setOnAction(e -> DeckDetailPanel.show(
                mainLayout,
                deck,
                mc,
                () -> mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc))));

        row.getChildren().addAll(leftInfo, middleInfo, spacer, selectBtn);
        return row;
    }

    private static String showImportFormatDialog(BorderPane mainLayout) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Import Format");

        VBox container = createDialogContainer();
        HBox topBar = createDialogTopBar(dialog);

        Label title = new Label("Import Deck");
        title.setFont(Font.font("Serif", 38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label description = new Label("Choose which file type you want to import.");
        description.setFont(Font.font("Serif", 15));
        description.setTextFill(Color.web(PRIMARY_BLUE));
        description.setWrapText(true);

        final String[] selectedFormat = {null};

        Button jsonBtn = createDialogActionButton("JSON");
        jsonBtn.setOnAction(e -> {
            selectedFormat[0] = "JSON";
            dialog.close();
        });

        Button csvBtn = createDialogActionButton("CSV");
        csvBtn.setOnAction(e -> {
            selectedFormat[0] = "CSV";
            dialog.close();
        });

        VBox buttonBox = new VBox(12, jsonBtn, csvBtn);
        buttonBox.setAlignment(Pos.CENTER);

        container.getChildren().addAll(topBar, title, description, buttonBox);

        Scene scene = new Scene(container, 360, 300);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();

        return selectedFormat[0];
    }

    private static void showCreateDeckDialog(BorderPane mainLayout, MainController mc) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Create Deck");

        VBox container = new VBox(12);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: #2a548f; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: #f8fafc;");

        container.setOnMousePressed(event -> {
            Xoffset = event.getSceneX();
            Yoffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - Xoffset);
            stage.setY(event.getScreenY() - Yoffset);
        });

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

        Label title = new Label("Create Deck");
        title.setFont(Font.font("Serif", 38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label nameLabel = new Label("Enter Deck Name");
        nameLabel.setFont(Font.font("Serif", 17));
        nameLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextField nameField = new TextField();
        nameField.setPromptText("Deck name");
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: Serif; -fx-font-size: 14; -fx-padding: 5 10;");

        Label descLabel = new Label("Enter Description");
        descLabel.setFont(Font.font("Serif", 17));
        descLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description (optional)");
        descArea.setPrefRowCount(5);
        descArea.setPrefHeight(140);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-control-inner-background: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: Serif; -fx-font-size: 16; -fx-padding: 8;");

        Button createBtn = createDialogActionButton("CREATE");

        createBtn.setOnAction(e -> {
            String deckName = nameField.getText().trim();
            String description = descArea.getText().trim();

            try {
                mc.createDeck(deckName, description);
                dialog.close();
                MainFrame.showSuccessDialog("Deck '" + deckName + "' created successfully!");
                mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
            } catch (CustomException ex) {
                MainFrame.showErrorDialog("Creation failed: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(createBtn);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(title, new Insets(0, 0, 14, 0));
        VBox.setMargin(nameField, new Insets(0, 0, 8, 0));
        VBox.setMargin(descArea, new Insets(0, 0, 8, 0));
        VBox.setMargin(buttonBox, new Insets(18, 0, 0, 0));

        container.getChildren().addAll(topBar, title, nameLabel, nameField, descLabel, descArea, buttonBox);

        Scene scene = new Scene(container, 360, 540);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static void showExportDeckDialog(BorderPane mainLayout, MainController mc, List<Deck> allDecks) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Export Deck");

        VBox container = new VBox(12);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: #2a548f; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: #f8fafc;");

        container.setOnMousePressed(event -> {
            Xoffset = event.getSceneX();
            Yoffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - Xoffset);
            stage.setY(event.getScreenY() - Yoffset);
        });

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

        Label title = new Label("Export Deck");
        title.setFont(Font.font("Serif", 38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label deckLabel = new Label("Choose Deck:");
        deckLabel.setFont(Font.font("Serif", 17));
        deckLabel.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<Deck> deckCombo = new ComboBox<>();
        deckCombo.getItems().addAll(allDecks);
        deckCombo.setMaxWidth(Double.MAX_VALUE);
        deckCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: Serif; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");
        deckCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Deck item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName().toUpperCase());
            }
        });
        deckCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Deck item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName().toUpperCase());
            }
        });
        deckCombo.setValue(allDecks.get(0));

        Label formatLabel = new Label("File Type");
        formatLabel.setFont(Font.font("Serif", 17));
        formatLabel.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("JSON", "CSV");
        formatCombo.setValue("JSON");
        formatCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: Serif; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox formatRow = new HBox(12, formatLabel, new Region(), formatCombo);
        formatRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(formatRow.getChildren().get(1), Priority.ALWAYS);

        Button exportBtn = createDialogActionButton("EXPORT");
        exportBtn.setOnAction(e -> {
            String format = formatCombo.getValue();
            Deck selectedDeck = deckCombo.getValue();

            if (selectedDeck == null) {
                MainFrame.showErrorDialog("Please select a deck to export.");
                return;
            }

            FileChooser fc = new FileChooser();
            if ("CSV".equals(format)) {
                fc.setTitle("Save Deck as CSV");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                fc.setInitialFileName(selectedDeck.getName() + ".csv");
            } else {
                fc.setTitle("Save Deck as JSON");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                fc.setInitialFileName(selectedDeck.getName() + ".json");
            }

            File file = fc.showSaveDialog(mainLayout.getScene().getWindow());
            if (file == null) {
                return;
            }

            try {
                if ("CSV".equals(format)) {
                    mc.exportDeckToCsv(selectedDeck.getDeckID(), file);
                } else {
                    mc.exportDeckToJson(selectedDeck.getDeckID(), file);
                }
                dialog.close();
                MainFrame.showSuccessDialog("Deck exported to: " + file.getName());
            } catch (CustomException ex) {
                MainFrame.showErrorDialog("Export failed: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(exportBtn);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(title, new Insets(0, 0, 14, 0));
        VBox.setMargin(deckCombo, new Insets(0, 0, 8, 0));
        VBox.setMargin(formatRow, new Insets(2, 0, 8, 0));
        VBox.setMargin(buttonBox, new Insets(22, 0, 0, 0));

        container.getChildren().addAll(topBar, title, deckLabel, deckCombo, formatRow, buttonBox);

        Scene scene = new Scene(container, 360, 380);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static Button createDialogActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setPrefHeight(56);
        String normalStyle = "-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; " +
                "-fx-font-size: 17; -fx-font-family: Serif; -fx-background-radius: 28; " +
                "-fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; " +
                "-fx-font-size: 17; -fx-font-family: Serif; -fx-background-radius: 28; " +
                "-fx-cursor: hand;";
        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        return button;
    }

    private static VBox createDialogContainer() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: #2a548f; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: " + DIALOG_BG + ";");

        container.setOnMousePressed(event -> {
            Xoffset = event.getSceneX();
            Yoffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - Xoffset);
            stage.setY(event.getScreenY() - Yoffset);
        });

        return container;
    }

    private static HBox createDialogTopBar(Stage dialog) {
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

        return topBar;
    }
}
