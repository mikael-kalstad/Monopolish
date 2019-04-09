package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.sql.SQLException;

/**
 * Controller class for dashboard view
 *
 * @author Mikael Kalstad
 * @version 1.4
 */

public class DashboardController {
    private final String USERNAME = Handler.getAccount().getUsername();

    // Profile info
    @FXML Text username;
    @FXML Text gamesPlayed;
    @FXML Text personalHighscore;

    // Top 3 leaderboard
    @FXML Text highscoreU1;
    @FXML Text highscoreS1;
    @FXML Text highscoreU2;
    @FXML Text highscoreS2;
    @FXML Text highscoreU3;
    @FXML Text highscoreS3;

    // Container for top 10 highscores
    @FXML Pane highscoreContainer;

    /**
     * This method will run before the view is rendered
     */
    @FXML public void initialize() {
        // Prevent exception
        if (Handler.getAccount() != null) {
            username.setText(USERNAME);
            gamesPlayed.setText(String.valueOf(Handler.getAccountDAO().getGamesPlayed(USERNAME)));
            personalHighscore.setText(FxUtils.thousandDecimalFormat(String.valueOf(Handler.getAccountDAO().getHighscore(USERNAME))));
        }

        setLeaderBoard(Handler.getPlayerDAO().getHighscoreList());

        // Logout user on window close
        Handler.getSceneManager().getWindow().setOnCloseRequest(e -> {
            Handler.getAccountDAO().setInactive(Handler.getAccount().getUsername());

            // Close connection
            if (ConnectionPool.getMainConnectionPool() != null) {
                try {
                    ConnectionPool.getMainConnectionPool().shutdown();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Will update leaderboard dynamically bases on highscore data
     *
     * @param highscoreData two-dimensional array with username and score
     */
    private void setLeaderBoard(String[][] highscoreData) {
        for (int i = 0; i < highscoreData.length; i++) {
            // GUI breaks after ten highscores
            if (i > 9) break;

            // Check if score is null, and set default as 0
            // Can be null with result from database
            if (highscoreData[i][0] == null) continue;
            if (highscoreData[i][1] == null) highscoreData[i][1] = String.valueOf(0);

            // Adding thousand format
            highscoreData[i][1] = FxUtils.thousandDecimalFormat(highscoreData[i][1]);

            // Setting top 3 highscores
            if (i == 0) {
                highscoreU1.setText(highscoreData[i][0]);
                highscoreS1.setText(highscoreData[i][1]);
            } else if (i == 1) {
                highscoreU2.setText(highscoreData[i][0]);
                highscoreS2.setText(highscoreData[i][1]);
            } else if (i == 2) {
                highscoreU3.setText(highscoreData[i][0]);
                highscoreS3.setText(highscoreData[i][1]);
            }

            // Setting top 10 highscore board
            drawHighscoreRow(highscoreData[i][0], highscoreData[i][1], i);
        }
    }

    /**
     * Will draw a row consisting of a username and a score.
     * Is used to make a dynamic leaderboard.
     *
     * @param username identity of user in the board
     * @param highscore the actual score
     * @param index leaderboard placement
     */
    private void drawHighscoreRow(String username, String highscore, int index) {
        final int WIDTH = 420;
        final int HEIGHT = 50;

        Pane row = new Pane();
        row.setStyle("-fx-padding: 10px");
        row.setTranslateY(50.0 * index);

        String color = "#EEEEEE";
        // Row should alternate with white background to give contrast
        if (index % 2 != 0 ) {
            color = "white";
        }
        // If the user is in the leaderboard it will be marked to easily see it
        if (username.equals(Handler.getAccount().getUsername())) {
            color = "rgba(252, 108, 108,0.4)";

        }

        row.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-effect: dropshadow(three-pass-box, derive(lightgrey, -20%), 10, 0, 4, 4);"
        );

        // Adding text elements
        Text user = new Text((index + 1) + ". " + username);
        user.setStyle("-fx-font-size: 18px;");

        Text score = new Text(highscore);
        score.setStyle("-fx-font-size: 26px;");

        // Adding text to grid for alignment
        GridPane grid = new GridPane();
        grid.setPrefWidth(WIDTH);
        grid.setPrefHeight(HEIGHT);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);

        grid.getColumnConstraints().addAll(col1, col2);
        grid.add(user, 0, 0);
        grid.add(score, 1, 0);

        row.getChildren().addAll(grid);

        // Add the row to the container
        highscoreContainer.getChildren().add(row);
    }


    /**
     * Change view to login
     */
    public void logout() {
        // Logout user
        Handler.getAccountDAO().setInactive(Handler.getAccount().getUsername());
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
        Handler.resetAccount(); // Make sure account is reset when login out
    }

    /**
     * Change view to lobby
     */
    public void play() {
        Handler.getSceneManager().setScene(ViewConstants.LOBBY.getValue());
    }
}
