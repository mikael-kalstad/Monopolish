package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    // Standard sizes for scenes (in pixels)
    private final int STANDARD_SCENE_WIDTH = 1940;
    private final int STANDARD_SCENE_HEIGHT = 1040;

    /**
     * Set the exit prompt
     *
     */

    /**
     * Instantiates a new Scene manager.
     *
     *
     * @param stage         the stage of the application
     * @param initial_view the initial view that will render on the stage
     */
    SceneManager(Stage stage, String initial_view) {
       this.window = stage;
       setScene(initial_view);
    }

    /**
     * Sets scene on the stage
     *
     * @param filename the filename
     */
    public void setScene(String filename) {
        // Reset close request for each scene to default
        window.setOnCloseRequest(event -> window.close());

        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + filename));
            Scene scene = new Scene(fxml);
            window.setScene(scene); // Setting new scene to stage
            setSceneScale(window); // Fixing scaling issues
        }
        catch (IOException e) { e.printStackTrace(); }
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

        /*
        System.out.println("Scene width: " + window.getWidth());
        System.out.println("Scene height: " + window.getHeight());
        System.out.println("Screen width: " + screen.getWidth());
        System.out.println("Screen height: " + screen.getHeight());
        System.out.println("Multiplier: width" + sw/window.getScene().getWidth());
        System.out.println("Multiplier: height" + sh/window.getScene().getHeight());
        */

        Scale scale = new Scale(sw/STANDARD_SCENE_WIDTH, sh/STANDARD_SCENE_HEIGHT, 0, 0);
        window.getScene().getRoot().getTransforms().setAll(scale);
    }

    public Stage getWindow() {
        return window;
    }
}
