package com.studyapp.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFrame {

    public static void show(Stage stage) {

        BorderPane root = new BorderPane();

        // Top bar
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #1a2a6c;");

        Label title = new Label("Study Assistant Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        header.getChildren().add(title);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #e3f2fd;");
        sidebar.setPrefWidth(150);

        sidebar.getChildren().addAll(
                new Label("Dashboard"),
                new Label("Study"),
                new Label("Progress")
        );

        // Center content
        VBox content = new VBox();
        content.setPadding(new Insets(20));

        Label welcome = new Label("Welcome to your Study Assistant");
        welcome.setStyle("-fx-font-size: 16px;");

        content.getChildren().add(welcome);

        // Layout
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(content);

        Scene scene = new Scene(root, 900, 600);

        stage.setScene(scene);
    }
}