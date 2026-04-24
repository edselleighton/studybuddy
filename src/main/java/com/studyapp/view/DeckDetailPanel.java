package com.studyapp.view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeckDetailPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String ACTIVE_BTN_STYLE = "-fx-background-color: #e6eaf5; -fx-text-fill: black;"
            + " -fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";

    private static double delXOffset = 0;
    private static double delYOffset = 0;

    public static void show(BorderPane mainLayout, Deck deckData, MainController mc) {
        show(mainLayout, deckData, mc, () -> mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc)));
    }

    public static void show(BorderPane mainLayout, Deck deckData, MainController mc, Runnable returnAction) {
        render(mainLayout, deckData, mc, returnAction, false, mainLayout.getLeft());
    }

    private static void render(
            BorderPane mainLayout,
            Deck deckData,
            MainController mc,
            Runnable returnAction,
            boolean editMode,
            Node originalSidebar) {
        TextField headerField = buildHeaderField(deckData.getName(), editMode);
        TextArea descriptionArea = buildDescriptionArea(deckData.getDescription(), editMode);

        mainLayout.setLeft(buildSidebar(
                mainLayout,
                originalSidebar,
                deckData,
                mc,
                returnAction,
                editMode,
                headerField,
                descriptionArea));
        mainLayout.setCenter(buildContent(deckData, mc, headerField, descriptionArea));
    }

    private static VBox buildSidebar(
            BorderPane mainLayout,
            Node originalSidebar,
            Deck deckData,
            MainController mc,
            Runnable returnAction,
            boolean editMode,
            TextField headerField,
            TextArea descriptionArea) {
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

        Button editBtn = new Button("EDIT");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setFont(Font.font("Serif", 16));
        editBtn.setStyle(ACTIVE_BTN_STYLE);
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(
                "-fx-background-color: #d0dcf5; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE
                        + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(ACTIVE_BTN_STYLE));
        editBtn.setOnAction(e -> {
            if (!editMode) {
                render(mainLayout, deckData, mc, returnAction, true, originalSidebar);
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setFont(Font.font("Serif", 16));
        String deleteDefault = "-fx-background-color: white; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        String deleteHover = "-fx-background-color: #f4f4f4; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        deleteBtn.setStyle(deleteDefault);
        deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle(deleteHover));
        deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle(deleteDefault));
        deleteBtn.setOnAction(ev -> showDeleteDeckDialog(mainLayout, mc, deckData, originalSidebar, returnAction));

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

        if (editMode) {
            backBtn.setOnAction(ev -> render(mainLayout, deckData, mc, returnAction, false, originalSidebar));

            Button saveBtn = new Button("SAVE");
            saveBtn.setMaxWidth(Double.MAX_VALUE);
            saveBtn.setFont(Font.font("Serif", 16));
            String saveDefault = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: green;"
                    + " -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
            String saveHover = "-fx-background-color: #e6f7e6; -fx-text-fill: black; -fx-border-color: green;"
                    + " -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
            saveBtn.setStyle(saveDefault);
            saveBtn.setOnMouseEntered(ev -> saveBtn.setStyle(saveHover));
            saveBtn.setOnMouseExited(ev -> saveBtn.setStyle(saveDefault));
            saveBtn.setOnAction(ev -> {
                try {
                    saveChanges(deckData, headerField.getText(), descriptionArea.getText(), mc);
                    render(mainLayout, deckData, mc, returnAction, false, originalSidebar);
                } catch (CustomException e) {
                    MainFrame.showErrorDialog(e.getMessage() != null ? e.getMessage() : "Unknown error occurred while saving.");
                }
            });

            buttonBox.getChildren().addAll(editBtn, saveBtn, spacer, deleteBtn, backBtn);
        } else {
            backBtn.setOnAction(ev -> {
                mainLayout.setLeft(originalSidebar);
                returnAction.run();
            });

            Button cardsBtn = new Button("Cards");
            cardsBtn.setMaxWidth(Double.MAX_VALUE);
            cardsBtn.setFont(Font.font("Serif", 16));
            cardsBtn.setStyle(ACTIVE_BTN_STYLE);
            cardsBtn.setOnMouseEntered(e -> cardsBtn.setStyle(
                    "-fx-background-color: #d0dcf5; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE
                            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;"));
            cardsBtn.setOnMouseExited(e -> cardsBtn.setStyle(ACTIVE_BTN_STYLE));
            cardsBtn.setOnAction(e -> mainLayout.setCenter(AllCardsPanel.create(mainLayout, deckData, mc)));

            Button studyBtn = new Button("Study");
            studyBtn.setMaxWidth(Double.MAX_VALUE);
            studyBtn.setFont(Font.font("Serif", 16));
            studyBtn.setStyle(ACTIVE_BTN_STYLE);
            studyBtn.setOnMouseEntered(e -> studyBtn.setStyle(
                    "-fx-background-color: #d0dcf5; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE
                            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;"));
            studyBtn.setOnMouseExited(e -> studyBtn.setStyle(ACTIVE_BTN_STYLE));
            studyBtn.setOnAction(e -> StudyPanel.create(mainLayout, deckData, mc));

            buttonBox.getChildren().addAll(editBtn, cardsBtn, studyBtn, spacer, deleteBtn, backBtn);
        }

        sidebar.getChildren().addAll(title, buttonBox);
        return sidebar;
    }

    private static void saveChanges(Deck deckData, String newName, String newDesc, MainController mc) throws CustomException {
        String updatedName = newName == null ? "" : newName.trim();
        String currentName = deckData.getName() == null ? "" : deckData.getName();
        String updatedDescription = normalizeDescription(newDesc);
        String currentDescription = normalizeDescription(deckData.getDescription());

        if (!Objects.equals(currentName, updatedName) || !Objects.equals(currentDescription, updatedDescription)) {
            try{
                deckData.setName(updatedName);
                deckData.setDescription(updatedDescription);
                mc.updateDeck(deckData);
            } catch (CustomException e) {
                deckData.setName(currentName);
                deckData.setDescription(currentDescription);
                throw e;
            }
        }
    }

    private static VBox buildContent(Deck deckData, MainController mc, TextField headerField, TextArea descriptionArea) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        HBox infoBox = new HBox(40);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle(BORDER_STYLE);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VBox leftInfo = new VBox(6);
        leftInfo.getChildren().addAll(
                infoLabel("ID: " + deckData.getDeckID()),
                infoLabel("Cards: " + mc.getFlashcardsByDeck(deckData.getDeckID()).size()),
                infoLabel("Created at: " + deckData.getCreatedAt().format(fmt)));

        VBox rightInfo = new VBox(6);
        Label descTitle = new Label("Description:");
        descTitle.setFont(Font.font("Serif", 14));
        rightInfo.getChildren().addAll(descTitle, descriptionArea);

        infoBox.getChildren().addAll(leftInfo, rightInfo);

        VBox progressSection = new VBox(10);
        Label progressTitle = new Label("Progress:");
        progressTitle.setFont(Font.font("Serif", 16));

        ProgressBar bar = new ProgressBar(mc.getDeckProgress(deckData.getDeckID()) / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(36);
        bar.setStyle("-fx-accent: " + HEADER_BLUE + ";");

        Label pctLbl = new Label(mc.getDeckProgress(deckData.getDeckID()) + "%");
        pctLbl.setFont(Font.font("Serif Bold", 16));
        pctLbl.setTextFill(Color.WHITE);

        StackPane progressStack = new StackPane(bar, pctLbl);
        progressSection.getChildren().addAll(progressTitle, progressStack);

        VBox previewSection = new VBox(15);
        Label previewHeader = new Label("Cards Preview");
        previewHeader.setFont(Font.font("Serif", 20));
        previewHeader.setMaxWidth(Double.MAX_VALUE);
        previewHeader.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col = new ColumnConstraints();
        col.setHgrow(Priority.ALWAYS);
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        List<Flashcard> preview = mc.getFlashcardsByDeck(deckData.getDeckID());
        if (preview.isEmpty()) {
            Label empty = new Label("No cards in this deck yet.");
            empty.setFont(Font.font("Serif", 14));
            empty.setTextFill(Color.web("#6b7280"));
            previewSection.getChildren().addAll(previewHeader, empty);
        } else {
            for (int i = 0; i < Math.min(4, preview.size()); i++) {
                Label qLbl = new Label("Q. " + preview.get(i).getQuestion());
                qLbl.setFont(Font.font("Serif", 14));
                qLbl.setWrapText(true);
                qLbl.setMaxWidth(Double.MAX_VALUE);
                qLbl.setPadding(new Insets(12));
                qLbl.setStyle("-fx-border-color: " + PRIMARY_BLUE
                        + "; -fx-border-radius: 5; -fx-background-color: white;");
                grid.add(qLbl, i % 2, i / 2);
            }
            previewSection.getChildren().addAll(previewHeader, grid);
        }

        mainContent.getChildren().addAll(headerField, infoBox, progressSection, previewSection);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 14));
        return lbl;
    }

    private static void showDeleteDeckDialog(
            BorderPane mainLayout,
            MainController mc,
            Deck deckData,
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

        Label title = new Label("Delete\nDeck?");
        title.setFont(Font.font("Serif", 41));
        title.setTextFill(Color.web("#2a548f"));

        Label description = new Label("This will also delete cards within this deck, are you sure?");
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
                mc.deleteDeck(deckData.getDeckID());
                MainFrame.showSuccessDialog("Deck deleted successfully.");
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

    private static TextField buildHeaderField(String deckName, boolean editMode) {
        TextField field = new TextField(deckName == null ? "" : deckName);
        field.setFont(Font.font("Serif", 32));
        field.setEditable(editMode);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setAlignment(Pos.CENTER);
        field.setStyle("-fx-background-color: " + HEADER_BLUE
                + "; -fx-background-radius: 8; -fx-padding: 10; -fx-text-fill: white;");
        return field;
    }

    private static TextArea buildDescriptionArea(String deckDescription, boolean editMode) {
        String rawDescription = deckDescription == null ? "" : deckDescription;
        String text = editMode
                ? rawDescription
                : (rawDescription.isBlank() ? "No description." : rawDescription);

        TextArea area = new TextArea(text);
        area.setFont(Font.font("Serif", 14));
        area.setWrapText(true);
        area.setEditable(editMode);
        if (editMode) {
            area.setStyle("-fx-text-fill: #475569; -fx-background-color: white; -fx-padding: 5;");
        } else {
            area.setStyle("-fx-text-fill: #475569; -fx-focus-color: transparent; -fx-faint-focus-color: transparent; "
                    + "-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 5;");
        }
        return area;
    }

    private static String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
