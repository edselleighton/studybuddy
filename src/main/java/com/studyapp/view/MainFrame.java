package com.studyapp.view;

import java.util.Optional;

import com.studyapp.controller.MainController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainFrame {

    private static final String PRIMARY_BLUE = "#2a548f";
    private static final String BORDER_STYLE = "-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    private static final String ACTIVE_STYLE = "-fx-background-color: #e6eaf5; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
    private static final String HOVER_STYLE = "-fx-background-color: #f0f4f8; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";

    private static Button dashBtn;
    private static Button decksBtn;
    private static Button cardsBtn;

    public static void show(Stage stage) {
        MainController mc = SetupPanel.getMainController();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8fafc;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20, 20, 20, 20));
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setStyle("-fx-background-color: transparent;");

        Label appTitleLabel = new Label("Study Assistant\nApplication");
        appTitleLabel.setFont(Font.font("Serif", 18));
        appTitleLabel.setTextFill(Color.web(PRIMARY_BLUE));
        VBox.setMargin(appTitleLabel, new Insets(0, 0, 10, 0));

        VBox buttonBox = new VBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle(BORDER_STYLE);
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        dashBtn = createNavButton("Dashboard");
        decksBtn = createNavButton("My Decks");
        cardsBtn = createNavButton("All Cards");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("EXIT");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setFont(Font.font("Serif", 16));
        String exitDefault = "-fx-background-color: #ff9999; -fx-text-fill: black; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        String exitHover = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10 15; -fx-cursor: hand;";
        exitBtn.setStyle(exitDefault);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHover));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitDefault));
        exitBtn.setOnAction(e -> handleExit(mainLayout, mc));

        buttonBox.getChildren().addAll(dashBtn, decksBtn, cardsBtn, spacer, exitBtn);
        sidebar.getChildren().addAll(appTitleLabel, buttonBox);

        dashBtn.setOnAction(e -> {
            setActiveButton(dashBtn);
            mainLayout.setCenter(DashboardPanel.create(mainLayout));
        });

        decksBtn.setOnAction(e -> {
            setActiveButton(decksBtn);
            mainLayout.setCenter(MyDeckPanel.create(mainLayout, mc));
        });

        cardsBtn.setOnAction(e -> {
            setActiveButton(cardsBtn);
            mainLayout.setCenter(AllCardsPanel.create(mainLayout, mc));
        });

        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(DashboardPanel.create(mainLayout));
        setActiveButton(dashBtn);

        Scene existingScene = stage.getScene();
        if (existingScene == null) {
            stage.setScene(new Scene(mainLayout, 1024, 768));
        } else {
            existingScene.setRoot(mainLayout);
        }
        stage.setMaximized(true);
        stage.setOnCloseRequest(event -> {
            if (stage.getScene().getRoot() == mainLayout) {
                event.consume();
                handleExit(mainLayout, mc);
            }
        });
    }

    public static void activateMyDecks() {
        setActiveButton(decksBtn);
    }

    private static Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Serif", 16));
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

    private static void setActiveButton(Button active) {
        dashBtn.setStyle(INACTIVE_STYLE);
        decksBtn.setStyle(INACTIVE_STYLE);
        cardsBtn.setStyle(INACTIVE_STYLE);
        active.setStyle(ACTIVE_STYLE);
    }

    private static void handleExit(BorderPane mainLayout, MainController mc) {
        ExitPanel.show(mainLayout, mc);
    }


}