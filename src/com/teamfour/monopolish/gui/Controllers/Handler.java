package com.teamfour.monopolish.gui.Controllers;

import com.teamfour.monopolish.gui.Views.ViewConstants;
import javafx.application.Application;
import javafx.stage.Stage;

public class Handler extends Application {
    private static SceneManager sceneManager;

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    // Testing purposes
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initial view is login
        sceneManager = new SceneManager(primaryStage, ViewConstants.LOGIN.getValue());
        primaryStage.show();
    }

    /*public static void main(String[] args) {
        launch(args);
    }*/
}
