package com.studyapp.view;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.util.*;

public class StudyPanel {

    // ── constants ─────────────────────────────────────────────────────────────
    static final String PRIMARY_BLUE = "#2a548f";
    static final String HEADER_BLUE  = "#41729f";
    static final String BORDER_STYLE =
            "-fx-border-color: " + PRIMARY_BLUE +
                    "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";

    // ── persistent state ──────────────────────────────────────────────────────
    private final MainController mc;
    private final Deck deckData;
    private final BorderPane mainLayout;
    private final Node originalSidebar;
    private final Runnable returnAction;

    private List<Flashcard> flashcards;
    private StudySession studySession;
    private boolean[] correctAnswers;
    private int totalCorrect  = 0;
    private int totalAttempts = 0;
    private int currentIndex  = 0;

    // ── live sidebar nodes ────────────────────────────────────────────────────
    private Label correctLbl;
    private Label attemptsLbl;
    private Arc   progressArc;
    private Label pctLabel;
    private double Xoffset = 0;
    private double Yoffset = 0;

    // ── entry point ───────────────────────────────────────────────────────────
    public static void create(BorderPane mainLayout, Deck deckData, MainController mc) {
        new StudyPanel(
                mainLayout,
                deckData,
                mc,
                mainLayout.getLeft(),
                () -> mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc)))
                .init();
    }

    public static void create(
            BorderPane mainLayout,
            Deck deckData,
            MainController mc,
            Node originalSidebar,
            Runnable returnAction) {
        new StudyPanel(mainLayout, deckData, mc, originalSidebar, returnAction).init();
    }

    private StudyPanel(
            BorderPane mainLayout,
            Deck deckData,
            MainController mc,
            Node originalSidebar,
            Runnable returnAction) {
        this.mainLayout   = mainLayout;
        this.deckData     = deckData;
        this.mc           = mc;
        this.originalSidebar = originalSidebar;
        this.returnAction = returnAction;
    }

    private void init() {
        flashcards = new ArrayList<>(mc.getFlashcardsByDeck(deckData.getDeckID()));
        if (flashcards == null || flashcards.isEmpty()) {
            MainFrame.showErrorDialog("No cards in this deck.");
            return;
        }

        orderCards();
        correctAnswers = new boolean[flashcards.size()];

        try {
            studySession = mc.createStudySession(deckData.getDeckID(), LocalDateTime.now());
        } catch (CustomException e) {
            MainFrame.showErrorDialog("Could not create study session: " + e.getMessage());
            return;
        }

        mainLayout.setLeft(buildSidebar());
        showQuestion();
    }

    // ── center swappers ───────────────────────────────────────────────────────
    void showQuestion() {
        mainLayout.setCenter(QuestionPanel.build(this, flashcards.get(currentIndex), deckData));
    }

    void orderCards(){
        // Separate cards into: unanswered, weak (wrong attempts), and rest
        List<Flashcard> unanswered = new ArrayList<>();
        List<Flashcard> weak = new ArrayList<>();
        List<Flashcard> rest = new ArrayList<>();

        for (Flashcard card : flashcards) {
            int totalReviews = (int) mc.getAllCardReviews().stream()
                    .filter(r -> r.getFlashcardID() == card.getCardID())
                    .count();
            int wrongCount = countWrongAttempts(card.getCardID());

            if (totalReviews == 0) {
                unanswered.add(card);
            } else if (wrongCount > 0) {
                weak.add(card);
            } else {
                rest.add(card);
            }
        }

        // Shuffle each group
        Collections.shuffle(unanswered);
        Collections.shuffle(weak);
        Collections.shuffle(rest);

        // Order: unanswered first, then weak, then rest
        flashcards.clear();
        flashcards.addAll(unanswered);
        flashcards.addAll(weak);
        flashcards.addAll(rest);
    }

    void showResult(String result, String answer, Flashcard card) {
        mainLayout.setCenter(ResultPanel.build(this, result, card, answer, deckData));
    }

    // ── called by QuestionPanel on SUBMIT ─────────────────────────────────────
    void handleSubmit(String answer) {
        if (answer == null || answer.trim().isEmpty()) return;

        Flashcard card = flashcards.get(currentIndex);
        LocalDateTime reviewedAt = LocalDateTime.now();
        String result = mc.checkAnswer(card.getAnswer(), answer);
        boolean isCorrect = result.equals("CORRECT");

        // Only count the attempt if the card hasn't been correctly answered yet
        if (!correctAnswers[currentIndex]) {
            totalAttempts++;

            if (isCorrect) {
                correctAnswers[currentIndex] = true;
                totalCorrect++;
            }
        }

        try {
            mc.createCardReview(
                    studySession.getSessionID(),
                    card.getCardID(),
                    reviewedAt,
                    isCorrect
            );
        } catch (CustomException e) {
            MainFrame.showErrorDialog("Could not save review: " + e.getMessage());
        }

        refreshScore();
        showResult(result, answer, card);
    }

    // ── called by ResultPanel nav buttons ─────────────────────────────────────
    void goNext() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            showQuestion();
        } else {
            finishSession();
        }
    }

    void goPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion();
        }
    }

    void goRetry() {
        showQuestion();
    }

    // ── state queries used by ResultPanel ─────────────────────────────────────
    boolean isFirstCard() { return currentIndex == 0; }
    boolean isLastCard()  { return currentIndex == flashcards.size() - 1; }

    // ── session end ───────────────────────────────────────────────────────────
    private void finishSession() {
        endSession();
        showFinalScore();
        returnToDeckDetail();
    }

    private void endSessionEarly() {
        endSession();
        returnToDeckDetail();
    }

    private void endSession() {
        try {
            studySession.setEndedAt(LocalDateTime.now());
            mc.updateEndStudySession(studySession);
        } catch (CustomException e) {
            MainFrame.showErrorDialog("Could not end session: " + e.getMessage());
        }
    }

    private void returnToDeckDetail() {
        DeckDetailPanel.show(mainLayout, deckData, mc, returnAction, originalSidebar);
    }

    // ── score refresh (sidebar stays mounted, just mutate the labels) ─────────
    private void refreshScore() {
        correctLbl.setText("Correct: "   + totalCorrect);
        attemptsLbl.setText("Attempts: " + totalAttempts);

        double pct = (double) totalCorrect / flashcards.size();
        progressArc.setLength(-180 * pct);
        pctLabel.setText((int) (pct * 100) + "%");
    }

    // ── sidebar (built once, never replaced) ──────────────────────────────────
    private VBox buildSidebar() {
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

        // ── gauge ──
        StackPane arcStack = new StackPane();
        arcStack.setPadding(new Insets(10, 0, 0, 0));

        Arc backgroundArc = new Arc(0, 0, 50, 50, 180, -180);
        backgroundArc.setFill(Color.TRANSPARENT);
        backgroundArc.setStroke(Color.web("#e6eaf5"));
        backgroundArc.setStrokeWidth(12);
        backgroundArc.setType(ArcType.OPEN);
        backgroundArc.setStrokeLineCap(StrokeLineCap.ROUND);

        progressArc = new Arc(0, 0, 50, 50, 180, 0);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(Color.web(HEADER_BLUE));
        progressArc.setStrokeWidth(12);
        progressArc.setType(ArcType.OPEN);
        progressArc.setStrokeLineCap(StrokeLineCap.ROUND);

        pctLabel = new Label("0%");
        pctLabel.setFont(Font.font("Serif", FontWeight.BOLD, 20));
        pctLabel.setTranslateY(5);

        Group gaugeGroup = new Group(backgroundArc, progressArc);
        arcStack.getChildren().addAll(gaugeGroup, pctLabel);

        // ── stats ──
        VBox stats = new VBox(10);
        stats.setAlignment(Pos.CENTER_LEFT);
        correctLbl  = new Label("Correct: 0");
        attemptsLbl = new Label("Attempts: 0");
        correctLbl.setFont(Font.font("Serif", 18));
        attemptsLbl.setFont(Font.font("Serif", 18));
        stats.getChildren().addAll(correctLbl, attemptsLbl);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── leave button ──
        Button leaveBtn = new Button("LEAVE");
        leaveBtn.setMaxWidth(Double.MAX_VALUE);
        leaveBtn.setFont(Font.font("Serif", 16));
        String leaveDefault = "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        String leaveHover = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: "
                + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5;"
                + " -fx-padding: 10 15; -fx-cursor: hand;";
        leaveBtn.setStyle(leaveDefault);
        leaveBtn.setOnMouseEntered(e -> leaveBtn.setStyle(leaveHover));
        leaveBtn.setOnMouseExited(e  -> leaveBtn.setStyle(leaveDefault));
        leaveBtn.setOnAction(e -> endSessionEarly());

        buttonBox.getChildren().addAll(arcStack, stats, spacer, leaveBtn);
        sidebar.getChildren().addAll(title, buttonBox);
        return sidebar;
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private int countWrongAttempts(int cardID) {
        return (int) mc.getAllCardReviews().stream()
                .filter(r -> r.getFlashcardID() == cardID && !r.isCorrect())
                .count();
    }

    private void showFinalScore() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Finished");

        VBox container = new VBox(4);
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

        Label title = new Label("Session Complete!");
        title.setFont(Font.font("Serif", 38));
        title.setTextFill(Color.web("#b3ffae"));
        VBox.setMargin(title, new Insets(-8, 0, 0, 0));

        Label description = new Label("Score: " + totalCorrect + "/" + flashcards.size() + "\nGreat work!");
        description.setFont(Font.font("Serif", 15));
        description.setTextFill(Color.web("#2a548f"));
        description.setWrapText(true);
        description.setMaxWidth(300);
        VBox.setMargin(description, new Insets(8, 20, 30, 20));

        Button okayBtn = new Button("OKAY");
        okayBtn.setMaxWidth(Double.MAX_VALUE);
        okayBtn.setPrefHeight(45);

        String normalStyle = "-fx-background-color: #c5cae9; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";
        String hoverStyleStr = "-fx-background-color: #b3b9e0; -fx-text-fill: #2a548f; "
                + "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; "
                + "-fx-cursor: hand;";

        okayBtn.setStyle(normalStyle);
        okayBtn.setOnMouseEntered(e -> okayBtn.setStyle(hoverStyleStr));
        okayBtn.setOnMouseExited(e -> okayBtn.setStyle(normalStyle));
        okayBtn.setOnAction(e -> dialog.close());

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

        container.getChildren().addAll(topBar, title, description, okayBtn);

        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }
}
