package com.studyapp.view;

import com.studyapp.controller.CredentialHandler;
import com.studyapp.data.AppContext;
import com.studyapp.db.DatabaseConnection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private static final CredentialHandler CREDENTIAL_HANDLER = new CredentialHandler();

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

        Label helperLabel = new Label("You can continue with local sample data for now and switch to MySQL later.");
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

        HBox rememberBox = new HBox(10);
        rememberBox.setAlignment(Pos.CENTER);
        Label rememberLabel = new Label("Remember me");
        rememberLabel.setTextFill(Color.web(primaryBlue));
        CheckBox rememberCheck = new CheckBox();
        rememberBox.getChildren().addAll(rememberLabel, rememberCheck);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button connectBtn = new Button("CONNECT");
        connectBtn.setPrefWidth(200);
        Button offlineBtn = new Button("USE SAMPLE DATA");
        offlineBtn.setPrefWidth(200);

        String defaultStyle = "-fx-background-color: " + lightBlueBg + "; -fx-text-fill: " + primaryBlue + "; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + headerBlue + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";
        String secondaryStyle = "-fx-background-color: white; -fx-text-fill: " + primaryBlue + "; -fx-border-color: " + primaryBlue + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";
        String secondaryHoverStyle = "-fx-background-color: #f8fafc; -fx-text-fill: " + primaryBlue + "; -fx-border-color: " + primaryBlue + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;";

        connectBtn.setStyle(defaultStyle);
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(hoverStyle));
        connectBtn.setOnMouseExited(e -> connectBtn.setStyle(defaultStyle));

        offlineBtn.setStyle(secondaryStyle);
        offlineBtn.setOnMouseEntered(e -> offlineBtn.setStyle(secondaryHoverStyle));
        offlineBtn.setOnMouseExited(e -> offlineBtn.setStyle(secondaryStyle));

        HBox connectBox = new HBox(connectBtn);
        connectBox.setAlignment(Pos.CENTER);
        HBox offlineBox = new HBox(offlineBtn);
        offlineBox.setAlignment(Pos.CENTER);

        connectBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            connectBtn.setDisable(true);
            connectBtn.setText("Connecting...");
            errorLabel.setText("");

            try {
                if (!DatabaseConnection.authenticate(username, password)) {
                    throw new IllegalStateException("Invalid credentials");
                }
                DatabaseConnection.setCredentials(username, password);
                AppContext.useMySqlData();
                if (rememberCheck.isSelected()) {
                    CREDENTIAL_HANDLER.write(username, password);
                } else {
                    CREDENTIAL_HANDLER.deleteEnvFile();
                }
                onSuccess.run();
            } catch (Exception ex) {
                errorLabel.setText("Connection failed. Check credentials or continue with sample data.");
                AppContext.useInMemoryData();
            }

            connectBtn.setDisable(false);
            connectBtn.setText("CONNECT");
        });

        offlineBtn.setOnAction(e -> {
            AppContext.useInMemoryData();
            errorLabel.setText("");
            onSuccess.run();
        });

        loginRoot.getChildren().addAll(titleLabel, helperLabel, userBox, passBox, rememberBox, connectBox, offlineBox, errorLabel);

        StackPane wrapper = new StackPane(loginRoot);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: #f7f9fc;");

        return new Scene(wrapper, 420, 580);
    }
}
