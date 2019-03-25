package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
 * @version 1.6
 */

public class GameController {
    private Timer timer = new Timer();
    private GameLogic gameLogic = new GameLogic(Handler.getCurrentGameId());

    // Array for events in game
    private ArrayList<Text> eventList = new ArrayList<>();

    // Array for players in game
    private ArrayList<FxPlayer> playerList = new ArrayList<>();

    // Elements in board
    @FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;
    @FXML private HBox player1view, player2view, player3view;
    @FXML private VBox playerInfo;
    @FXML private TextFlow propertycard;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, buyBtn, claimrentBtn;
    @FXML private Text moneyValue, roundValue, statusValue;

    // Chat
    @FXML private Pane chatContainer;
    @FXML private Pane chatMessages;
    private boolean chatOpen = false;

    @FXML
    public void initialize() {
        // Load gamelogic and initialize the game setup
        try { gameLogic.setupGame(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Draw players based on the number of players
        String[] playerTurns = gameLogic.getTurns();
        for (int i = 0; i < playerTurns.length; i++) {
            playerList.add(new FxPlayer(playerTurns[i], FxPlayer.getMAX(), FxPlayer.getMAX()));
        }

        drawPlayers();
        drawPlayerInfo();
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

    /**
     * Method that will run when the user wants to leave the game.
     */
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

    public void forfeit() {
        // Some voting gui and logic here...
    }

    /**
     * This method will open or close the chat,
     * depending if the chat is open or closed.
     */
    public void toggleChat() {
        // Open chat
        if (chatOpen) {
            chatContainer.setTranslateY(-275); // Move down
            chatOpen = false;
        }

        // Open chat
        else {
            chatContainer.setTranslateY(275); // Move up
            chatOpen = true;
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
        rolldiceBtn.setDisable(true);
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
        rolldiceBtn.setDisable(false);
    }

    public void rollDice(){
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
        updatePlayerPositions();
        //movePlayer(playerList.get(gameLogic.getTurnNumber()), dice1+dice2);
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

    public void buy() {
        addToEventlog("Æsj");
    }


    private void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }

    private void drawPlayerInfo(){
        if (playerList.size() >= 2) {
            if (playerList.size() == 3) {
                player2view.setVisible(true);
                p1name.setText(playerList.get(0).getUsername());
            }
            if (playerList.size() == 4) {
                player2view.setVisible(true);
                player3view.setVisible(true);
            }
        }
    }
}