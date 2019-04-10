package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MessagePopupController {
    private static @FXML Pane container;
    private static final int TRANSLATE_Y = 120;
    private static final int ANIMATION_DURATION = 400;
    private static int COUNTDOWN_TIME = 5;
    private static int time = 0;
    private static Timer timer;

    /**
     * Setup the container for the messages. Should only be run once when using this class.
     *
     * @param container Target container for message popups
     * @param delay How long the message will be displayed before fading out.
     */
    public static void setup(Pane container, int delay) {
        MessagePopupController.container = container;
        MessagePopupController.COUNTDOWN_TIME = delay;
    }

    /**
     * Calling this method will display a message popup in the container defined in setup.
     * A fade in and out animation will be displayed, the popups will stack on top of each other.
     * After a delay the popup will fade out, this can be defined in setup.
     *
     * @param msg Displayed message in popup
     * @param logoName Filename of the target logo, must be located in src/res/gui/MessagePopup
     * @param title Title of the message popup
     */
    public static void show (String msg, String logoName, String title) {
        Pane messagePopup = null;
        try {
            messagePopup = FXMLLoader.load(MessagePopupController.class.getResource(ViewConstants.FILE_PATH.getValue() + ViewConstants.MESSAGE_POPUP.getValue()));
        }
        catch (IOException e) { e.printStackTrace(); }

        if (messagePopup == null || container == null) return;

        // Find and set text element
        Text textElement = (Text) messagePopup.getChildren().get(0);
        textElement.setText(msg);

        // Find and set logo if defined
        if (logoName != null && !logoName.equals("")) {
            ImageView logo = (ImageView) messagePopup.getChildren().get(1);
            logo.setImage(new Image("file:res/gui/MessagePopup/" + logoName));
        }

        // Find and set title if defined
        if (title != null && !title.equals("")) {
            Label titleValue = (Label) messagePopup.getChildren().get(3);
            titleValue.setText(title);
        }

        messagePopup.setId(String.valueOf(time));

        // Check if timer should start
        if (container.getChildren().size() == 0) {
            startTimerCheck();
        }

        // Move messagePopup and add to container
        container.getChildren().add(messagePopup);
        //messagePopup.setTranslateY(TRANSLATE_Y);

        // Hide container on click
        Pane finalMessagePopup = messagePopup;
        messagePopup.setOnMouseClicked(e -> animateMovement(false, finalMessagePopup));

        // Slide and fade container in
        animateMovement(true, messagePopup);
    }

    /**
     * Calling this method will display a message popup in the container defined in setup.
     *  A fade in and out animation will be displayed, the popups will stack on top of each other.
     *  After a delay the popup will fade out, this can be defined in setup.
     *  Notice this method will display a standard info logo.
     * @param msg Displayed message in popup
     */
    public static void show(String msg) {
        MessagePopupController.show(msg, null);
    }

    /**
     * Calling this method will display a message popup in the container defined in setup.
     *  A fade in and out animation will be displayed, the popups will stack on top of each other.
     *  After a delay the popup will fade out, this can be defined in setup.
     *  Notice this method will display a standard info logo.
     * @param msg Displayed message in popup
     * @param logoName Filename of the target logo, must be located in src/res/gui/MessagePopup
     */
    public static void show (String msg, String logoName) { MessagePopupController.show(msg, logoName, null); }

    private static void startTimerCheck() {
        // Start timer
        timer = new Timer();

        // Countdown to zero seconds
        TimerTask countdown = new TimerTask() {
            @Override
            public void run() {
                time++;

                // Check if messages should be removed
                for (Node m : container.getChildren()) {
                    if (Integer.valueOf(m.getId()) <= time - COUNTDOWN_TIME) {
                        animateMovement(false, (Pane) m);
                    }
                }

                // Stop timer if there are no messages
                if (container.getChildren().size() == 0) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.scheduleAtFixedRate(countdown, 0L, 1000L);
    }

    private static void animateMovement(boolean show, Pane messagePopup) {
        int numOfMessages = container.getChildren().size() + 1;

        int translateY = TRANSLATE_Y;
        double opacityFrom = 1.0;
        double opacityTo = 0.0;

        // Reverse animation
        if (show) {
            translateY *= -1;
            opacityFrom = 0.0;
            opacityTo = 1.0;
        }

        // Setup translate- and fade-transition
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATION_DURATION), messagePopup);
        tt.setByY(translateY * numOfMessages);

        // Fade message in and out
        FadeTransition ft = new FadeTransition(Duration.millis(ANIMATION_DURATION), messagePopup);
        ft.setFromValue(opacityFrom);
        ft.setToValue(opacityTo);

        // Show both animations at the same time
        ParallelTransition pt = new ParallelTransition(tt, ft);

        // Play transition
        pt.play();

        // Hide container visibility
        if (!show) {
            pt.setOnFinished(e -> container.getChildren().remove(messagePopup));
        }
    }
}
