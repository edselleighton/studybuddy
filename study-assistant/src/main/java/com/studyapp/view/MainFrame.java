package com.studyapp.view;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFrame {

    private static final String ACTIVE_STYLE = "-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;";
    private static final String HOVER_STYLE = "-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;";

    private static Button dashBtn, decksBtn, cardsBtn;

    public static void show(Stage stage) {

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8fafc;");

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setMinWidth(250);
        sidebar.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 0; -fx-border-color: #e2e8f0;");

        dashBtn = createNavButton("Dashboard");
        decksBtn = createNavButton("My Decks");
        cardsBtn = createNavButton("All Cards");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("Exit");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER; -fx-cursor: hand;");

        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle("-fx-background-color: #f87171; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER; -fx-cursor: hand;"));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8; -fx-alignment: CENTER; -fx-cursor: hand;"));

        exitBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Application");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("Any unsaved study progress might be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                stage.close();
            }
        });

        dashBtn.setOnAction(e -> {
            setActiveButton(dashBtn);
            mainLayout.setCenter(DashboardPanel.create());
        });

        decksBtn.setOnAction(e -> {
            setActiveButton(decksBtn);
            mainLayout.setCenter(DeckDetailPanel.create());
        });

        cardsBtn.setOnAction(e -> {
            setActiveButton(cardsBtn);
            mainLayout.setCenter(AllCardsPanel.create(mainLayout));
        });

        sidebar.getChildren().addAll(dashBtn, decksBtn, cardsBtn, spacer, exitBtn);

        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(DashboardPanel.create());
        setActiveButton(dashBtn);

        Scene scene = new Scene(mainLayout, 1024, 768);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private static Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(INACTIVE_STYLE);

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().equals(ACTIVE_STYLE)) btn.setStyle(HOVER_STYLE);
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().equals(ACTIVE_STYLE)) btn.setStyle(INACTIVE_STYLE);
        });

        return btn;
    }

    private static void setActiveButton(Button active) {
        dashBtn.setStyle(INACTIVE_STYLE);
        decksBtn.setStyle(INACTIVE_STYLE);
        cardsBtn.setStyle(INACTIVE_STYLE);
        active.setStyle(ACTIVE_STYLE);
    }
}
