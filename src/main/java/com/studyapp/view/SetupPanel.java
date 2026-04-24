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
import javafx.stage.Stage;

public class SetupPanel {
    private static final MainController MAIN_CONTROLLER = new MainController();
    private static final double SETUP_WIDTH = 420;
    private static final double SETUP_HEIGHT = 580;

    public static MainController getMainController() {
        return MAIN_CONTROLLER;
    }

    public static Scene createScene(Stage primaryStage, Runnable onSuccess) {
        String primaryBlue = "#1a2a6c";
        String lightBlueBg = "#e3f2fd";
        String headerBlue = "#0d47a1";
        String borderStyle = "-fx-background-color: white; -fx-border-color: #dce3ea; -fx-border-radius: 10; -fx-background-radius: 10;";

        VBox loginRoot = new VBox(20);
        loginRoot.setPadding(new Insets(40, 30, 40, 30));
        loginRoot.setAlignment(Pos.CENTER_LEFT);
        loginRoot.setStyle(borderStyle);
        loginRoot.setMaxSize(320, 520);

        Label titleLabel = new Label("Connect Your\nDatabase");
        titleLabel.setFont(Font.font("Serif", 28));
        titleLabel.setTextFill(Color.web(primaryBlue));

        Label helperLabel = new Label("Sign in with your MySQL credentials to open the prototype UI flow.");
        helperLabel.setWrapText(true);
        helperLabel.setTextFill(Color.web("#475569"));

        VBox userBox = new VBox(5);
        Label userLabel = new Label("Enter Username");
        userLabel.setTextFill(Color.web(primaryBlue));
        TextField userField = new TextField();
        userField.setStyle("-fx-border-color: " + primaryBlue + "; -fx-border-radius: 3; -fx-background-color: white;");
        userBox.getChildren().addAll(userLabel, userField);

        VBox passBox = new VBox(5);
        Label passLabel = new Label("Enter Password");
        passLabel.setTextFill(Color.web(primaryBlue));
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-border-color: " + primaryBlue + "; -fx-border-radius: 3; -fx-background-color: white;");
        passBox.getChildren().addAll(passLabel, passField);

        Button connectBtn = new Button("SIGN IN");
        connectBtn.setPrefWidth(200);
        HBox buttonBox = new HBox(connectBtn);
        buttonBox.setAlignment(Pos.CENTER);

        String defaultStyle = "-fx-background-color: " + lightBlueBg + "; -fx-text-fill: " + primaryBlue + "; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + headerBlue + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";

        connectBtn.setStyle(defaultStyle);
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(hoverStyle));
        connectBtn.setOnMouseExited(e -> connectBtn.setStyle(defaultStyle));

        if (MAIN_CONTROLLER.tryAutoLogin()) {
            onSuccess.run();
        }

        connectBtn.setOnAction(e -> {
            String username = userField.getText() == null ? "" : userField.getText().trim();
            String password = passField.getText() == null ? "" : passField.getText();

            connectBtn.setDisable(true);
            connectBtn.setText("Signing in...");

            try {
                MAIN_CONTROLLER.login(username, password);
                onSuccess.run();
            } catch (CustomException ex) {
                MainFrame.showErrorDialog(ex.getMessage());
            }

            connectBtn.setDisable(false);
            connectBtn.setText("SIGN IN");
        });

        loginRoot.getChildren().addAll(titleLabel, helperLabel, userBox, passBox, buttonBox);

        StackPane wrapper = new StackPane(loginRoot);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: #f7f9fc;");

        primaryStage.setMaximized(false);
        primaryStage.setWidth(SETUP_WIDTH);
        primaryStage.setHeight(SETUP_HEIGHT);
        primaryStage.centerOnScreen();

        return new Scene(wrapper, SETUP_WIDTH, SETUP_HEIGHT);
    }
}
