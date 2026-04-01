package com.studyapp;

import com.studyapp.view.MainFrame;
import com.studyapp.view.SetupPanel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        Scene setupScene = SetupPanel.createScene(primaryStage, () -> {
            // After successful login → open MainFrame
            MainFrame.show(primaryStage);
        });

        primaryStage.setTitle("Study Assistant");
        primaryStage.setScene(setupScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}