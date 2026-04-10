package com.studyapp;

import com.studyapp.controller.MainController;
import com.studyapp.view.MainFrame;
import com.studyapp.view.SetupPanel;
import com.studyapp.view.CLIView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene setupScene = SetupPanel.createScene(primaryStage, () -> MainFrame.show(primaryStage));
        primaryStage.setTitle("Study Assistant");
        primaryStage.setMaximized(false);
        primaryStage.setScene(setupScene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Optional CLI fallback:
        // new CLIView(new MainController()).start();
        Application.launch(args);
    }
}
