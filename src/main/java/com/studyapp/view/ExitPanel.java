package com.studyapp.view;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ExitPanel {
    private static double xOffset = 0;
    private static double yOffset = 0;
    private static final String PRIMARY_COLOR = "#2a548f";
    private static final String LIGHT_BG = "#f8fafc";
    private static final String ERROR_COLOR = "#c0392b";
    private static final String BUTTON_GRAY = "#c5cae9";
    private static final String BUTTON_GRAY_HOVER = "#b3b9e0";
    private static final String BUTTON_GREEN = "#b3ffae";
    private static final String BUTTON_GREEN_HOVER = "#00bf63";
    private static final String BUTTON_RED = "#ff9999";
    private static final String BUTTON_RED_HOVER = "#ff6666";

    public static void show(BorderPane mainLayout, MainController mc) {
        if(mc.hasUnsavedChanges()){
            createSaveOrDiscard(mainLayout, mc);
        }else{
            createExit(mainLayout, mc);
        }
    }

    private static void createSaveOrDiscard(BorderPane mainLayout, MainController mc) {
        Stage dialog = createDialog();
        VBox container = createContainer();

        Label title = createTitle("Save \nProgress?");
        Label description = createDescription("You have unsaved changes, do you want to save before exiting?");

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Serif", 13));
        errorLabel.setTextFill(Color.web(ERROR_COLOR));
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        VBox.setMargin(errorLabel, new Insets(-25, 0, 0, 0));

        Button discardBtn = createStyledButton("DISCARD", BUTTON_GRAY, BUTTON_GRAY_HOVER);
        discardBtn.setOnAction(e -> System.exit(0));

        Button saveBtn = createStyledButton("SAVE AND EXIT", BUTTON_GREEN, BUTTON_GREEN_HOVER);
        saveBtn.setOnAction(e -> {
            try {
                mc.saveChanges();
                dialog.close();
                System.exit(0);
            } catch (CustomException ex) {
                errorLabel.setText("⚠ " + (ex.getMessage() != null ? ex.getMessage() : "Failed to save. Please try again."));
                errorLabel.setTextFill(Color.web("#ff9999"));
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(discardBtn, saveBtn);

        HBox topBar = createTopBar(dialog);
        container.getChildren().addAll(topBar, title, description, errorLabel, buttonBox);

        Scene scene = new Scene(container, 300, 500);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    private static void createExit(BorderPane mainLayout, MainController mc) {
        Stage dialog = createDialog();
        VBox container = createContainer();

        Label title = createTitle("Exit\nApplication?");
        Label description = createDescription("Are you sure you want to exit? Data is synced to the database.");

        Button cancelBtn = createStyledButton("CANCEL", BUTTON_GRAY, BUTTON_GRAY_HOVER);
        cancelBtn.setOnAction(e -> dialog.close());

        Button exitBtn = createStyledButton("CONFIRM", BUTTON_RED, BUTTON_RED_HOVER);
        exitBtn.setOnAction(e -> System.exit(0));

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelBtn, exitBtn);

        HBox topBar = createTopBar(dialog);
        container.getChildren().addAll(topBar, title, description, buttonBox);

        Scene scene = new Scene(container, 300, 500);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    private static Stage createDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Exit Application");
        return dialog;
    }

    private static VBox createContainer() {
        VBox container = new VBox(20);
        container.setPrefWidth(300);
        container.setPrefHeight(500);
        container.setSpacing(15);
        container.setPadding(new Insets(0, 40, 40, 40));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 10; -fx-background-color: " + LIGHT_BG + ";");

        container.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            Stage stage = (Stage) container.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        return container;
    }

    private static HBox createTopBar(Stage dialog) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);

        Button closeBtn = new Button("X");
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: #1A438E; -fx-font-size: 18; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 0 10 0 0;";

        closeBtn.setStyle(normalStyle);
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(hoverStyle));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(normalStyle));

        topBar.getChildren().add(closeBtn);
        VBox.setMargin(topBar, new Insets(5, -30, 0, 0));

        return topBar;
    }

    private static Label createTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("Serif", 41));
        title.setTextFill(Color.web(PRIMARY_COLOR));
        return title;
    }

    private static Label createDescription(String text) {
        Label description = new Label(text);
        description.setFont(Font.font("Serif", 15));
        description.setTextFill(Color.web(PRIMARY_COLOR));
        description.setWrapText(true);
        VBox.setMargin(description, new Insets(20, 20, 35, 0));
        return description;
    }

    private static Button createStyledButton(String text, String normalColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(45);
        String normalStyle = "-fx-background-color: " + normalColor + "; -fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-cursor: hand;";
        String hoverStyleStr = "-fx-background-color: " + hoverColor + "; -fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-cursor: hand;";

        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyleStr));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));

        return btn;
    }
}
