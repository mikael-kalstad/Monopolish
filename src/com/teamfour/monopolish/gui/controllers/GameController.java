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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    @FXML private Label yourname, p1name, p1money, p2name, p2money, p3name, p3money, p4name, p4money;
    @FXML private HBox player1view, player2view, player3view, player4view;
    @FXML private VBox playerInfo;
    @FXML private TextFlow propertycard;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    // Dice elements
    @FXML ImageView dice1_img, dice2_img;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, buyBtn, claimrentBtn;
    @FXML private Label moneyValue, roundValue, statusValue;

    // Chat
    @FXML private Pane chatContainer;
    @FXML private Pane chatMessages;
    private boolean chatOpen = false;

    // Properties dialog
    @FXML private FlowPane propertyContainer;

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
        drawAllPlayersView();
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
        drawYourPlayerView();
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

    public void showProperties() {
        propertyContainer.setVisible(true);
        System.out.println("show properties!");
    }

    public void closePropertiesDialog() {
        propertyContainer.setVisible(false);

    }

    /**
     * This method will open or close the chat,
     * depending if the chat is open or closed.
     */
    public void toggleChat() {
        // Open chat
        if (chatOpen) {
            chatContainer.setTranslateY(0); // Set to default position
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
                //Platform.runLater(() -> {
                    try {
                        // If it's your turn, break out of the timer
                        int result = gameLogic.isNewTurn();
                        if (result == 1) {
                            System.out.println("Your turn");
                            newTurn(true);
                        } else if (result == 0) {
                            System.out.println("Updated board");
                            newTurn(false);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                //});
            }
        }, 0l, 1000l);
    }

    public void updateBoard() {
        try {
            int[] positions = gameLogic.getPlayerPositions();
            for (int i = 0; i < positions.length; i++) {
                movePlayer(playerList.get(i), positions[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void newTurn(boolean yourTurn) {
        try {
            gameLogic.newTurn(yourTurn);
            updateBoard();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (yourTurn) {
            timer.cancel();
            rolldiceBtn.setDisable(false);
        }
    }

    public void yourTurn() {
        // Beginning of your turn
        try {
            gameLogic.startYourTurn();
            updateBoard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        timer.cancel();
        rolldiceBtn.setDisable(false);
    }

    /**
     * Will roll two dices and get random values between 1-6.
     * This method will also update corresponding dice images in the GUI.
     */
    public void rollDice(){
        // Get values for two dices
        int[] diceValues = null;
        try { diceValues = gameLogic.throwDice(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Check if diceValues array is initialized or length is less than two
        if (diceValues == null || diceValues.length < 2) return;

        // Update dice images on board
        dice1_img.setImage(new Image(("file:res/gui/dices/dice"+ diceValues[0] +".png")));
        dice2_img.setImage(new Image(("file:res/gui/dices/dice"+ diceValues[1] +".png")));

        String s = "Threw dice:  "+ diceValues[0] + ",  " + diceValues[1];
        addToEventlog(s);
        updateBoard();
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

    public void claimrent() {

    }


    private void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }

    private void drawYourPlayerView(){
        yourname.setText(gameLogic.getYourPlayer().getUsername());
    }

    private void drawAllPlayersView(){
        p1name.setText(playerList.get(0).getUsername());
        p2name.setText(playerList.get(1).getUsername());
        if (playerList.size() >= 3) {
            player3view.setVisible(true);
            p3name.setText(playerList.get(2).getUsername());
        }
        if (playerList.size() == 4) {
            player4view.setVisible(true);
            p4name.setText(playerList.get(3).getUsername());
        }
    }
}