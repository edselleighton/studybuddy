package com.studyapp.view;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

    // ── NEW: page size constant ──────────────────────────────────────────────
    private static final int PAGE_SIZE = 5;

    public static VBox create(BorderPane mainLayout, MainController mc) {
        return create(mainLayout, "Manage decks, or import/export your deck data as JSON.", PRIMARY_BLUE, mc);
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
            FileChooser fc = new FileChooser();
            fc.setTitle("Import Decks from JSON");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fc.showOpenDialog(mainLayout.getScene().getWindow());

            if (file != null) {
                try {
                    int count = mc.importFromJson(file);
                    if (count > 0) {
                        mainLayout.setCenter(MyDeckPanel.create(
                                mainLayout,
                                count + " deck(s) imported successfully!",
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
                ChoiceDialog<String> deckPicker = new ChoiceDialog<>(
                        allDecks.get(0).getName(),
                        allDecks.stream().map(Deck::getName).toList()
                );
                deckPicker.setTitle("Export Deck");
                deckPicker.setHeaderText("Select deck to export");
                deckPicker.setContentText("Choose deck:");

                Optional<String> deckChoice = deckPicker.showAndWait();
                if (deckChoice.isPresent()) {
                    Deck selectedDeck = allDecks.stream()
                            .filter(d -> d.getName().equals(deckChoice.get()))
                            .findFirst()
                            .orElse(null);

                    if (selectedDeck != null) {
                        FileChooser fc = new FileChooser();
                        fc.setTitle("Save Deck as JSON");
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                        fc.setInitialFileName(selectedDeck.getName() + ".json");
                        File file = fc.showSaveDialog(mainLayout.getScene().getWindow());

                        if (file != null) {
                            try {
                                mc.exportDeckToJson(selectedDeck.getDeckID(), file);
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText("Export successful");
                                alert.setContentText("Deck exported to: " + file.getName());
                                alert.showAndWait();
                            } catch (CustomException ex) {
                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle("Export Error");
                                alert.setHeaderText("Failed to export deck");
                                alert.setContentText(ex.getMessage());
                                alert.showAndWait();
                            }
                        }
                    }
                }
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

    private static void showCreateDeckDialog(BorderPane mainLayout, MainController mc) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Deck");

        VBox container = new VBox(20);
        container.setPadding(new Insets(30, 40, 30, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15;");

        Label title = new Label("Create Deck");
        title.setFont(Font.font("Serif", 36));
        title.setTextFill(Color.web("#2a548f"));

        Label nameLabel = new Label("Enter Deck Name");
        nameLabel.setFont(Font.font("Serif", 16));
        nameLabel.setTextFill(Color.web("#2a548f"));

        TextField nameField = new TextField();
        nameField.setPromptText("Deck name");
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-border-color: #2a548f; -fx-border-width: 2; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 10;");

        Label descLabel = new Label("Enter Description");
        descLabel.setFont(Font.font("Serif", 16));
        descLabel.setTextFill(Color.web("#2a548f"));

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description (optional)");
        descArea.setPrefRowCount(6);
        descArea.setPrefHeight(150);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-border-color: #2a548f; -fx-border-width: 2; -fx-border-radius: 5; " +
                "-fx-control-inner-background: white; -fx-background-radius: 5; " +
                "-fx-font-size: 14; -fx-padding: 5;");

        Button createBtn = new Button("CREATE");
        createBtn.setPrefWidth(250);
        createBtn.setPrefHeight(45);
        createBtn.setStyle("-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; " +
                "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-cursor: hand;");
        createBtn.setOnMouseEntered(e ->
                createBtn.setStyle("-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; " +
                        "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; " +
                        "-fx-cursor: hand;"));
        createBtn.setOnMouseExited(e ->
                createBtn.setStyle("-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; " +
                        "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; " +
                        "-fx-cursor: hand;"));

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

        container.getChildren().addAll(title, nameLabel, nameField, descLabel, descArea, buttonBox);

        Scene scene = new Scene(container, 400, 550);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }
}