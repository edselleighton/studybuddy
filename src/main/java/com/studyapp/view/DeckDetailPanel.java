package com.studyapp.view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.view.MyDeckPanel.DeckData;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

public class DeckDetailPanel {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String HEADER_BLUE = "#41729f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String INACTIVE_STYLE = "-fx-background-color: white; -fx-text-fill: black;"
            + " -fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15;";
    private static final String ACTIVE_BTN_STYLE = "-fx-background-color: #e6eaf5; -fx-text-fill: black;"
            + " -fx-border-color: " + PRIMARY_BLUE
            + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";

    public static void show(BorderPane mainLayout, Deck deckData, MainController mc) {
        Node savedSidebar = mainLayout.getLeft();
        mainLayout.setLeft(buildSidebar(mainLayout, savedSidebar, deckData, mc));
        mainLayout.setCenter(buildContent(deckData, mc));
    }

    private static VBox buildSidebar(BorderPane mainLayout, Node savedSidebar, Deck deckData, MainController mc) {
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

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setFont(Font.font("Serif", 16));
        deleteBtn.setDisable(true);
        deleteBtn.setStyle("-fx-background-color: white; -fx-text-fill: #cc0000;"
                + " -fx-border-color: #cc0000; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15;");

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
            MainFrame.activateMyDecks();
            mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
        });

        buttonBox.getChildren().addAll(editBtn, cardsBtn, studyBtn, spacer, deleteBtn, backBtn);
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

    private static VBox buildContent(Deck deckData, MainController mc) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle(BORDER_STYLE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Label header = new Label(deckData.getName());
        header.setFont(Font.font("Serif", 32));
        header.setTextFill(Color.WHITE);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + HEADER_BLUE
                + "; -fx-background-radius: 8; -fx-padding: 10;");

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
        String descText = deckData.getDescription();
        Label descLbl = new Label(descText == null || descText.isBlank() ? "No description." : descText);
        descLbl.setFont(Font.font("Serif", 14));
        descLbl.setTextFill(Color.web("#475569"));
        descLbl.setWrapText(true);
        rightInfo.getChildren().addAll(descTitle, descLbl);

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

        mainContent.getChildren().addAll(header, infoBox, progressSection, previewSection);
        wrapper.getChildren().add(mainContent);
        return wrapper;
    }

    private static Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Serif", 14));
        return lbl;
    }
}
