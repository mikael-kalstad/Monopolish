package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
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
    Timer countdownTimer = new Timer();
    @FXML private Text timeValue;

    // Text showing what you voted
    @FXML private Text yourVoteText;

    // Constants
    private final int COUNTDOWN_TIME = 20;
    private final String USERNAME = Handler.getAccount().getUsername();
    private final int GAME_ID = Handler.getCurrentGameId();
    private final int NUM_OF_PLAYERS = Handler.getPlayerDAO().getPlayersInGame(GAME_ID).size();
    private final String VOTE_QUIT_MSG = "You voted to Quit";
    private final String VOTE_CONTINUE_MSG = "You voted to Continue";

    private int votesForQuit = 0;
    private int votesForContinue = 0;
    int time = COUNTDOWN_TIME;

    @FXML public void initialize() {
        timeValue.setStyle("-fx-text-fill: orange");
        timeValue.setText(String.valueOf(COUNTDOWN_TIME));

        // User want to forfeit/quit
        voteQuit.onMouseClickedProperty().set(e -> {
            Handler.getPlayerDAO().setForfeitStatus(USERNAME, GAME_ID, 1);
            yourVoteText.setText(VOTE_QUIT_MSG);
            yourVoteText.setFill(Paint.valueOf("red"));
        });

        // User want to continue the game
        voteContinue.onMouseClickedProperty().set(e -> {
            Handler.getPlayerDAO().setForfeitStatus(USERNAME, GAME_ID, 2);
            yourVoteText.setText(VOTE_CONTINUE_MSG);
            yourVoteText.setFill(Paint.valueOf("green"));
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
        // Quit color
        String color = "#ef5350";
        String hoverColor = "#d13734";
        String backgroundRadius = " 0 0 0 15";

        if (container.getId().equals("voteContinue")) {
            // Continue color
            color = "#52d35e";
            hoverColor = "#01870e";
            backgroundRadius = "0 0 15 0";
        }

        // Final values for variables is needed in lambda functions
        String finalColor = color;
        String finalHoverColor = hoverColor;
        String finalBackgroundRadius = backgroundRadius;

        // Lighten background on hover
        container.setOnMouseEntered(e -> container.setStyle(
                "-fx-background-color: " + finalHoverColor + ";" +
                "-fx-background-radius: " + finalBackgroundRadius + ";"
        ));

        // Reset background when mouse leaves on hover
        container.setOnMouseExited(e -> container.setStyle(
                "-fx-background-color: " + finalColor + ";" +
                "-fx-background-radius: " + finalBackgroundRadius + ";"
        ));
    }

    /**
     * Refresh/update periodically to check what players vote.
     */
    private void refresh() {
        // Timer for refresh
//        Timer refreshTimer = new Timer();
//
//        TimerTask refreshTask = new TimerTask() {
//            @Override
//            public void run() {
//                int[] votes = Handler.getPlayerDAO().getForfeitStatus(GAME_ID);
//                votesForQuit = votes[0];
//                votesForContinue = votes[1];
//
//                // Update vote counts
//                voteCountQuit.setText(String.valueOf(votes[0]));
//                voteCountContinue.setText(String.valueOf(votes[1]));
//
//                if (votesForQuit + votesForContinue == NUM_OF_PLAYERS) {
//                    if (votesForQuit > votesForContinue) {
//                        refreshTimer.cancel();
//                        refreshTimer.purge();
//                        endGame();
//                    } else {
//                        Handler.getForfeitContainer().setVisible(false);
//                    }
//                }
//            }
//        };
//
//        // Update/refresh every second
//        refreshTimer.scheduleAtFixedRate(refreshTask, 0L, 1000L);

        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                int[] votes = Handler.getPlayerDAO().getForfeitStatus(GAME_ID);
                votesForQuit = votes[0];
                votesForContinue = votes[1];

                // Update vote counts
                voteCountQuit.setText(String.valueOf(votes[0]));
                voteCountContinue.setText(String.valueOf(votes[1]));

                checkVotes();

                if (time <= 5) {
                    timeValue.setStyle("-fx-text-fill: red");
                }

                if (time > 0) {
                    time--;
                    timeValue.setText(String.valueOf(time));
                } else {
                    stopTimer();
                    checkVotes();
                }
            }
        };

        // Update/refresh every second
        countdownTimer.scheduleAtFixedRate(countdownTask, 0L, 1000L);
    }

    // Check if every player has voted and set action accordingly
    private void checkVotes() {
        if (votesForQuit + votesForContinue == NUM_OF_PLAYERS) {
            if (votesForQuit > votesForContinue) endGame(); // Quit game
            else Handler.getForfeitContainer().setVisible(false); // Continue game

            stopTimer();
        }
    }

    private void stopTimer() {
        countdownTimer.cancel();
        countdownTimer.purge();
    }

    private void endGame() {
        String res = "Game will continue";
        if (votesForQuit > votesForContinue) res = "Game will quit";

        System.out.println("FORFEIT RESULT: " + res);

        // End game in database
        Handler.getPlayerDAO().endGame(GAME_ID);
        Handler.getGameDAO().finishGame(GAME_ID);

        // Switch to dashboard!
        Platform.runLater(() -> Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue()));
    }
}
