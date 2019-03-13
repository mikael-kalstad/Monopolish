package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class will handle scene changes to a specific stage.
 *
 * @author Mikael Kalstad
 * @version 1.0
 */
public class SceneManager {
    private Stage window;

    /**
     * Instantiates a new Scene manager.
     *
     *
     * @param stage         the stage of the application
     * @param initial_view the initial view that will render on the stage
     */
    SceneManager(Stage stage, String initial_view) {
       this.window = stage;

       window.setOnCloseRequest(e -> {
            e.consume(); // Override default closing of window
            closeWindow(window); // Run closing method
       });

       setScene(initial_view);
    }

    /**
     * Sets scene on the stage
     *
     * @param filename the filename
     */
    public void setScene(String filename) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + filename));
            Scene scene = new Scene(fxml);
            window.setScene(scene); // Setting new scene to stage
            setSceneScale(window); // Fixing scaling issues
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * This specifies the logic when closing the stage
     *
     * @param stage the target for the closing logic
     */
    private void closeWindow(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning");
        alert.setContentText("Are you sure you want to close the application?");

        ButtonType okBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(okBtn, noBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == okBtn) stage.close();
        });
    }

    /**
     * This method will scale the scene to fit within a stage
     * Note: also used in Handler (when resizing)
     *
     * @param window Stage target
     */
    public void setSceneScale(Stage window) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double sw = window.getWidth();
        double sh = window.getHeight();

        Scale scale = new Scale(sw/screen.getWidth() * 0.95, sh/screen.getHeight() * 0.95, 0, 0);
        window.getScene().getRoot().getTransforms().setAll(scale);
    }
}
