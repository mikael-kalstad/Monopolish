package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private GameLogic gameLogic = new GameLogic(Handler.getCurrentGameId());
    private ArrayList<Text> eventList = new ArrayList<>();
    private ArrayList<FxPlayer> playerList = new ArrayList<>();
    //@FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;

    @FXML private Button rolldice;
    @FXML private TextFlow propertycard;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    @FXML private ImageView dice1_img;
    @FXML private ImageView dice2_img;

    @FXML public void initialize() {
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
                timer.cancel(); // Stop timer thread

                // Remove player from lobby
                final String USERNAME = Handler.getAccount().getUsername();
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));

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
                int lobbyId = Handler.getLobbyDAO().getLobbyId(USERNAME);
                System.out.println("lobby id when leaving... " + lobbyId);
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

    public void updatePlayerPositions() {
        int[] positions = gameLogic.getPlayerPositions();
        for (int i = 0; i < positions.length; i++) {
            movePlayer(playerList.get(i), positions[i]);
        }
    }

    public void yourTurn() {
        // Beginning of your turn
        try {
            gameLogic.startYourTurn();
            updatePlayerPositions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        timer.cancel();
        rolldice.setDisable(false);
    }

    /**
     * This method will find two random number (0-6) and update dice images on the board.
     */
    public void rollDice(){
        int[] dice = null;
        try { dice = gameLogic.throwDice(); }
        catch (SQLException e) { e.printStackTrace(); }
        if (dice == null || dice.length == 0) return;

        updateDice(dice1_img, dice[0]);
        updateDice(dice2_img, dice[1]);
        String s = "Threw dice:  "+ dice[0] + ",  " + dice[1];
        addToEventlog(s);
        updatePlayerPositions();
        //movePlayer(playerList.get(gameLogic.getTurnNumber()), dice1+dice2);
        waitForTurn();
    }

    /**
     * This method will update the dice image shown on the board
     *
     * @param diceImg Target ImageView
     * @param diceNum Value of the dice
     */
    private void updateDice(ImageView diceImg, int diceNum) {
        // Check value of the dice and change image accordingly
        switch (diceNum) {
            case 1:
                diceImg.setImage(new Image("file:res/gui/dices/dice1.png"));
                break;
            case 2:
                diceImg.setImage(new Image("file:res/gui/dices/dice2.png"));
                break;
            case 3:
                diceImg.setImage(new Image("file:res/gui/dices/dice3.png"));
                break;
            case 4:
                diceImg.setImage(new Image("file:res/gui/dices/dice4.png"));
                break;
            case 5:
                diceImg.setImage(new Image("file:res/gui/dices/dice5.png"));
                break;
            case 6:
                diceImg.setImage(new Image("file:res/gui/dices/dice6.png"));
                break;
        }
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
        player.posToXY(steps);
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

    public void setBuy() {
        addToEventlog("Æsj");
    }

    public void setHome() {
        addToEventlog("I'm afraid i can't do that...");
    }

    private void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }
}