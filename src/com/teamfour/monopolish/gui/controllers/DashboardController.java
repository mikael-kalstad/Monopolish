package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Controller class for dashboard view
 *
 * @author Mikael Kalstad
 * @version 1.2
 */

public class DashboardController {
    // Profile info
    @FXML Text username;
    @FXML Text personalHighscore;

    // Top 3 leaderboard
    @FXML Text highscoreU1;
    @FXML Text highscoreS1;
    @FXML Text highscoreU2;
    @FXML Text highscoreS2;
    @FXML Text highscoreU3;
    @FXML Text highscoreS3;

    // Container for remaining highscores in top 10 (4-10)
    @FXML Pane highscoreContainer;

    // Example data
    private String[][] highscoreData = {
            {"Torbjørn", "10000"}, {"Lisa", "9000"}, {"Mikael", "8734"},
            {"Giske", "8500"}, {"Carlos", "7053"}, {"Siv jensen", "3223"},
            {"Giske", "2300"}, {"Carlos", "1233"}, {"Siv jensen", "1111"},
            {"KNM Helge Ingstad", "0001"}
    };

    /**
     * This method will run before the view is rendered
     */
    @FXML public void initialize() {
        // Prevent exception
        if (Handler.getAccount() != null) {
            username.setText(Handler.getAccount().getUsername());
            personalHighscore.setText(formatWithThousandDecimal(String.valueOf(Handler.getAccount().getHighscore())));

        }
        setLeaderBoard(highscoreData);
    }

    public String formatWithThousandDecimal(String num) {
        if (num.length() < 3) return num;
        int thousandIndex = num.length() - 3;
        return num.substring(0, thousandIndex) + "." + num.substring(thousandIndex);
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

            // Adding thousand format
            highscoreData[i][1] = formatWithThousandDecimal(highscoreData[i][1]);

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

            // Setting rest of highscores
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
        //if (username.equals(Handler.getAccount().getUsername())) ---- USE THIS WHEN DATABAsE IS UP AGAIN ----
        if (username.equals("Mikael")) { // ---- ADD HANDLER ACCOUNT NAME HERE!
            color = "#ff6b5e";
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
        col2.setPercentWidth(20);

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
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }

    /**
     * Change view to game
     */
    public void play() {
        Handler.getSceneManager().setScene(ViewConstants.LOBBY.getValue());
    }
}
