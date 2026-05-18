package com.studyapp.view;

import com.studyapp.util.UiScale;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;

import javafx.application.Platform;
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
    private static final String SOFT_PANEL_STYLE = "-fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #f8fbff;";
    private static final String ACTIVE_STYLE = "-fx-background-color: #e6eaf5; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 14 18; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 14 18; -fx-cursor: hand;";
    private static final String HOVER_STYLE = "-fx-background-color: #f0f4f8; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 14 18; -fx-cursor: hand;";

    private static double delXOffset = 0;
    private static double delYOffset = 0;
    private static Button activeButton = null;

    public static void show(BorderPane mainLayout, Deck deckData, MainController mc) {
        Runnable returnAction = () -> {
            activeButton.setStyle(INACTIVE_STYLE);
            mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
        };
        show(mainLayout, deckData, mc, returnAction, mainLayout.getLeft());
    }

    public static void show(BorderPane mainLayout, Deck deckData, MainController mc, Runnable returnAction) {
        show(mainLayout, deckData, mc, returnAction, mainLayout.getLeft());
    }

    public static void show(
            BorderPane mainLayout,
            Deck deckData,
            MainController mc,
            Runnable returnAction,
            Node originalSidebar) {
        render(mainLayout, deckData, mc, returnAction, false, originalSidebar);
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
        mainLayout.setCenter(buildContent(
                mainLayout,
                deckData,
                mc,
                headerField,
                descriptionArea,
                originalSidebar,
                returnAction));

        if (editMode) {
            Platform.runLater(() -> {
                headerField.requestFocus();
                headerField.selectAll();
            });
        }
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
        VBox sidebar = new VBox(18);
        sidebar.setPadding(UiScale.insets(20, 24, 20, 24));
        sidebar.setPrefWidth(UiScale.size(290));
        sidebar.setMinWidth(UiScale.size(290));
        sidebar.setMaxWidth(UiScale.size(290));
        sidebar.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Study Assistant\nApplication");
        title.setFont(UiScale.titleFont(38));
        title.setWrapText(true);
        title.setMaxWidth(UiScale.size(242));
        title.setTextFill(Color.web(PRIMARY_BLUE));
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        VBox buttonBox = new VBox(18);
        buttonBox.setPadding(UiScale.insets(24));
        buttonBox.setStyle(BORDER_STYLE);
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        Button editBtn = createNavButton("EDIT");
        editBtn.setOnAction(e -> {
            if (!editMode) {
                render(mainLayout, deckData, mc, returnAction, true, originalSidebar);
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setPrefHeight(UiScale.size(56));
        deleteBtn.setFont(UiScale.buttonFont(20));
        String deleteDefault = "-fx-background-color: white; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 7; -fx-background-radius: 7;"
                + " -fx-padding: 14 18; -fx-cursor: hand;";
        String deleteHover = "-fx-background-color: #f4f4f4; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 7; -fx-background-radius: 7;"
                + " -fx-padding: 14 18; -fx-cursor: hand;";
        deleteBtn.setStyle(deleteDefault);
        deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle(deleteHover));
        deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle(deleteDefault));
        deleteBtn.setOnAction(ev -> showDeleteDeckDialog(mainLayout, mc, deckData, originalSidebar, returnAction));

        Button backBtn = new Button("BACK");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(UiScale.size(56));
        backBtn.setFont(UiScale.buttonFont(20));
        String backDefault = "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 7; -fx-background-radius: 7;"
                + " -fx-padding: 14 18; -fx-cursor: hand;";
        String backHover = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 7; -fx-background-radius: 7;"
                + " -fx-padding: 14 18; -fx-cursor: hand;";
        backBtn.setStyle(backDefault);
        backBtn.setOnMouseEntered(ev -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(ev -> backBtn.setStyle(backDefault));

        if (editMode) {
            editBtn.setText("EDITING");
            editBtn.setStyle(ACTIVE_STYLE);
            activeButton = editBtn;
            backBtn.setOnAction(ev -> render(mainLayout, deckData, mc, returnAction, false, originalSidebar));

            Button saveBtn = new Button("SAVE CHANGES");
            saveBtn.setMaxWidth(Double.MAX_VALUE);
            saveBtn.setPrefHeight(UiScale.size(56));
            saveBtn.setFont(UiScale.buttonFont(20));
            String saveDefault = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: green;"
                    + " -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 14 18; -fx-cursor: hand;";
            String saveHover = "-fx-background-color: #e6f7e6; -fx-text-fill: black; -fx-border-color: green;"
                    + " -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 14 18; -fx-cursor: hand;";
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

            buttonBox.getChildren().addAll(editBtn, saveBtn, deleteBtn, spacer, backBtn);
        } else {
            backBtn.setOnAction(ev -> {
                mainLayout.setLeft(originalSidebar);
                returnAction.run();
            });

            Button cardsBtn = createNavButton("CARDS");
            cardsBtn.setOnAction(e -> {
                cardsBtn.setStyle(ACTIVE_STYLE);
                activeButton = cardsBtn;
                backBtn.setOnAction(ev -> render(mainLayout, deckData, mc, returnAction, false, originalSidebar));
                mainLayout.setCenter(AllCardsPanel.create(mainLayout, deckData, mc));
            });

            buttonBox.getChildren().addAll(cardsBtn, editBtn, spacer, backBtn);
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

    private static VBox buildContent(
            BorderPane mainLayout,
            Deck deckData,
            MainController mc,
            TextField headerField,
            TextArea descriptionArea,
            Node originalSidebar,
            Runnable returnAction) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(12));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(14);
        mainContent.setPadding(new Insets(14));
        mainContent.setStyle(BORDER_STYLE);
        mainContent.setMaxWidth(Double.MAX_VALUE);
        mainContent.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        HBox infoBox = new HBox(20);
        infoBox.setPadding(new Insets(14));
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setMaxHeight(170);
        infoBox.setStyle(SOFT_PANEL_STYLE);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VBox leftInfo = new VBox(10);
        leftInfo.setPadding(new Insets(12));
        leftInfo.setPrefWidth(UiScale.size(300));
        leftInfo.setMinWidth(UiScale.size(280));
        leftInfo.setStyle("-fx-border-color: #d7e2f3; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: white;");
        leftInfo.getChildren().addAll(
                infoLabel("ID: " + deckData.getDeckID()),
                infoLabel("Cards: " + mc.getFlashcardsByDeck(deckData.getDeckID()).size()),
                infoLabel("Created at: " + deckData.getCreatedAt().format(fmt)));

        VBox rightInfo = new VBox(8);
        HBox.setHgrow(rightInfo, Priority.ALWAYS);
        Label descTitle = new Label("Description:");
        descTitle.setFont(UiScale.headingFont(26));
        rightInfo.getChildren().addAll(descTitle, descriptionArea);

        infoBox.getChildren().addAll(leftInfo, rightInfo);

        Button studyBtn = new Button("START STUDY");
        studyBtn.setMaxWidth(Double.MAX_VALUE);
        studyBtn.setPrefHeight(UiScale.size(60));
        studyBtn.setFont(UiScale.buttonFont(24));
        String studyDefault = "-fx-background-color: #00bf63; -fx-text-fill: white;"
                + " -fx-border-color: #00bf63; -fx-border-radius: 8; -fx-background-radius: 8;"
                + " -fx-padding: 12 15; -fx-cursor: hand; -fx-font-weight: bold;";
        String studyHover = "-fx-background-color: #b3ffae; -fx-text-fill: white;"
                + " -fx-border-color: #00bf63; -fx-border-radius: 8; -fx-background-radius: 8;"
                + " -fx-padding: 12 15; -fx-cursor: hand; -fx-font-weight: bold;";
        studyBtn.setStyle(studyDefault);
        studyBtn.setOnMouseEntered(e -> studyBtn.setStyle(studyHover));
        studyBtn.setOnMouseExited(e -> studyBtn.setStyle(studyDefault));
        studyBtn.setOnAction(e -> StudyPanel.create(mainLayout, deckData, mc, originalSidebar, returnAction));
        VBox.setMargin(studyBtn, new Insets(10, 0, 0, 0));

        VBox progressSection = new VBox(8);
        progressSection.setPadding(new Insets(10, 0, 0, 0));
        Label progressTitle = new Label("Progress:");
        progressTitle.setFont(UiScale.headingFont(28));
        progressTitle.setTextFill(Color.web(PRIMARY_BLUE));

        ProgressBar bar = new ProgressBar(mc.getDeckProgress(deckData.getDeckID()) / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(UiScale.size(42));
        bar.setStyle("-fx-accent: " + HEADER_BLUE + ";");

        Label pctLbl = new Label(mc.getDeckProgress(deckData.getDeckID()) + "% complete");
        pctLbl.setFont(UiScale.emphasisFont(22));
        pctLbl.setTextFill(Color.web(PRIMARY_BLUE));

        HBox progressHeader = new HBox();
        progressHeader.setAlignment(Pos.CENTER_LEFT);
        Region progressSpacer = new Region();
        HBox.setHgrow(progressSpacer, Priority.ALWAYS);
        progressHeader.getChildren().addAll(progressTitle, progressSpacer, pctLbl);
        progressSection.getChildren().addAll(progressHeader, bar);

        VBox previewSection = new VBox(12);
        previewSection.setPadding(new Insets(4, 0, 0, 0));
        VBox.setVgrow(previewSection, Priority.ALWAYS);
        Label previewHeader = new Label("Cards Preview");
        previewHeader.setFont(UiScale.headingFont(34));
        previewHeader.setTextFill(Color.web(PRIMARY_BLUE));
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
            empty.setFont(UiScale.bodyFont(14));
            empty.setTextFill(Color.web("#6b7280"));
            previewSection.getChildren().addAll(previewHeader, empty);
        } else {
            for (int i = 0; i < Math.min(4, preview.size()); i++) {
                Label qLbl = new Label("Q. " + preview.get(i).getQuestion());
                qLbl.setFont(UiScale.bodyFont(18));
                qLbl.setWrapText(true);
                qLbl.setMaxWidth(Double.MAX_VALUE);
                qLbl.setMinHeight(UiScale.size(64));
                qLbl.setPadding(UiScale.insets(14));
                qLbl.setStyle("-fx-border-color: " + PRIMARY_BLUE
                        + "; -fx-border-radius: 5; -fx-background-color: white;");
                grid.add(qLbl, i % 2, i / 2);
            }
            previewSection.getChildren().addAll(previewHeader, grid);
        }

        mainContent.getChildren().addAll(headerField, infoBox, studyBtn, progressSection, previewSection);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(UiScale.bodyFont(18));
        lbl.setWrapText(true);
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private static Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(UiScale.size(56));
        btn.setFont(UiScale.buttonFont(20));
        btn.setStyle(INACTIVE_STYLE);
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().equals(ACTIVE_STYLE)) {
                btn.setStyle(HOVER_STYLE);
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().equals(ACTIVE_STYLE)) {
                btn.setStyle(INACTIVE_STYLE);
            }
        });
        return btn;
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
        title.setFont(UiScale.headingFont(41));
        title.setTextFill(Color.web("#2a548f"));

        Label description = new Label("This will also delete cards within this deck, are you sure?");
        description.setFont(UiScale.bodyFont(15));
        description.setTextFill(Color.web("#2a548f"));
        description.setWrapText(true);
        VBox.setMargin(description, new Insets(20, 20, 35, 0));

        Label errorLabel = new Label();
        errorLabel.setFont(UiScale.bodyFont(13));
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
        field.setFont(UiScale.titleFont(64));
        field.setEditable(editMode);
        field.setFocusTraversable(editMode);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setMinHeight(UiScale.size(100));
        field.setPrefHeight(UiScale.size(100));
        field.setAlignment(Pos.CENTER);
        if (editMode) {
            field.setStyle("-fx-background-color: #d8e4f5; -fx-background-radius: 8; "
                    + "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 8; "
                    + "-fx-padding: 10; -fx-text-fill: black;");
        } else {
            field.setStyle("-fx-background-color: " + HEADER_BLUE
                    + "; -fx-background-radius: 8; -fx-padding: 10; -fx-text-fill: white;");
        }
        return field;
    }

    private static TextArea buildDescriptionArea(String deckDescription, boolean editMode) {
        String rawDescription = deckDescription == null ? "" : deckDescription;
        String text = editMode
                ? rawDescription
                : (rawDescription.isBlank() ? "No description." : rawDescription);

        TextArea area = new TextArea(text);
        area.setFont(UiScale.bodyFont(24));
        area.setWrapText(true);
        area.setEditable(editMode);
        area.setFocusTraversable(editMode);
        area.setPrefRowCount(3);
        area.setMinHeight(UiScale.size(84));
        area.setPrefHeight(UiScale.size(104));
        area.setMaxHeight(118);
        area.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(area, Priority.NEVER);
        if (editMode) {
            area.setPromptText("Description (optional)");
            area.setStyle("-fx-control-inner-background: #f0f4ff; -fx-text-fill: #111827; " + UiScale.uiFontCss(24) + " "
                    + "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 2; -fx-border-radius: 6; "
                    + "-fx-background-radius: 6; -fx-padding: 8;");
        } else {
            area.setStyle("-fx-text-fill: #2a548f; " + UiScale.uiFontCss(24) + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent; "
                    + "-fx-control-inner-background: white; -fx-background-color: white; -fx-background-radius: 6; "
                    + "-fx-border-color: #d7e2f3; -fx-border-radius: 6; -fx-padding: 8;");
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