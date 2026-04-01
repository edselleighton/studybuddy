package com.studyapp.view;

import com.studyapp.db.DatabaseConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SetupPanel {

    public static Scene createScene(Stage primaryStage, Runnable onSuccess) {

        String PRIMARY_BLUE = "#1a2a6c";
        String LIGHT_BLUE_BG = "#e3f2fd";
        String HEADER_BLUE = "#0d47a1";
        String BORDER_STYLE = "-fx-background-color: white; -fx-border-color: #dce3ea; -fx-border-radius: 10; -fx-background-radius: 10;";

        VBox loginRoot = new VBox(20);
        loginRoot.setPadding(new Insets(40, 30, 40, 30));
        loginRoot.setAlignment(Pos.CENTER_LEFT);
        loginRoot.setStyle(BORDER_STYLE);
        loginRoot.setMaxSize(300, 450);

        // Title
        Label titleLabel = new Label("Connect Your\nDatabase");
        titleLabel.setFont(Font.font("Serif", 28));
        titleLabel.setTextFill(Color.web(PRIMARY_BLUE));

        // Username
        VBox userBox = new VBox(5);
        Label userLabel = new Label("Enter Username");
        userLabel.setTextFill(Color.web(PRIMARY_BLUE));
        TextField userField = new TextField();
        userField.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 3; -fx-background-color: white;");
        userBox.getChildren().addAll(userLabel, userField);

        // Password
        VBox passBox = new VBox(5);
        Label passLabel = new Label("Enter Password");
        passLabel.setTextFill(Color.web(PRIMARY_BLUE));
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 3; -fx-background-color: white;");
        passBox.getChildren().addAll(passLabel, passField);

        // Remember me
        HBox rememberBox = new HBox(10);
        rememberBox.setAlignment(Pos.CENTER);
        Label rememberLabel = new Label("Remember me");
        rememberLabel.setTextFill(Color.web(PRIMARY_BLUE));
        CheckBox rememberCheck = new CheckBox();
        rememberBox.getChildren().addAll(rememberLabel, rememberCheck);

        // Error label
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        // Button
        Button connectBtn = new Button("CONNECT");
        connectBtn.setPrefWidth(200);

        String defaultStyle = "-fx-background-color: " + LIGHT_BLUE_BG + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + HEADER_BLUE + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";

        connectBtn.setStyle(defaultStyle);
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(hoverStyle));
        connectBtn.setOnMouseExited(e -> connectBtn.setStyle(defaultStyle));

        HBox btnBox = new HBox(connectBtn);
        btnBox.setAlignment(Pos.CENTER);

        // Logic
        connectBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            connectBtn.setDisable(true);
            connectBtn.setText("Connecting...");
            errorLabel.setText("");

            try {
                DatabaseConnection.setCredentials(username, password);
                DatabaseConnection.getConnection();

                // Success → run callback (open main app)
                onSuccess.run();

            } catch (Exception ex) {
                errorLabel.setText("Connection failed. Check credentials.");
            }

            connectBtn.setDisable(false);
            connectBtn.setText("CONNECT");
        });

        loginRoot.getChildren().addAll(
                titleLabel,
                userBox,
                passBox,
                rememberBox,
                btnBox,
                errorLabel
        );

        StackPane wrapper = new StackPane(loginRoot);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: #f7f9fc;");

        return new Scene(wrapper, 400, 550);
    }
}