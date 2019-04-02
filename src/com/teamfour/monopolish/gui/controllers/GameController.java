package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.Board;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for game view. This class acts as a communication layer between the player and the GameLogic class.
 * We also use this class a general layout of the main game loop. Here, we control when the user should be able
 * to click certain buttons, what happens when you click them, and handles all graphical interfaces and updates
 *
 * @author Bård Hestmark
 * @version 1.9
 */

public class GameController {
    // Timer for checking database updates
    private Timer databaseTimer = new Timer();

    // GameLogic for handling more intricate game operations
    private GameLogic gameLogic;
    private int current_money = 0;
    private final String USERNAME = Handler.getAccount().getUsername();

    // Array for events in game
    private ArrayList<Text> eventList = new ArrayList<>();

    // Background overlays
    @FXML private Pane backgroundOverlay;
    @FXML private Pane helpOverlay;

    // Elements in board
    @FXML private AnchorPane cardContainer;
    @FXML private Button buyPropertyBtn, payRentBtn;
    @FXML private Label propertyOwned;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    // Dice images in board
    @FXML ImageView dice1_img, dice2_img;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, endturnBtn, payBailBtn;
    @FXML private Label roundValue, statusValue;
    @FXML private Label username, userMoney;
    @FXML private Pane userColor, userPropertiesIcon;
    @FXML private Text roundTimeValue;
    @FXML private Pane opponentsContainer;

    // Container for chat element
    @FXML private Pane chatContainer;
    @FXML private Pane forfeitContainer;

    // Properties dialog
    @FXML private Pane propertiesContainer;
    @FXML private FlowPane propertiesContentContainer;
    @FXML private Text propertiesUsername;
    @FXML private Button tradeBtn;
    @FXML private Text tradeMsg;

    // Container for trade
    @FXML private Pane tradeContainer;

    // Message popup
    @FXML private Pane messagePopup;
    @FXML private Text msgPopupText;
    @FXML private Pane messagePopupContainer;

    /**
     * Launches when the scene is loaded.
     */
    @FXML public void initialize() {
        // Reference that is used in other controllers
        Handler.setForfeitContainer(forfeitContainer);
        Handler.setTradeContainer(tradeContainer);

        // Set gamelogic object in handler
        Handler.setGameLogic(new GameLogic(Handler.getCurrentGameId()));
        gameLogic = Handler.getGameLogic();

        // Load gamelogic and initialize the game setup
        try { gameLogic.setupGame(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Setup messagePop
        MessagePopupController.setup(messagePopupContainer);

        updateBoard();

        // Start the game!
        waitForTurn();

        // Load chat
        try {
            Node chat = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + ViewConstants.CHAT.getValue()));
            chatContainer.getChildren().add(chat);
        }
        catch (IOException e) { e.printStackTrace(); }

        // Start forfeit timer
        Timer forfeitTimer = new Timer();

        TimerTask forfeitTask = new TimerTask() {
            @Override
            public void run() {
                int[] votes = Handler.getPlayerDAO().getForfeitStatus(Handler.getCurrentGameId());

                if (votes[0] != 0 || votes[1] != 0 && !forfeitContainer.isVisible()) {
                    // Show forfeit dialog
                    Platform.runLater(() -> forfeit());

                    // Stop timer
                    forfeitTimer.cancel();
                    forfeitTimer.purge();
                }
            }
        };

        // Check for forfeit every second
        forfeitTimer.scheduleAtFixedRate(forfeitTask, 0L, 1000L);

        // Set default alert box for leaving when window is closed
        Handler.getSceneManager().getWindow().setOnCloseRequest(e -> {
            e.consume(); // Override default closing method

            Alert alertDialog = AlertBox.display(
                    Alert.AlertType.CONFIRMATION,
                    "Warning", "Do you want to leave?",
                    "You will not be able to join later if you leave"
            );
            alertDialog.showAndWait();

            // Check if yes button is pressed
            if (alertDialog.getResult().getButtonData().isDefaultButton()) {
                // Remove player from lobby
                Handler.getAccountDAO().setInactive(USERNAME);
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
                gameLogic.getEntityManager().removePlayer(USERNAME);
                databaseTimer.cancel(); // Stop databaseTimer thread
                databaseTimer.purge();
                ChatController.getChatTimer().cancel();
                ChatController.getChatTimer().purge();

                // Logout user
                Handler.getAccountDAO().setInactive(USERNAME);

                // Close the window
                Handler.getSceneManager().getWindow().close();
            }
        });
    }

    /**
     * Method that will run when the user wants to leave the game.
     */
    public void leave() {
        // Create and display alert box when leaving
        Alert alertDialog = AlertBox.display(
                Alert.AlertType.CONFIRMATION,
                "Warning", "Do you want to leave?",
                "You will not be able to join later if you leave"
        );
        alertDialog.showAndWait();

        if (alertDialog.getResult().getButtonData().isDefaultButton()) {
            databaseTimer.cancel(); // Stop timer thread
            databaseTimer.purge();
            ChatController.getChatTimer().cancel();
            ChatController.getChatTimer().purge();

            // Remove player from lobby
            int lobbyId = Handler.getLobbyDAO().getLobbyId(USERNAME);
            System.out.println("lobby id when leaving... " + lobbyId);
            Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
            gameLogic.getEntityManager().removePlayer(USERNAME);

            // Change view to dashboard
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
            databaseTimer.cancel(); // Stop timer thread
            databaseTimer.purge();
        }
    }

    /**
     * Toggle help overlay with help button
     */
    public void toggleHelpOverlay() {
        if (helpOverlay.isVisible()) helpOverlay.setVisible(false);
        else helpOverlay.setVisible(true);
    }

    /**
     * Will run when the forfeit button is clicked.
     * A forfeit dialog will appear on the screen
     */
    public void forfeit() {
        // Load forfeit GUI
        addElementToContainer(ViewConstants.FORFEIT.getValue(), forfeitContainer);

        // Show background overlay
        backgroundOverlay.setVisible(true);

        // Hide properties dialog and show forfeit dialog
        propertiesContainer.setVisible(false);
        forfeitContainer.setVisible(true);
    }

    /**
     * Load element from .fxml file and add to container
     *
     * @param filename Target .fxml file
     * @param container Target container
     */
    private void addElementToContainer(String filename, Pane container) {
        try {
            Node element = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + filename));
            container.getChildren().add(element);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Show a popup dialog with all the properties belonging to a player
     *
     * @param username Target user
     */
    public void showProperties(String username) {
        // Show backgroundOverlay
        backgroundOverlay.setVisible(true);

        // Clear all properties that already may exists inside the dialog
        propertiesContentContainer.getChildren().clear();

        // Show properties dialog and set username
        propertiesContainer.setVisible(true);
        propertiesUsername.setText(username);

        // Add all properties that the user owns to the dialog
        for (Property p : gameLogic.getPlayer(username).getProperties()) {
            Pane card = GameControllerDrawFx.createPropertyCard(p);
            propertiesContentContainer.getChildren().add(card);
        }

        // Check if trade btn and msg should be shown
        if (username.equals(USERNAME)) {
            tradeBtn.setVisible(false);
            tradeMsg.setVisible(false);
        } else {
            tradeBtn.setVisible(true);
            tradeMsg.setVisible(true);
        }

        tradeBtn.setOnMouseClicked(e -> {
            propertiesContainer.setVisible(false);
            backgroundOverlay.setVisible(false);
            tradeContainer.getChildren().clear();
            tradeContainer.setVisible(true);
            Handler.setTradeUsername(username);
            addElementToContainer(ViewConstants.TRADING.getValue(), tradeContainer);
        });

        // Close dialog if background is clicked
        backgroundOverlay.setOnMouseClicked(e -> {
            closePropertiesDialog();
            backgroundOverlay.setVisible(false); // Hide background
        });
    }

    /**
     * Hide the popup dialog showing the properties to a player
     */
    public void closePropertiesDialog() {
        propertiesContainer.setVisible(false);
        backgroundOverlay.setVisible(false);
    }

    /**
     * Hide the popup dialog showing trading
     */
    public void closeTradeDialog() {
        tradeContainer.setVisible(false);
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
        // Create a databaseTimer object
        databaseTimer = new Timer();

        // We'll schedule a task that will check against the database
        // if it's your turn every 1 second
        databaseTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    try {
                        // If it's your turn, break out of the databaseTimer
                        int result = gameLogic.isNewTurn();
                        if (result == 1) {
                            newTurn(true);
                        } else if (result == 0) {
                            newTurn(false);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0l, 1000l);
    }

    /**
     * Will roll two dices and get random values between 1-6.
     * This method will also update corresponding dice images in the GUI.
     */
    public void rollDice() {
        // Get values for two dices
        int[] diceValues = null;
        int diceCounter = 0;
        try {
            diceValues = gameLogic.throwDice();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Check if diceValues array is initialized or number of dices is not correct
        if (diceValues == null || diceValues.length != 2) return;

        // Update dice images and log on board
        dice1_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[0] + ".png")));
        dice2_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[1] + ".png")));
        String s = USERNAME + " threw dice:  " + diceValues[0] + ",  " + diceValues[1];

        // Animation constants
        final int DURATION = 600;
        final int ROTATE_ANGLE = 1080;

        RotateTransition rt1 = new RotateTransition(Duration.millis(DURATION), dice1_img);
        RotateTransition rt2 = new RotateTransition(Duration.millis(DURATION), dice2_img);
        rt1.setByAngle(ROTATE_ANGLE);
        rt2.setByAngle(-ROTATE_ANGLE);

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(DURATION), dice1_img);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(DURATION), dice2_img);
        tt1.setFromY(dice1_img.getY() + 100);
        tt1.setFromX(dice1_img.getX() + 200);
        tt1.setToY(dice1_img.getY());
        tt1.setToX(dice1_img.getX());

        tt2.setFromY(dice2_img.getY() + 100);
        tt2.setFromX(dice1_img.getX() - 200);
        tt2.setToY(dice2_img.getY());
        tt2.setToX(dice1_img.getX());

        ParallelTransition pt = new ParallelTransition(rt1, rt2, tt1, tt2);
        pt.play();

        int[] finalDiceValues = diceValues;
        addToEventlog(s);

            // Update board view to show where player moved
        updateBoard();

        // If the player didn't throw two equal dices, disable the dice button. If not, the player can throw dice again
        if (finalDiceValues[0] != finalDiceValues[1]) {
            rolldiceBtn.setDisable(true);
            endturnBtn.setDisable(false);
        } else {
            if (diceCounter == 2) {
                try {
                    gameLogic.setPlayerInJail(USERNAME, true);
                    payBailBtn.setDisable(true);
                    payBailBtn.setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                MessagePopupController.show("The dices are equal, throw again!");
                diceCounter++;
            }
        }

        // Check the tile you are currently on and call that event
        callTileEvent();

        // Update board view again
        updateBoard();
    }

    /**
     * Ends your current turn
     */
    public void endTurn() {
        MessagePopupController.show("Test msg");
        // Stop and reset timer

        try {
            // Disable buttons
            endturnBtn.setDisable(true);
            rolldiceBtn.setDisable(true);
            buyPropertyBtn.setDisable(true);
            payBailBtn.setDisable(true);

            // Finish turn in gamelogic and wait for your next turn
            gameLogic.finishYourTurn();
            updateBoard();
            waitForTurn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see what form of tile you are on and call the event accordingly
     */
    private void callTileEvent() {
        // Store your player's position
        int yourPosition = gameLogic.getPlayer(USERNAME).getPosition();

        // PROPERTY TILE HANDLING
        if(gameLogic.getBoard().getTileType(yourPosition) == Board.PROPERTY) {
            cardContainer.getChildren().clear();
            // Draw property card with
            Pane card = GameControllerDrawFx.createPropertyCard(gameLogic.getEntityManager().getPropertyAtPosition(gameLogic.getPlayer(USERNAME).getPosition()));
            cardContainer.getChildren().add(card);

            // Get owner of property and set the button or label accordingly
            String propertyOwner = gameLogic.getEntityManager().getOwnerAtProperty(yourPosition);
            if (propertyOwner == null || propertyOwner.equals("")) {
                // If property is available, show button
                buyPropertyBtn.setDisable(false);
                buyPropertyBtn.setVisible(true);
                payRentBtn.setDisable(true);
                payRentBtn.setVisible(false);
                propertyOwned.setVisible(false);
            } else {
                // If this is your property, just display a label informing so
                if (propertyOwner.equals(USERNAME)) {
                    buyPropertyBtn.setDisable(true);
                    buyPropertyBtn.setVisible(false);
                    payRentBtn.setDisable(true);
                    payRentBtn.setVisible(false);
                    propertyOwned.setVisible(true);
                    propertyOwned.setText("Owned by you");
                } else {
                    // If this is someone else's property, activate the pay rent button
                    propertyOwned.setVisible(false);
                    payRentBtn.setDisable(false);
                    payRentBtn.setVisible(true);
                    endturnBtn.setDisable(true);
                    rolldiceBtn.setDisable(true);
                }
            }
        } else {
            // If no property here, make sure to clear the property
            cardContainer.getChildren().clear();
            propertyOwned.setVisible(false);
            buyPropertyBtn.setVisible(false);
            payRentBtn.setVisible(false);
        }

        // If on free parking, get a free-parking token
        if (gameLogic.getPlayer(USERNAME).getPosition() == gameLogic.getBoard().getFreeParkingPosition()) {
            gameLogic.getPlayer(USERNAME).setFreeParking(true);
        }

        // If go-to jail, go to jail!
        if (gameLogic.getPlayer(USERNAME).getPosition() == gameLogic.getBoard().getGoToJailPosition()) {
            try {
                gameLogic.setPlayerInJail(USERNAME, true);
                MessagePopupController.show("Criminal scumbag! You are going to jail. Your mother is not proud...");
                payBailBtn.setVisible(true);
                payBailBtn.setDisable(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates all the scene's graphics to reflect the changes in the database
     */
    public void updateBoard() {
        String[] turns = gameLogic.getTurns();
        String[] colors = new String[turns.length];
        int[] positions = null;

        try {
            positions = gameLogic.getPlayerPositions();

            // Set player colors
            for (int i = 0; i < turns.length; i++) {
                colors[i] = getPlayerColor(turns[i]);
            }

            GameControllerDrawFx.createPlayerPieces(gamegrid, positions, colors);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        roundValue.setText(String.valueOf(gameLogic.getRoundNumber() + 1));
        // Updated in updatePlayerInfo()?
        //userMoney.setText(String.valueOf(gameLogic.getPlayer(USERNAME).getMoney()));
        statusValue.setText("Waiting for " + gameLogic.getCurrentPlayer() + " to finish their turn");

        if (positions != null)
            //addToEventlog(gameLogic.getCurrentPlayer() + " moved to " + gameLogic.getEntityManager().getPropertyAtPosition(positions[gameLogic.getTurnNumber()]).getName());

        updatePlayersInfo();
    }

    /**
     * This method runs at the start of each new turn, regardless if it's your turn or not
     * A couple things needs to be updated at the start of each turn
     *
     * @param yourTurn Is it your turn?
     */
    public void newTurn(boolean yourTurn) {
        try {
            // Increment to a new turn in the gamelogic object
            gameLogic.newTurn(yourTurn);

            // Update the playing board accordingly to database updates
            updateBoard();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If this is your turn, stop the database check databaseTimer and enable the button to roll dice
        if (yourTurn) {
            payBailBtn.setDisable(false);
            databaseTimer.cancel();
            databaseTimer.purge();
            rolldiceBtn.setDisable(false);
        }
    }

    private void addToEventlog(String msg) {
        eventList.add(new Text(msg));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }

    /**
     * Helper method for setting onclick in propertyIcon
     * @param container for the propertyIcon
     * @param username target user
     */
    private void setPropertyOnClick(Pane container, String username) {
        container.setOnMouseClicked(e -> {
            showProperties(username);
        });
    }

    /**
     * This method will update players info in the sidebar.
     * It will render the GUI in the opponentsContainer.
     */
    private void updatePlayersInfo(){
        opponentsContainer.getChildren().clear();
        ArrayList<Player> players = Handler.getPlayerDAO().getPlayersInGame(Handler.getCurrentGameId());
        String color;
        ImageView img;

        // Go through all the players, update info and render GUI
        for (Player player : players) {
            // Find color associated with user
            color = getPlayerColor(player.getUsername());
            if (color == null) color = "red"; // Check if color has been assigned

            // Check if player has left, is in jail or bankrupt and change img accordingly
            if (player.getActive() == 0) img = new ImageView("file:res/gui/Game/exited.png");
            else if (player.isInJail()) img = new ImageView("file:res/gui/Game/jail.png");
            else if (player.isBankrupt()) img = new ImageView("file:res/gui/Game/bankrupt.png");
            else img = null;

            // Convert and format money value to player
            String playerMoney = FxUtils.thousandDecimalFormat(String.valueOf(player.getMoney()));

            // Player is the actual user
            if (player.getUsername().equals(Handler.getAccount().getUsername())) {
                username.setText(player.getUsername());
                userMoney.setText(playerMoney);
                userColor.setStyle("-fx-background-color: " + color + ";");

                // Check if amount of money is changed
                if (current_money != player.getMoney()) {
                    MessagePopupController.show("Money transaction: " + (player.getMoney() - current_money));
                }
                current_money = player.getMoney();

                // Show your own properties on click
                setPropertyOnClick(userPropertiesIcon, player.getUsername());

                // Set img if it is assigned
                //userColor.getChildren().clear(); // Reset

                if (img != null) {
                    img.setFitHeight(userColor.getHeight());
                    img.setFitWidth(userColor.getWidth());
                    userColor.getChildren().add(img);
                } else {
                    // Remove image
                    userColor.getChildren().clear();
                }
            }

            // Player is an opponent
            else {
                // Render opponentRow in opponentsContainer and save the propertyIcon that is returned
                Pane imgContainer = GameControllerDrawFx.createOpponentRow(
                        player.getUsername(),
                        color,
                        String.valueOf(playerMoney),
                        img,
                        opponentsContainer
                );

                // Show properties to the user when the icon container is clicked
                setPropertyOnClick(imgContainer, player.getUsername());
            }
        }
    }

    /**
     * Go through a color list (located in Handler) and find the color associated with a player.
     * @param username Target user
     * @return Color associated with user
     */
    private String getPlayerColor(String username) {
        // Go through the arraylist located in Handler
        for (String[] player : Handler.getColorList()) {
            // Check if username is target username and return color associated with it if it is an match
            if (player[0].equals(username)) {
                return player[1];
            }
        }
        return null;
    }

    /**
     * Attempts to pay the player with the current owned property with the proper rent
     */
    public void rentTransaction() {
        // Check if your player has a free parking token
        if (gameLogic.getPlayer(USERNAME).hasFreeParking()) {
            MessagePopupController.show("You have a 'Free Parking' token! You don't have to pay rent here");
            gameLogic.getPlayer(USERNAME).setFreeParking(false);
        } else {
            try {
                gameLogic.rentTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Property property = gameLogic.getEntityManager().getPropertyAtPosition(gameLogic.getEntityManager().getYou().getPosition());

            // REMEMBER TO CHANGE INDEX (END OF THIS HUUUUGE LINE) TO ACTUAL RENT WHEN HOUSE AND HOTEL IS IMPLEMENTED
            MessagePopupController.show(
                    "You have paid " +
                    property.getAllRent()[0] +
                    " in rent to " + property.getOwner()
            );
        }
        payRentBtn.setDisable(true);
        int[] currentDice = gameLogic.getCurrentDice();
        if (currentDice[0] == currentDice[1] && !gameLogic.getEntityManager().getPlayer(USERNAME).isInJail()) {
            rolldiceBtn.setDisable(false);
            endturnBtn.setDisable(true);
        } else {
            endturnBtn.setDisable(false);
        }

    }

    /**
     * Lets the player choose to purchase a property
     */
    public void buyProperty() {
        Alert buyprompt = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to purchase this property?",
                ButtonType.YES, ButtonType.NO);
        buyprompt.showAndWait();

        if (buyprompt.getResult() == ButtonType.YES) {
            // Perform the transaction of property through gamelogic
            try {
                if (!gameLogic.propertyTransaction()) {
                    Alert messageBox = new Alert(Alert.AlertType.INFORMATION, "You do not have enough funds to purchase this property.");
                    messageBox.showAndWait();
                } else {
                    MessagePopupController.show("Purchase successful, you are now the owner of " + gameLogic.getEntityManager().getPropertyAtPosition(gameLogic.getEntityManager().getYou().getPosition()).getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            buyprompt.close();

            // Update board
            updateBoard();

            // Update property information label
            buyPropertyBtn.setVisible(false);
            propertyOwned.setVisible(true);
            propertyOwned.setText("Owned by you");
        }
        if (buyprompt.getResult() == ButtonType.NO) {
            buyprompt.close();
        }
    }

    public void payBail() {
        if (gameLogic.payBail()) {
            try {
                gameLogic.setPlayerInJail(USERNAME, false);
                payBailBtn.setVisible(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}