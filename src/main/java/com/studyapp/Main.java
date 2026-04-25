package com.studyapp;

import com.studyapp.controller.CredentialHandler;
import com.studyapp.controller.MainController;
import com.studyapp.view.MainFrame;
import com.studyapp.view.SetupPanel;

import com.studyapp.view.SplashScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    public static final MainController MAIN_CONTROLLER = new MainController();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        if (MAIN_CONTROLLER.tryAutoLogin()) {
            SplashScreen.show(primaryStage, MAIN_CONTROLLER, () -> {});
        } else {
            Scene setupScene = SetupPanel.createScene(primaryStage, MAIN_CONTROLLER, () ->
                    SplashScreen.show(primaryStage, MAIN_CONTROLLER, () -> {})
            );
            primaryStage.setTitle("Study Assistant");
            primaryStage.setScene(setupScene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        // Optional CLI fallback:
        // new CLIView(new MainController()).start();

        CredentialHandler.clear(); //COMMENT THIS OUT TO ENABLE AUTO LOGIN
        Application.launch(args);
    }
}
