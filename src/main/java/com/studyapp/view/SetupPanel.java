package com.studyapp.view;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SetupPanel {
    private static final double SETUP_WIDTH = 340;
    private static final double SETUP_HEIGHT = 520;

    public static Scene createScene(Stage primaryStage, MainController mainController, Runnable onSuccess) {
        String primaryBlue  = "#1A438E";
        String cardBg       = "#F8F9FF";

        VBox loginRoot = new VBox(15);
        loginRoot.setPadding(new Insets(0, 40, 40, 40));
        loginRoot.setAlignment(Pos.TOP_LEFT);
        loginRoot.setPrefWidth(300);
        loginRoot.setPrefHeight(500);
        loginRoot.setStyle(
                "-fx-background-color: " + cardBg + "; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: " + primaryBlue + "; " +
                        "-fx-border-width: 2.5;;"
        );

        final double[] offset = {0, 0};
        loginRoot.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });
        loginRoot.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - offset[0]);
            primaryStage.setY(event.getScreenY() - offset[1]);
        });

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        Button closeBtn = new Button("X");
        String closeNormal = "-fx-background-color: transparent; -fx-text-fill: " + primaryBlue + "; -fx-font-size: 18; -fx-cursor: hand;";
        String closeHover  = "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 0 10 0 0;";
        closeBtn.setStyle(closeNormal);
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(closeHover));
        closeBtn.setOnMouseExited(e  -> closeBtn.setStyle(closeNormal));
        closeBtn.setOnAction(e -> System.exit(0));
        topBar.getChildren().add(closeBtn);
        VBox.setMargin(topBar, new Insets(5, -30, 0, 0));

        Label titleLabel = new Label("Connect Your\nDatabase");
        titleLabel.setFont(Font.font("Serif", FontWeight.MEDIUM, 33));
        titleLabel.setTextFill(Color.web(primaryBlue));
        VBox.setMargin(titleLabel, new Insets(-10, 0, 20, 0));

        String fieldStyle =
                "-fx-background-color: white; " +
                        "-fx-border-color: " + primaryBlue + "; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-text-fill: " + primaryBlue + "; " +
                        "-fx-font-family: 'Serif'; " +
                        "-fx-font-size: 16px;";

        Label userLabel = new Label("Enter Username: ");
        userLabel.setTextFill(Color.web(primaryBlue));
        userLabel.setFont(Font.font("Serif", FontWeight.MEDIUM, 18));
        TextField userField = new TextField();
        userField.setPrefHeight(45);
        userField.setStyle(fieldStyle);
        userField.setPromptText("username");
        VBox.setMargin(userField, new Insets(0, 0, 10, 0));

        Label passLabel = new Label("Enter Password: ");
        passLabel.setTextFill(Color.web(primaryBlue));
        passLabel.setFont(Font.font("Serif", FontWeight.MEDIUM, 18));
        PasswordField passField = new PasswordField();
        passField.setPrefHeight(45);
        passField.setStyle(fieldStyle);
        passField.setPromptText("password");

        String loginNormal = "-fx-background-color: #DDE2F3; " +
                "-fx-background-radius: 30; " +
                "-fx-text-fill: " + primaryBlue + "; " +
                "-fx-font-size: 25; " +
                "-fx-font-family: 'Serif';";
        String loginHover  = "-fx-background-color: " + primaryBlue + "; " +
                "-fx-background-radius: 30; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 25; " +
                "-fx-font-family: 'Serif';";

        Button connectBtn = new Button("SIGN IN");
        connectBtn.setMaxWidth(Double.MAX_VALUE);
        connectBtn.setPrefHeight(50);
        connectBtn.setStyle(loginNormal);
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(loginHover));
        connectBtn.setOnMouseExited(e  -> connectBtn.setStyle(loginNormal));
        VBox.setMargin(connectBtn, new Insets(20, 0, -10, 0));

        connectBtn.setOnAction(e -> {
            String username = userField.getText() == null ? "" : userField.getText().trim();
            String password  = passField.getText()  == null ? "" : passField.getText();

            connectBtn.setDisable(true);
            connectBtn.setText("Signing in...");

            try {
                mainController.login(username, password);
                onSuccess.run();
            } catch (CustomException ex) {
                MainFrame.showErrorDialog(ex.getMessage());
            }

            connectBtn.setDisable(false);
            connectBtn.setText("SIGN IN");
        });

        loginRoot.getChildren().addAll(topBar, titleLabel, userLabel, userField, passLabel, passField, connectBtn);

        StackPane wrapper = new StackPane(loginRoot);
        wrapper.setStyle("-fx-background-color: transparent;");
        
        primaryStage.setWidth(SETUP_WIDTH);
        primaryStage.setHeight(SETUP_HEIGHT);
        primaryStage.centerOnScreen();

        return new Scene(wrapper, SETUP_WIDTH, SETUP_HEIGHT, Color.TRANSPARENT);
    }
}