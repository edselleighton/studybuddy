package com.studyapp.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFrame {

    public static void show(Stage stage) {

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f7f9fc;");
        
        // The padding will be handled by the internal components.

        // Sidebar
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setMinWidth(250); // Set a minimum width for the sidebar
        sidebar.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 0; -fx-border-color: #e2e8f0;"); // Only right border

        // Navigation button
        Button dashBtn = new Button("Dashboard");
        dashBtn.setMaxWidth(Double.MAX_VALUE); // Let button fill sidebar width
        dashBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6;");

        dashBtn.setOnAction(e -> {
            mainLayout.setCenter(DashboardPanel.create());
        });

        sidebar.getChildren().addAll(dashBtn);

        // Default view
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(DashboardPanel.create());

        // Scene
        Scene scene = new Scene(mainLayout, 850, 550);

        // Apply scene
        stage.setScene(scene);
        stage.setMaximized(true); // Maximizes the window
    }
}