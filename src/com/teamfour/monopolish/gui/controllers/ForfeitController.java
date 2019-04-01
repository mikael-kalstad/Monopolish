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

    // Countdown time
    @FXML private Text timeValue;

    // Constants
    private final int COUNTDOWN_TIME = 60;
    private final String USERNAME = Handler.getAccount().getUsername();
    private final int GAME_ID = Handler.getCurrentGameId();
    private final int NUM_OF_PLAYERS = Handler.getPlayerDAO().getPlayersInGame(GAME_ID).size();

    private int votesForQuit = 0;
    private int votesForContinue = 0;

    @FXML public void initialize() {
        timeValue.setText(String.valueOf(COUNTDOWN_TIME));

        voteQuit.onMouseClickedProperty().set(e -> {
            Handler.getPlayerDAO().setForfeitStatus(USERNAME, GAME_ID, 1);
        });

        voteContinue.onMouseClickedProperty().set(e -> {
            Handler.getPlayerDAO().setForfeitStatus(USERNAME, GAME_ID, 2);
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
        String color = "#ef5350";  // Quit color
        if (container.getId().equals("voteContinue")) color = "#009e0f"; // Continue color

        String finalColor = color;

        // Lighten background on hover
        container.setOnMouseEntered(e -> {
            container.setStyle(
                    "filter: brightness(-20%);" +
                    "-fx-background-color: " + finalColor + ";"
            );
        });

        // Reset background when mouse leaves on hover
        container.setOnMouseExited(e -> {
            container.setStyle(
                    "filter: brightness(100%);" +
                    "-fx-background-color: " + finalColor + ";"
            );
        });
    }

    /**
     * Refresh/update periodically to check what players vote.
     */
    private void refresh() {
        // Timer for refresh
        Timer refreshTimer = new Timer();

        TimerTask refreshTask = new TimerTask() {
            @Override
            public void run() {
                int[] votes = Handler.getPlayerDAO().getForfeitStatus(GAME_ID);
                votesForQuit = votes[0];
                votesForContinue = votes[1];

                // Update vote counts
                voteCountQuit.setText(String.valueOf(votes[0]));
                voteCountContinue.setText(String.valueOf(votes[1]));

                if (votesForQuit + votesForContinue == NUM_OF_PLAYERS) {
                    // End game
                }
            }
        };

        // Update/refresh every second
        refreshTimer.scheduleAtFixedRate(refreshTask, 0L, 1000L);

        // Timer for countdown
        Timer countdownTimer = new Timer();

        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                int time = COUNTDOWN_TIME;

                if (time > 0) {
                    time--;
                    timeValue.setText(String.valueOf(time));
                } else {
                    endGame();
                }
            }
        };

        // Update/refresh every second
        countdownTimer.scheduleAtFixedRate(countdownTask, 0L, 1000L);
    }

    private void endGame() {
        String res = "Game will continue";
        if (votesForQuit > votesForContinue) res = "Game will quit";

        System.out.println("FORFEIT RESULT: " + res);
    }
}
