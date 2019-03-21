package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for game view
 *
 * @author Bård Hestmark
 * @version 1.5
 */

public class GameController {
    // Timers

    private Timer timer = new Timer();

    private GameLogic gameLogic = new GameLogic(1);
    private ArrayList<Text> eventList = new ArrayList<>();
    private ArrayList<FxPlayer> playerList = new ArrayList<>(); //hentes fra et annet sted, lobby?
    //@FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;
    @FXML
    private Button rolldice;
    @FXML
    private TextFlow propertycard;
    @FXML
    private GridPane gamegrid;
    @FXML
    private ListView eventlog;

    @FXML
    public void initialize() {
        // Load gamelogic and initialize the game setup
        try {
            gameLogic.setupGame();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Draw players based on the number of players
        String[] playerTurns = gameLogic.getTurns();
        for (int i = 0; i < playerTurns.length; i++) {
            playerList.add(new FxPlayer(playerTurns[i], FxPlayer.getMAX(), FxPlayer.getMAX()));
        }

        drawPlayers();

        // Start the game!
        waitForTurn();

        // Set default alert box for leaving


        // When window is closed
        Handler.getSceneManager().getWindow().setOnCloseRequest(e -> {
            e.consume(); // Override default closing method

            Alert alertDialog = AlertBox.display (
                    Alert.AlertType.CONFIRMATION,
                    "Warning", "Do you want to leave?",
                    "You will not be able to join later if you leave"
            );
            alertDialog.showAndWait();

            // Check if yes button is pressed
            if (alertDialog.getResult().getButtonData().isDefaultButton()) {
                // Remove player from lobby
                final String USERNAME = Handler.getAccount().getUsername();
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));

                timer.cancel(); // Stop timer thread

                // Close the window
                Handler.getSceneManager().getWindow().close();
            }
        });
    }

    public void leave() {
        Alert alertDialog = AlertBox.display (
                Alert.AlertType.CONFIRMATION,
                "Warning", "Do you want to leave?",
                "You will not be able to join later if you leave"
        );
        alertDialog.showAndWait();

        if (alertDialog.getResult().getButtonData().isDefaultButton()) {
            timer.cancel(); // Stop timer thread

            if (alertDialog.getResult().getButtonData().isDefaultButton()) {
                // Remove player from lobby
                final String USERNAME = Handler.getAccount().getUsername();
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));

                // Change view to dashboard
                Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
            }
        }
    }

    /**
     * Initiates a clock that runs on a separate thread. This clock
     * checks the database each second to see if you are the current player.
     * You will not be able to roll the dice while this clock runs. When
     * it's registered that you are the current player, the clock stops
     * and re-enables the 'roll dice' button. At the end of your turn, the
     * clock will start again
     */
    private void waitForTurn() {
        rolldice.setDisable(true);
        // Create a timer object
        timer = new Timer();
        // We'll schedule a task that will check against the database
        // if it's your turn every 1 second
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                Platform.runLater(() -> {
                    try {
                        // If it's your turn, break out of the timer
                        if (gameLogic.isYourTurn()) {
                            System.out.println("Your turn");
                            yourTurn();
                        } else {
                            System.out.println("Not your turn");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0l, 1000l);
    }

    public void yourTurn() {
        try {
            gameLogic.startYourTurn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        timer.cancel();
        rolldice.setDisable(false);
    }

    public void setRolldice(){
        int[] dice = null;
        try {
            dice = gameLogic.throwDice();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int dice1 = dice[0];
        int dice2 = dice[1];
        String s = "Threw dice:  "+ dice1 + ",  " + dice2;
        addToEventlog(s);
        movePlayer(playerList.get(gameLogic.getTurnNumber()), dice1+dice2);
        waitForTurn();
    }

    public void drawPlayers() {

        for (FxPlayer player : playerList) {
            GridPane.setConstraints(player, player.getPosX(), player.getPosY());
        }
        checkForOthers(playerList.get(0));

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);
    }

    public void movePlayer(FxPlayer player, int steps) {
        player.move(steps);
        player.setAlignment(Pos.CENTER);
        GridPane.setConstraints(player, player.getPosX(), player.getPosY());

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);

        checkForOthers(player);

        String pos = player.getUsername() + " moved to X: " + player.getPosX() + " Y:" + player.getPosY();
        addToEventlog(pos);
    }

    private void checkForOthers(FxPlayer player) {

        ArrayList<FxPlayer> checklist = new ArrayList<>();

        for (FxPlayer p : playerList) {
            if ((p.getPosX() == player.getPosX()) && (p.getPosY() == player.getPosY())) {
                checklist.add(p);
            }
        }

        if (checklist.size() > 1) {
            if (checklist.size() == 2) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
            }
            if (checklist.size() == 3) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
                checklist.get(2).setAlignment(Pos.BOTTOM_CENTER);
            }
            if (checklist.size() == 4) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
                checklist.get(2).setAlignment(Pos.BOTTOM_LEFT);
                checklist.get(3).setAlignment(Pos.BOTTOM_RIGHT);
            }
        }
    }

    private void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }
}