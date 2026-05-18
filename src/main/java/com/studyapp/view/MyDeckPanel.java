package com.studyapp.view;

import com.studyapp.util.UiScale;

import java.io.File;
import java.util.List;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
    private static final String TOOLBAR_BUTTON_STYLE = "-fx-background-color: white; -fx-border-color: #22c55e; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: black; -fx-padding: 10 28; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String TOOLBAR_BUTTON_HOVER_STYLE = "-fx-background-color: #eafbf1; -fx-border-color: #22c55e; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: black; -fx-padding: 10 28; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String DECK_ROW_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 20 22; -fx-cursor: hand;";
    private static final String DECK_ROW_HOVER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-color: #f8fbff; -fx-padding: 20 22; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_STYLE = "-fx-background-color: #e6eaf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 24; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String OPEN_BUTTON_HOVER_STYLE = "-fx-background-color: #d0dcf5; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: black; -fx-padding: 10 24; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String TOOLBAR_INPUT_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-background-color: white; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-padding: 6 12;";
    private static final String DIALOG_BG = "#f8fafc";
    private static final Insets PAGE_PADDING = new Insets(12);
    private static final Insets CONTENT_PADDING = new Insets(14);
    private static final int CONTENT_SPACING = 12;

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
        wrapper.setPadding(PAGE_PADDING);
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(CONTENT_SPACING);
        mainContent.setPadding(CONTENT_PADDING);
        mainContent.setStyle(BORDER_STYLE);
        mainContent.setMaxWidth(Double.MAX_VALUE);
        mainContent.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label("My Decks");
        header.setFont(UiScale.titleFont(64));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE + "; -fx-background-radius: 8; -fx-padding: 10;");

        HBox toolbar = new HBox(18);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(4, 0, 6, 0));

        Button newBtn = createToolbarButton("New");
        Button importBtn = createToolbarButton("Import Cards");
        Button exportBtn = createToolbarButton("Export Deck");

        newBtn.setOnAction(e -> showCreateDeckDialog(mainLayout, mc));

                // Opens the full Import Questions dialog, which handles file selection,
        // preview, deck assignment, and post-import save/navigation internally.
        importBtn.setOnAction(e -> ImportDialogPanel.show(mainLayout, mc));

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
        searchField.setPrefWidth(UiScale.size(340));
        searchField.setPrefHeight(UiScale.size(44));
        searchField.setStyle(TOOLBAR_INPUT_STYLE);

        Label searchIcon = new Label("Search");
        searchIcon.setFont(UiScale.bodyFont(20));
        searchIcon.setTextFill(Color.web(PRIMARY_BLUE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setFont(UiScale.bodyFont(20));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Newest", "Oldest", "Name");
        sortCombo.setValue("Newest");
        sortCombo.setStyle(TOOLBAR_INPUT_STYLE);
        sortCombo.setPrefWidth(UiScale.size(180));
        sortCombo.setPrefHeight(UiScale.size(44));

        toolbar.getChildren().addAll(newBtn, importBtn, exportBtn, searchField, searchIcon, spacer, sortLabel, sortCombo);

        Label statusLabel = new Label(statusMessage);
        statusLabel.setFont(UiScale.bodyFont(15));
        statusLabel.setTextFill(Color.web(statusColor));
        statusLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-control-inner-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deckList = new VBox(15);
        deckList.setPadding(new Insets(5, 15, 5, 5));
        deckList.setStyle("-fx-background-color: white;");

        HBox paginationBar = new HBox(10);
        paginationBar.setAlignment(Pos.CENTER);
        paginationBar.setPadding(new Insets(10, 0, 4, 0));

        // ── NEW: shared mutable page tracker ────────────────────────────────
        int[] currentPage = {0};

        updateDeckList(deckList, paginationBar, decks, "", "Newest", currentPage[0], mainLayout, mc);

        // Search listener — reset to page 0 on new query
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentPage[0] = 0;
            updateDeckList(deckList, paginationBar, decks, newValue, sortCombo.getValue(), currentPage[0], mainLayout, mc);
        });

        // Sort listener — reset to page 0 on new sort
        sortCombo.setOnAction(e -> {
            currentPage[0] = 0;
            updateDeckList(deckList, paginationBar, decks, searchField.getText(), sortCombo.getValue(), currentPage[0], mainLayout, mc);
        });

        scrollPane.setContent(deckList);
        mainContent.getChildren().addAll(header, toolbar, statusLabel, scrollPane, paginationBar);
        wrapper.getChildren().add(mainContent);

        return wrapper;
    }

    // ── UPDATED: added pageIndex param; builds deck slice + pagination bar ──
    private static void updateDeckList(VBox deckList, HBox paginationBar, List<Deck> allDecks, String searchQuery,
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
        paginationBar.getChildren().clear();

        if (filteredDecks.isEmpty()) {
            Label emptyLabel = new Label("No decks found");
            emptyLabel.setFont(UiScale.bodyFont(16));
            emptyLabel.setTextFill(Color.GRAY);
            emptyLabel.setPadding(new Insets(20));
            deckList.getChildren().add(emptyLabel);
            paginationBar.setVisible(false);
            paginationBar.setManaged(false);
            return;
        }

        paginationBar.setVisible(true);
        paginationBar.setManaged(true);

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
        Button prevBtn = new Button("< Prev");
        prevBtn.setStyle(OPEN_BUTTON_STYLE);
        prevBtn.setDisable(safePage == 0);
        prevBtn.setOnMouseEntered(e -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        prevBtn.setOnMouseExited(e  -> { if (!prevBtn.isDisabled()) prevBtn.setStyle(OPEN_BUTTON_STYLE); });

        Label pageLabel = new Label("Page " + (safePage + 1) + " of " + totalPages);
        pageLabel.setFont(UiScale.bodyFont(18));
        pageLabel.setTextFill(Color.web(PRIMARY_BLUE));

        Button nextBtn = new Button("Next >");
        nextBtn.setStyle(OPEN_BUTTON_STYLE);
        nextBtn.setDisable(safePage >= totalPages - 1);
        nextBtn.setOnMouseEntered(e -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_HOVER_STYLE); });
        nextBtn.setOnMouseExited(e  -> { if (!nextBtn.isDisabled()) nextBtn.setStyle(OPEN_BUTTON_STYLE); });

        prevBtn.setOnAction(e -> {
            updateDeckList(deckList, paginationBar, allDecks, searchQuery, sortOption, safePage - 1, mainLayout, mc);
        });
        nextBtn.setOnAction(e -> {
            updateDeckList(deckList, paginationBar, allDecks, searchQuery, sortOption, safePage + 1, mainLayout, mc);
        });

        paginationBar.getChildren().addAll(prevBtn, pageLabel, nextBtn);
    }

    // ── Everything below is unchanged ────────────────────────────────────────

    private static Button createToolbarButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(UiScale.size(44));
        button.setStyle(TOOLBAR_BUTTON_STYLE);
        button.setOnMouseEntered(e -> button.setStyle(TOOLBAR_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(TOOLBAR_BUTTON_STYLE));
        return button;
    }

    private static HBox createDeckItem(Deck deck, BorderPane mainLayout, MainController mc) {
        HBox row = new HBox(24);
        row.setStyle(DECK_ROW_STYLE);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(UiScale.size(124));
        row.setOnMouseEntered(e -> row.setStyle(DECK_ROW_HOVER_STYLE));
        row.setOnMouseExited(e -> row.setStyle(DECK_ROW_STYLE));

        VBox leftInfo = new VBox(8);
        leftInfo.setPrefWidth(UiScale.size(330));
        Label idLbl = new Label("ID: " + deck.getDeckID());
        idLbl.setFont(UiScale.bodyFont(18));
        Label titleLbl = new Label(deck.getName());
        titleLbl.setFont(UiScale.headingFont(24));
        titleLbl.setWrapText(true);
        leftInfo.getChildren().addAll(idLbl, titleLbl);

        VBox middleInfo = new VBox(8);
        Label cardsLbl = new Label("Cards: " + mc.getFlashcardsByDeck(deck.getDeckID()).size());
        cardsLbl.setFont(UiScale.bodyFont(20));
        Label progLbl = new Label(String.format("Progress: %d%%", mc.getDeckProgress(deck.getDeckID())));
        progLbl.setFont(UiScale.bodyFont(20));
        middleInfo.getChildren().addAll(cardsLbl, progLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button selectBtn = new Button("OPEN");
        selectBtn.setPrefWidth(UiScale.size(120));
        selectBtn.setPrefHeight(UiScale.size(48));
        selectBtn.setStyle(OPEN_BUTTON_STYLE);
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(OPEN_BUTTON_HOVER_STYLE));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle(OPEN_BUTTON_STYLE));
        selectBtn.setOnAction(e -> DeckDetailPanel.show(
                mainLayout,
                deck,
                mc,
                () -> mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc))));

        HBox actions = new HBox(8, selectBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(leftInfo, middleInfo, spacer, actions);
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
        title.setFont(UiScale.headingFont(38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label description = new Label("Choose which file type you want to import.");
        description.setFont(UiScale.bodyFont(18));
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
        title.setFont(UiScale.headingFont(38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label nameLabel = new Label("Enter Deck Name");
        nameLabel.setFont(UiScale.bodyFont(17));
        nameLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextField nameField = new TextField();
        nameField.setPromptText("Deck name");
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-padding: 5 10;");

        Label descLabel = new Label("Enter Description");
        descLabel.setFont(UiScale.bodyFont(17));
        descLabel.setTextFill(Color.web(PRIMARY_BLUE));

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description (optional)");
        descArea.setPrefRowCount(5);
        descArea.setPrefHeight(140);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-control-inner-background: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-padding: 8;");

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
        title.setFont(UiScale.headingFont(38));
        title.setTextFill(Color.web(PRIMARY_BLUE));

        Label deckLabel = new Label("Choose Deck:");
        deckLabel.setFont(UiScale.bodyFont(20));
        deckLabel.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<Deck> deckCombo = new ComboBox<>();
        deckCombo.getItems().addAll(allDecks);
        deckCombo.setMaxWidth(Double.MAX_VALUE);
        deckCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");
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
        formatLabel.setFont(UiScale.bodyFont(17));
        formatLabel.setTextFill(Color.web(PRIMARY_BLUE));

        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("JSON", "CSV");
        formatCombo.setValue("JSON");
        formatCombo.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-text-fill: " + PRIMARY_BLUE + ";");

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
                "-fx-font-size: 17; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-background-radius: 28; " +
                "-fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; " +
                "-fx-font-size: 17; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-background-radius: 28; " +
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

