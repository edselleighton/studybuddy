package com.studyapp.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import com.studyapp.controller.MainController;

public class SplashScreen {

    private static final String PRIMARY_BLUE = "#1A438E";
    private static final String CARD_BG      = "#F8F9FF";
    private static final String CARD_BORDER  = "#1A438E";

    public static void show(Stage primaryStage, MainController mc, Runnable onFinished) {
        primaryStage.hide();

        Stage splash = new Stage();
        splash.initStyle(StageStyle.TRANSPARENT);
        splash.setAlwaysOnTop(true);

        StackPane screen = new StackPane();
        screen.setStyle("-fx-background-color: rgba(15, 25, 60, 0.92);");

        double cardW = 560;
        double cardH = 340;
        Rectangle card = new Rectangle(cardW, cardH);
        card.setArcWidth(30);
        card.setArcHeight(30);
        card.setFill(Color.web(CARD_BG));
        card.setStroke(Color.web(CARD_BORDER));
        card.setStrokeWidth(2.5);

        Label title = new Label("Study Assistant\nApplication");
        title.setFont(Font.font("Serif", FontWeight.MEDIUM, 42));
        title.setTextFill(Color.web(PRIMARY_BLUE));
        title.setAlignment(Pos.CENTER);
        title.setOpacity(0);

        Label loadingLabel = new Label("Initializing...");
        loadingLabel.setFont(Font.font("Serif", 16));
        loadingLabel.setTextFill(Color.web("#8FA5DF"));
        loadingLabel.setOpacity(0);
        StackPane.setAlignment(loadingLabel, Pos.BOTTOM_CENTER);

        Rectangle page1 = makePage("#DDE2F3", cardW - 40, cardH - 40, 20, 20);
        Rectangle page2 = makePage("#C5CFF0", cardW - 40, cardH - 40, 12, 12);
        Rectangle page3 = makePage("#A8B8E8", cardW - 40, cardH - 40,  6,  6);
        Rectangle page4 = makePage("#8FA5DF", cardW - 40, cardH - 40,  0,  0);

        StackPane cardStack = new StackPane(card, page1, page2, page3, page4, title);
        cardStack.setMaxSize(cardW, cardH);

        javafx.scene.layout.VBox centerBox = new javafx.scene.layout.VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(cardStack, loadingLabel);

        screen.getChildren().add(centerBox);

        double screenW = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenH = javafx.stage.Screen.getPrimary().getBounds().getHeight();

        Scene scene = new Scene(screen, screenW, screenH, Color.TRANSPARENT);
        splash.setScene(scene);
        splash.setX(0);
        splash.setY(0);
        splash.setWidth(screenW);
        splash.setHeight(screenH);
        splash.show();

        BorderPane[] prebuiltLayout = {null};
        Thread preloadThread = new Thread(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                prebuiltLayout[0] = new BorderPane();
                MainFrame.buildInto(prebuiltLayout[0], mc);
            });
        });
        preloadThread.setDaemon(true);
        preloadThread.start();

        Duration slideDur = Duration.millis(750);

        TranslateTransition t1 = slide(page1, -280, slideDur);
        TranslateTransition t2 = slide(page2, -180, slideDur.multiply(0.9));
        TranslateTransition t3 = slide(page3,  180, slideDur.multiply(0.9));
        TranslateTransition t4 = slide(page4,  280, slideDur);

        for (TranslateTransition t : new TranslateTransition[]{t1, t2, t3, t4})
            t.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition fadeTitle = new FadeTransition(Duration.millis(700), title);
        fadeTitle.setFromValue(0);
        fadeTitle.setToValue(1);
        fadeTitle.setDelay(Duration.millis(500));

        FadeTransition fadeLoading = new FadeTransition(Duration.millis(600), loadingLabel);
        fadeLoading.setFromValue(0);
        fadeLoading.setToValue(1);
        fadeLoading.setDelay(Duration.millis(800));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(700), screen);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(1800));
        fadeOut.setOnFinished(e -> {
            splash.close();
            onFinished.run();
        });

        ParallelTransition reveal = new ParallelTransition(t1, t2, t3, t4, fadeTitle, fadeLoading);
        reveal.setDelay(Duration.millis(400));
        reveal.setOnFinished(ev -> {
            //CHECK IF THE MAINFRAME WAS PRE-BUILT, ELSE USE NORMAL START-UP
            if (prebuiltLayout[0] != null) {
                MainFrame.showPrebuilt(primaryStage, prebuiltLayout[0]);
            } else {
                MainFrame.show(primaryStage, mc);
            }
            fadeOut.play();
        });
        reveal.play();
    }

    private static Rectangle makePage(String color, double w, double h, double tx, double ty) {
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(16);
        r.setArcHeight(16);
        r.setFill(Color.web(color));
        r.setTranslateX(tx);
        r.setTranslateY(ty);
        return r;
    }

    private static TranslateTransition slide(Rectangle r, double toX, Duration dur) {
        TranslateTransition t = new TranslateTransition(dur, r);
        t.setToX(r.getTranslateX() + toX);
        return t;
    }
}