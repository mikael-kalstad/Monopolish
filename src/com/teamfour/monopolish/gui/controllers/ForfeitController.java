package com.teamfour.monopolish.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public class ForfeitController {
    // Containers for voting
    @FXML private Pane voteQuit;
    @FXML private Pane voteContinue;

    // Vote count in containers
    @FXML private Text voteCountQuit;
    @FXML private Text voteCountContinue;

    @FXML public void initialize() {
        voteQuit.onMouseClickedProperty().set(e -> {
            // DAO REQUEST HERE
        });

        voteContinue.onMouseClickedProperty().set(e -> {
            // DAO REQUEST HERE
        });

        // Change color on hover to show selection
        setOnHover(voteQuit);
        setOnHover(voteContinue);

        // Start refreshing method
        refresh();
    }

    /**
     * Change container style on hover,
     * will change the brightness to more easily see which container is selected
     *
     * @param container Target container for on hover
     */
    private void setOnHover(Pane container) {
        // Lighten background on hover
        container.setOnMouseEntered(e -> container.setStyle("filter: brightness(-20%);"));

        // Reset background when mouse leaves on hover
        container.setOnMouseExited(e -> container.setStyle("filter: brightness(100%);"));
    }

    /**
     * Refresh/update periodically to check what players vote.
     */
    private void refresh() {
        Timer refreshTimer = new Timer();

        TimerTask refreshTask = new TimerTask() {
            @Override
            public void run() {
                // GET DAO REQUEST HERE
                int[] votes = new int[]{1, 2};

                // Update vote counts
                voteCountQuit.setText(String.valueOf(votes[0]));
                voteCountContinue.setText(String.valueOf(votes[1]));
            }
        };

        // Update/refresh every second
        refreshTimer.scheduleAtFixedRate(refreshTask, 0L, 1000L);
    }
}
