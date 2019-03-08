package com.teamfour.monopolish.gui.Controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SceneSwitcher extends Application {
    private Stage window;
    private Scene startScreen;

    public SceneSwitcher(Stage stage, Scene startScreen) {
        this.window = stage;
        this.startScreen = startScreen;
    }

    void changeScene(Scene newScene) {
        window.setScene(newScene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Monopoly");
        window.setScene(startScreen);

        window.setOnCloseRequest(e -> {
            e.consume(); // Override default closing of window
            closeProgram(window); // Run closing method
        });
    }

    // Method for closing logic
    private void closeProgram(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning");
        alert.setContentText("Are you sure you want to leave?");

        ButtonType okBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(okBtn, noBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == okBtn) stage.close();
        });
    }
}
