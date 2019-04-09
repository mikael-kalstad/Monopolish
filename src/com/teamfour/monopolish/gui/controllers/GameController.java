package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.game.*;
import com.teamfour.monopolish.game.chanceCards.ChanceCard;
import com.teamfour.monopolish.game.chanceCards.ChanceCardData;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.*;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
 * @version 1.10
 */

public class GameController {
    // Timer for checking database updates and requests
    private static Timer databaseTimer = new Timer();
    private static Timer requestTimer = new Timer();

    // GameLogic for handling more intricate game operations
    private Game game;
    private final int GAME_ID = Handler.getCurrentGameId();
    private final String USERNAME = Handler.getAccount().getUsername();
    private int current_money = 0;
    private boolean firstTurn = true;

    // Background overlays
    @FXML private Pane backgroundOverlay;
    @FXML private Pane helpOverlay;
    @FXML private Pane propertiesDialogOverlay;

    // Elements in board
    @FXML private AnchorPane cardContainer;
    @FXML private GridPane gamegrid;
    @FXML private Button propertyBtn;
    @FXML private Text propertyOwnerMsg;
    @FXML private Text propertyMsg;
    @FXML ImageView dice1_img, dice2_img;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, endturnBtn, giveUpBtn;
    @FXML private Label roundValue, statusValue;
    @FXML private Label username, userMoney;
    @FXML private Pane userColor, userPropertiesIcon;
    @FXML private Pane opponentsContainer;

    // Container for chat element
    @FXML private Pane chatContainer;
    @FXML public Pane forfeitContainer;
    public static boolean forfeit = false;

    // Properties dialog
    @FXML private Pane propertiesContainer, buyHouseContainer;
    @FXML private FlowPane propertiesContentContainer;
    @FXML private Text propertiesUsername;
    @FXML private Button tradeBtn;
    @FXML private Text tradeMsg;

    // Container for trade
    @FXML public Pane tradeContainer;

    // Message popup
    @FXML private Pane messagePopupContainer;

    // Settings
    @FXML private Pane settingsContent;
    @FXML private ImageView notificationToggle;
    @FXML private ImageView soundToggle;
    private boolean showSettings = false;
    private boolean showNotifications = true;
    private static boolean playSounds = true;

    // Container for houses
    @FXML private GridPane housegrid;

    // Free parking card container
    @FXML private Pane freeParkingCard;

    // Winner elements
    @FXML private Pane winnerContainer;
    @FXML private Text winnerMsg;
    @FXML private Button winnerBtn;
    static boolean gameFinished = false;

    /**
     * Launches when the scene is loaded.
     */
    @FXML public void initialize() {
        // Reference that is used in other controllers
        Handler.setForfeitContainer(forfeitContainer);
        Handler.setTradeContainer(tradeContainer);
        Handler.setBuyHouseContainer(buyHouseContainer);

        // Set gamelogic object in handler
        game = new Game(GAME_ID);

        // Load gamelogic and initialize the game setup
        GameLogic.startGame();

        // Setup messagePop
        MessagePopupController.setup(messagePopupContainer, 5);

        // Setup chat
        addElementToContainer(ViewConstants.CHAT.getValue(), chatContainer);

        // Check for forfeit regularly
        startRequestTimer();

        // Update the board
        updateBoard();

        // Start the game!
        waitForTurn();

        // Update player controls
        updateClientControls();

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
                GameLogic.onPlayerLeave();
                stopTimers();

                // Logout user
                Handler.getAccountDAO().setInactive(USERNAME);

                // Close connection
                if (ConnectionPool.getMainConnectionPool() != null) {
                    try {
                        ConnectionPool.getMainConnectionPool().shutdown();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

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
            GameLogic.onPlayerLeave();
            stopTimers();

            // Change view to dashboard
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        }
    }

    /**
     * Stop all timers in class
     */
    static void stopTimers() {
        databaseTimer.cancel();
        databaseTimer.purge();
        ChatController.getChatTimer().cancel();
        ChatController.getChatTimer().purge();
        requestTimer.cancel();
        requestTimer.purge();
    }

    /**
     * Will run when the forfeit button is clicked.
     * A forfeit dialog will appear on the screen
     */
    public void forfeit() {
        forfeit = true;
        Handler.getGameDAO().setForfeit(GAME_ID, true);

        // Load forfeit GUI
        addElementToContainer(ViewConstants.FORFEIT.getValue(), forfeitContainer);

        // Show background overlay
        backgroundOverlay.setVisible(true);

        // User should not be able to close dialog onclick background
        backgroundOverlay.setOnMouseClicked(e -> {});

        // Hide properties dialog and show forfeit dialog
        propertiesContainer.setVisible(false);
        forfeitContainer.setVisible(true);
    }

    /**
     * Start a timer that will check for different request. <br/>
     * <b>What the timer checks</b>
     * <ul>
     *     <li>1. Forfeit request in database</li>
     *     <li>2. Trade request in database</li>
     *     <li>3. Winner of the game if game is finished</li>
     * </ul>
     */
    private void startRequestTimer() {
        // Setup forfeit task
        TimerTask requestTask = new TimerTask() {
            @Override
            public void run() {
                // 1. Check for forfeit
                boolean gameForfeit = Handler.getGameDAO().getForfeit(GAME_ID);

                if (!forfeit && gameForfeit) {
                    Platform.runLater(() -> forfeit());
                } else if (forfeit && !gameForfeit) {
                    backgroundOverlay.setVisible(false);
                }

                // Check if forfeit checks should be reset
                if (Handler.getPlayerDAO().getForfeitCheck(GAME_ID)) {
                    // Reset all player forfeit votes and checks
                    for (String u : Handler.getCurrentGame().getPlayers()) {
                        Handler.getPlayerDAO().setForfeitStatus(u, GAME_ID, 0);
                        Handler.getPlayerDAO().setForfeitCheck(GAME_ID, u, false);
                        forfeit = false;
                    }
                }

                // 2. Check for trade request
                if (Handler.getPlayerDAO().isTrade(USERNAME)) {
                    addElementToContainer(ViewConstants.SHOW_TRADE.getValue(), tradeContainer);
                    //Platform.runLater(() -> addElementToContainer(ViewConstants.SHOW_TRADE.getValue(), tradeContainer));
                }

                // 3. Check if there is any winner
                String winner = game.getEntities().findWinner();
                if (winner != null) {
                    stopTimers();
                    Platform.runLater(() -> announceWinner(winner));
                }

//                if (gameFinished) {
//                    String winner = game.getEntities().findWinner();
//                    Platform.runLater(() -> announceWinner(winner));
//                }
            }
        };

        // Check for forfeit every second
        requestTimer.scheduleAtFixedRate(requestTask, 0L, 1000L);
    }

    /**
     * Will open a winner dialog displaying the name of the winner,
     * and includes a button that will go back to the dashboard.
     * @param winner Name of the player that won the game
     */
    private void announceWinner(String winner) {
        // Set fixed background overlay
        backgroundOverlay.setVisible(true);
        backgroundOverlay.setOnMouseClicked(e -> {});

        // Show winner container and set msg
        winnerMsg.setText(winner + " has won the game");
        winnerContainer.setVisible(true);

        // leave the game and change to dashboard on click
        winnerBtn.setOnMouseClicked(e -> {
            GameLogic.onPlayerLeave();
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        });
    }

    /**
     * Load element from .fxml file and add to container
     *
     * @param filename  Target .fxml file
     * @param container Target container
     */
    private void addElementToContainer(String filename, Pane container) {
        try {
            Node element = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + filename));
            container.getChildren().clear(); // Reset container
            container.getChildren().add(element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggle showing settings box (show/hide).
     */
    public void toggleSettings() {
        if (showSettings) {
            showSettings = false;
            settingsContent.setVisible(false);
        } else {
            showSettings = true;
            settingsContent.setVisible(true);
        }
    }

    /**
     * Toggles all notifications in the game, on or off
     */
    public void toggleNotification() {
        // Turn of notifications and set image and text
        if (showNotifications) {
            notificationToggle.setImage(new Image("file:res/gui/Game/toggleOff.png"));
            showNotifications = false;
            messagePopupContainer.setVisible(false);
        } else {
            notificationToggle.setImage(new Image("file:res/gui/Game/toggleOn.png"));
            showNotifications = true;
            messagePopupContainer.setVisible(true);
        }
    }

    /**
     * Toggle sounds on or off
     */
    public void toggleSounds() {
        if (playSounds) {
            playSounds = false;
            soundToggle.setImage(new Image("file:res/gui/Game/toggleOff.png"));
        }
        else {
            playSounds = true;
            soundToggle.setImage(new Image("file:res/gui/Game/toggleOn.png"));
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
     * Show a popup dialog with all the properties belonging to a player
     *
     * @param username Target user
     */
    private void showProperties(String username) {
        // Show backgroundOverlay
        backgroundOverlay.setVisible(true);

        // Clear all properties that already may exists inside the dialog
        propertiesContentContainer.getChildren().clear();

        // Show properties dialog and set username
        propertiesContainer.setVisible(true);
        propertiesUsername.setText(username);

        // Add all properties that the user owns to the dialog
        for (Property p : game.getEntities().getPlayer(username).getProperties()) {
            Pane card = GameControllerDrawFx.createPropertyCard(p);
            propertiesContentContainer.getChildren().add(card);

            if (username.equals(USERNAME)) {
                card.setOnMouseClicked(event -> {
                    buyHouseContainer.setVisible(true);

                    // Set background overlay
                    propertiesDialogOverlay.setVisible(true);

                    // Hide onclick
                    propertiesDialogOverlay.setOnMouseClicked(e -> {
                        propertiesDialogOverlay.setVisible(false);
                        buyHouseContainer.setVisible(false);
                    });

                    Handler.setBuyHouseProperty(p);
                    addElementToContainer(ViewConstants.BUY_HOUSE.getValue(), buyHouseContainer);
                });
            }

            FxUtils.setScaleOnHover(card, 0.1);
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
                    // If it's your turn, break out of the databaseTimer
                    if (GameLogic.waitForTurn() || firstTurn) {
                        if (game.getPlayers()[game.getCurrentTurn()].equals(USERNAME)) {
                            firstTurn = false;
                            yourTurn();
                        }
                    }
                    firstTurn = false;
                    updateBoard();
                });
            }
        }, 0L, 1000L);
    }

    /**
     * Will roll two dices and get random values between 1-6.
     * This method will also update corresponding dice images in the GUI.
     */
    public void rollDice() {
        GameLogic.rollDice();
        int[] diceValues = game.getDice().getLastThrow();

        // Check if diceValues array is initialized or number of dices is not correct
        if (diceValues == null || diceValues.length != 2) return;

        // Update dice images and log on board
        dice1_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[0] + ".png")));
        dice2_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[1] + ".png")));

        // Animation constants
        final int DURATION = 600;
        final int ROTATE_ANGLE = 1080;

        // Animate dice rotation
        RotateTransition rt1 = new RotateTransition(Duration.millis(DURATION), dice1_img);
        RotateTransition rt2 = new RotateTransition(Duration.millis(DURATION), dice2_img);
        rt1.setByAngle(ROTATE_ANGLE);
        rt2.setByAngle(-ROTATE_ANGLE);

        // Animate dice movement
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

        // Run rotate- and translate (movement) animation in parallel
        ParallelTransition pt = new ParallelTransition(rt1, rt2, tt1, tt2);
        pt.play();

        // Update board view to show where player moved
        updateBoard();

        // Check the tile you are currently on and call that event
        updateClientControls();

        // Update board view again
        updateBoard();
    }

    /**
     * Ends your current turn
     */
    public void endTurn() {
        // Disable buttons
        endturnBtn.setDisable(true);
        rolldiceBtn.setDisable(true);
        propertyBtn.setDisable(true);

        // Finish turn in gameLogic and wait for your next turn
        GameLogic.endTurn();
        updateBoard();
        waitForTurn();
    }

    /**
     * Checks to see what form of tile you are on and enable / disable player controls accordingly
     */
    private void updateClientControls() {
        // Remove previous cards
        cardContainer.getChildren().clear();

        // Get dice values
        int[] diceValues = game.getDice().getLastThrow();

        // If the player didn't throw two equal dices, disable the dice button. If not, the player can throw dice again
        if (diceValues[0] != diceValues[1]) {
            rolldiceBtn.setDisable(true);
            endturnBtn.setDisable(false);
        }

        // Get your position
        int yourPosition = game.getEntities().getYou().getPosition();

        int tileType = Handler.getCurrentGame().getBoard().getTileType(yourPosition);
        Pane card = null;

        // No buttons or msg under property card is shown by default
        propertyBtn.setVisible(false);
        propertyOwnerMsg.setVisible(false);
        propertyMsg.setVisible(false);

        // Check tile type and change controls and GUI accordingly, each tile has its own card that will be displayed
        switch (tileType) {
            case Board.PROPERTY:
                // Get property card
                Property currentProperty = game.getEntities().getPropertyAtPosition(yourPosition);
                card = GameControllerDrawFx.createPropertyCard(currentProperty);

                // Check if property has an owner
                String propertyOwner = game.getEntities().getOwnerAtProperty(yourPosition);

                // No owner, buying is optional
                if (propertyOwner == null || propertyOwner.equals("")) {
                    FxUtils.showAndChangeBtn(propertyBtn, "Buy property", "#79b76e");
                    propertyBtn.setOnMouseClicked(e -> buyProperty());
                }

                // Owned by user, no action
                else if (propertyOwner.equals(USERNAME)) FxUtils.showAndChangeText(propertyOwnerMsg, "Property owned by you");

                // Owned by other player, rent required if not pawned
                else {
                    if (!currentProperty.isPawned()) {
                        FxUtils.showAndChangeText(propertyOwnerMsg, "Property owned by " + propertyOwner);
                        FxUtils.showAndChangeText(propertyMsg, "You must pay rent before continuing");
                        FxUtils.showAndChangeBtn(propertyBtn, "Pay rent", "#ef5350");
                        disableControls();

                        propertyBtn.setOnMouseClicked(e -> rentTransaction());
                    } else {
                        // If property is pawned, you don't have to pay
                        FxUtils.showAndChangeText(propertyOwnerMsg, "Property pawned by " + propertyOwner);
                    }
                }
                break;

            case Board.START:
                card = GameControllerDrawFx.createSpecialCard("Start", "file:res/gui/SpecialCard/start.png", "You will get $4000 if you land or go past start", "#e2885a");
                break;

            case Board.COMMUNITY_TAX:
                card = GameControllerDrawFx.createSpecialCard("Income tax", "file:res/gui/SpecialCard/tax.png", "$4000",  "#cc6c6c");
                disableControls();

                FxUtils.showAndChangeBtn(propertyBtn, "Pay tax", "#ef5350");
                FxUtils.showAndChangeText(propertyMsg, "Tax must be payed before continuing");
                propertyBtn.setOnMouseClicked(e -> payTax());
                break;

            case Board.FREE_PARKING:
                card = GameControllerDrawFx.createSpecialCard("Free parking", "file:res/gui/SpecialCard/freeParking.png", "The next owned property you land on will be rent free", "#555");

                // Show free parking token and update players object
                game.getEntities().getYou().setFreeParking(true);
                freeParkingCard.setVisible(true);
                break;

            case Board.JAIL:
                // check if player is in jail or just visiting
                String info = "Relax, just visiting";

                if (game.getEntities().getYou().isInJail()) {
                    FxUtils.showAndChangeBtn(propertyBtn, "Pay bail", "#e2742b");
                    info = "To get out of jail throw equal dices, or pay $1000 in bail";
                }

                card = GameControllerDrawFx.createSpecialCard("Jail", "file:res/gui/SpecialCard/prisonDoor.png", info, "#444");

                // Pay bail is only available on your turn
                if (game.getPlayers()[game.getCurrentTurn()].equals(USERNAME))
                    propertyBtn.setOnMouseClicked(e -> payBail());
                else
                    propertyBtn.setVisible(true);
                break;

            case Board.CHANCE:
                ChanceCard chanceCard = ChanceCardData.getRandomChanceCard();
                ChanceCardController.display(chanceCard, cardContainer);
                break;
        }

        // Show card in container if defined
        if (card != null)
            cardContainer.getChildren().add(card);

        if (game.getEntities().getYou().isBankrupt())
            endTurn();
    }

    private void disableControls() {
        rolldiceBtn.setDisable(true);
        endturnBtn.setDisable(true);
    }

    /**
     * Updates all the scene's graphics to reflect the changes in the database
     */
    private void updateBoard() {
        String[] turns = game.getPlayers();
        String[] colors = new String[turns.length];
        int[] positions = null;

        // Get player positions and update GUI on the board
        try {
            positions = game.getEntities().getPlayerPositions();

            // Set player colors
            for (int i = 0; i < turns.length; i++) {
                colors[i] = Handler.getPlayerColor(turns[i]);
            }

            // Draw player pieces on the board
            GameControllerDrawFx.createPlayerPieces(gamegrid, positions, colors);

            for (String player : turns){
                if (game.getEntities().getPlayer(player) == null) continue;

                for (Property property : game.getEntities().getPlayer(player).getProperties()){
                    if (property instanceof Street) {
                        GameControllerDrawFx.drawHouse(housegrid, (Street) property);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update round- and status-value
        roundValue.setText(String.valueOf(game.getRoundNumber() + 1));

        if (game.getPlayers()[game.getCurrentTurn()].equals(USERNAME))
            statusValue.setText("It is your turn, make a move!");
        else
            statusValue.setText("Waiting for " + game.getPlayers()[game.getCurrentTurn()] + " to finish");

        // Update info for all players
        if (positions != null) updatePlayersInfo();
    }

    /**
     * This method runs at the start of each new turn, regardless if it's your turn or not
     * A couple things needs to be updated at the start of each turn
     */
    private void yourTurn() {
        // If you're bankrupt, end your turn right away since you're not really allowed to play
        if (game.getEntities().getYou().isBankrupt()) {
            endTurn();
            return;
        }

        // Play a sound to indicate that it is your turn
        if (playSounds) Handler.playSound("res/sounds/pling.mp3");

        // Set buttons state
        rolldiceBtn.setDisable(false);

        if (game.getEntities().getYou().isInJail())
            propertyBtn.setDisable(false);

        // Stop timer temporarily
        databaseTimer.cancel();
        databaseTimer.purge();
    }

    /**
     * Helper method for setting onclick in propertyIcon
     *
     * @param container for the propertyIcon
     * @param username  target user
     */
    private void setPropertyOnClick(Pane container, String username) {
        container.setOnMouseClicked(e -> showProperties(username));
    }

    /**
     * This method will update players info in the sidebar.
     * It will render the GUI in the opponentsContainer.
     */
    private void updatePlayersInfo() {
        // Clear opponents before re-rendering
        opponentsContainer.getChildren().clear();

        ArrayList<Player> players = Handler.getPlayerDAO().getPlayersInGame(Handler.getCurrentGameId());
        String color;
        ImageView img;

        // Go through all the players, update info and render GUI
        for (Player player : players) {

            // Find color associated with user
            color = Handler.getPlayerColor(player.getUsername());
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
                    String msg = "Money transaction: " + (player.getMoney() - current_money);

                    // Display message popup with money transaction
                    if (player.getMoney() - current_money > 0)
                        MessagePopupController.show(msg, "dollarPositive.png", "Bank");
                    else
                        MessagePopupController.show(msg, "dollarNegative.png", "Bank");

                    // Play a sound to indicate that it is your turn
                   if (playSounds) Handler.playSound("res/sounds/coin.wav");
                }
                // Update for next check
                current_money = player.getMoney();

                // Show your own properties on click
                setPropertyOnClick(userPropertiesIcon, player.getUsername());

                // Set img if it is assigned
                if (img == null) {
                    userColor.getChildren().clear(); // Reset
                } else {
                    img.setFitHeight(userColor.getHeight());
                    img.setFitWidth(userColor.getWidth());
                    userColor.getChildren().add(img);
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
     * Lets the player choose to purchase a property
     */
    private void buyProperty() {
        Alert buyprompt = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to purchase this property?",
                ButtonType.YES, ButtonType.NO);
        buyprompt.showAndWait();

        if (buyprompt.getResult() == ButtonType.YES) {
            // Perform the transaction of property through gameLogic
            if (GameLogic.purchaseProperty()) {
                // Update board
                updateBoard();

                // Update property information label
                FxUtils.showAndChangeText(propertyOwnerMsg, "Property owned by you");
                propertyBtn.setDisable(true);
            } else {
                Alert messageBox = new Alert(Alert.AlertType.INFORMATION,
                        "You do not have enough funds to purchase this property.");
                messageBox.showAndWait();
            }
            buyprompt.close();
        }
        if (buyprompt.getResult() == ButtonType.NO) {
            buyprompt.close();
        }
    }

    /**
     * Pay bail to try and get out of jail
     */
    public void payBail() {
        if (GameLogic.payBail()) {
            propertyBtn.setDisable(true);
        } else {
            Alert messageBox = new Alert(Alert.AlertType.INFORMATION,
                    "You do not have enough funds to pay bail.");
            messageBox.showAndWait();
        }
    }

    /**
     * Pay income tax
     */
    private void payTax() {
        GameLogic.payTax();
        updateBoard();
        checkDiceThrow();
    }

    /**
     * Attempts to pay the player with the current owned property with the proper rent
     */
    private void rentTransaction() {
        if(!GameLogic.payRent()) {
            Alert messageBox = new Alert(Alert.AlertType.INFORMATION,
                    "You do not have enough funds to pay rent.");
        } else {
            updateBoard();
            checkDiceThrow();
            freeParkingCard.setVisible(false);
        }
    }

    private void checkDiceThrow() {
        propertyBtn.setDisable(true);
        int[] currentDice = game.getDice().getLastThrow();
        if (currentDice[0] == currentDice[1] && !game.getEntities().getYou().isInJail()) {
            rolldiceBtn.setDisable(false);
            endturnBtn.setDisable(true);
        } else {
            endturnBtn.setDisable(false);
        }
    }
}