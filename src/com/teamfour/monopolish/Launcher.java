package com.teamfour.monopolish;

import com.teamfour.monopolish.gui.controllers.Handler;
import com.teamfour.monopolish.gui.views.SceneManager;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Launcher extends Application {
    // Constants for Application
    private final String APPLICATION_TITLE = "Monopoly";
    private final String APPLICATION_LOGO = "res/gui/Dices/dice5.png";
    private final double ASPECT_RATIO = 16.0/9.0;
    private final String INITIAL_VIEW = ViewConstants.LOGIN.getValue();

    /**
     * This method will start the application on the initial view
     *
     * @param primaryStage The target window
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.getIcons().add(new Image("file:" + APPLICATION_LOGO));

        // Setting full screen size to stage
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screen.getWidth());
        primaryStage.setHeight(screen.getHeight());
        primaryStage.setMaximized(true);

        // Setting initial view (login)
        Handler.setSceneManager(new SceneManager(primaryStage, INITIAL_VIEW));
        primaryStage.show();

        // Event listener for resizing
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            // Fixing scene scaling issues
            Handler.getSceneManager().setSceneScale(primaryStage);

            // Setting height
            primaryStage.setHeight(primaryStage.getWidth() / (ASPECT_RATIO));
        };

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);
    }
}
