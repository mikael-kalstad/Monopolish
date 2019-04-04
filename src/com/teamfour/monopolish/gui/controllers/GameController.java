package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.Board;
import com.teamfour.monopolish.game.Game;
import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.chanceCards.ChanceCard;
import com.teamfour.monopolish.game.chanceCards.ChanceCardData;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.Street;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
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
    // Timer for checking database updates and forfeit
    private Timer databaseTimer = new Timer();
    private Timer forfeitTimer = new Timer();

    // GameLogic for handling more intricate game operations
    private Game game;
    private int current_money = 0;
    private final String USERNAME = Handler.getAccount().getUsername();
    private boolean firstTurn = true;

    // Background overlays
    @FXML private Pane backgroundOverlay;
    @FXML private Pane helpOverlay;

    // Elements in board
    @FXML private AnchorPane cardContainer;
    @FXML private Button buyPropertyBtn, payRentBtn;
    @FXML private Label propertyOwned;
    @FXML private GridPane gamegrid;

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
    @FXML public Pane forfeitContainer;
    public static boolean forfeit = false;

    // Properties dialog
    @FXML private Pane propertiesContainer;
    @FXML private FlowPane propertiesContentContainer;
    @FXML private Text propertiesUsername;
    @FXML private Button tradeBtn;
    @FXML private Text tradeMsg;

    // Container for trade
    @FXML private Pane tradeContainer;

    // Message popup
    @FXML private Pane messagePopupContainer;

    // Notification toggle
    @FXML private ImageView notificationLogo;
    @FXML private Text notificationText;
    private String MSG_NOTIFICATION_ON = "Turn off notifications";
    private String MSG_NOTIFICATION_OFF = "Turn on notifications";
    private boolean showNotifications = true;

    // Container for houses
    @FXML private GridPane housegrid;

    // Free parking card container
    @FXML private Pane freeParkingCard;

    /**
     * Launches when the scene is loaded.
     */
    @FXML
    public void initialize() {
        // Reference that is used in other controllers
        Handler.setForfeitContainer(forfeitContainer);
        Handler.setTradeContainer(tradeContainer);

        // Set gamelogic object in handler
        game = new Game(Handler.getCurrentGameId());

        // Load gamelogic and initialize the game setup
        GameLogic.startGame();

        // Setup messagePop
        MessagePopupController.setup(messagePopupContainer, 5);

        // Setup chat
        addElementToContainer(ViewConstants.CHAT.getValue(), chatContainer);

        // Update the board
        updateBoard();

        // Start the game!
        waitForTurn();

        // Check periodically if someone initiated a forfeit
        TimerTask forfeitTask = new TimerTask() {
            @Override
            public void run() {
                // Get votes from database
                int[] votes = Handler.getPlayerDAO().getForfeitStatus(Handler.getCurrentGameId());

                // Show forfeit dialog if forfeit is initiated
                if (votes[0] != 0 || votes[1] != 0 && !forfeit) {
                    Platform.runLater(() -> forfeit());
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
                endGameForPlayer();

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
            endGameForPlayer();

            // Change view to dashboard
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        }
    }

    /**
     * This method will be run before leaving or quitting the game.
     * It will sort out all DB queries and stop all timers required.
     */
    public void endGameForPlayer() {
        // Remove player from lobby
        Handler.getAccountDAO().setInactive(USERNAME);
        Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
        game.getEntities().removePlayer(USERNAME);

        // Stop timers
        databaseTimer.cancel();
        databaseTimer.purge();
        ChatController.getChatTimer().cancel();
        ChatController.getChatTimer().purge();
        forfeitTimer.cancel();
        forfeitTimer.purge();
    }

    /**
     * Will run when the forfeit button is clicked.
     * A forfeit dialog will appear on the screen
     */
    public void forfeit() {
        forfeit = true;
        if (forfeitContainer == null) System.out.println("Forfeit container is null!");
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
     * @param filename  Target .fxml file
     * @param container Target container
     */
    private void addElementToContainer(String filename, Pane container) {
        try {
            Node element = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + filename));
            container.getChildren().add(element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles all notifications in the game, on or off
     */
    public void toggleNotification() {
        // Turn of notifications and set image and text
        if (showNotifications) {
            notificationLogo.setImage(new Image("file:res/gui/Game/toggleOff.png"));
            notificationText.setText(MSG_NOTIFICATION_OFF);
            showNotifications = false;
            messagePopupContainer.setVisible(false);
        } else {
            notificationLogo.setImage(new Image("file:res/gui/Game/toggleOn.png"));
            notificationText.setText(MSG_NOTIFICATION_ON);
            showNotifications = true;
            messagePopupContainer.setVisible(true);
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
    public void showProperties(String username) {
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
                    // If it's your turn, break out of the databaseTimer
                    if (GameLogic.waitForTurn() || firstTurn) {
                        if (game.getPlayers()[game.getCurrentTurn()].equals(USERNAME)) {
                            firstTurn = false;
                            // TODO: Your turn, break out
                            yourTurn();
                        }
                    }
                    firstTurn = false;
                    updateBoard();
                });
            }
        }, 0l, 1000l);
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
        buyPropertyBtn.setDisable(true);
        payBailBtn.setDisable(true);

        // Finish turn in gamelogic and wait for your next turn
        GameLogic.endTurn();
        updateBoard();
        waitForTurn();
    }

    /**
     * Checks to see what form of tile you are on and call the event accordingly
     */
    private void updateClientControls() {
        // Remove previous cards
        cardContainer.getChildren().clear();

        // Get dice values
        int[] diceValues = game.getDice().getLastThrow();

        // If the player didn't throw two equal dices, disable the dice button. If not, the player can throw dice again
        if (diceValues[0] != diceValues[1]) {
            rolldiceBtn.setDisable(true);
            payBailBtn.setDisable(true);
            endturnBtn.setDisable(false);
        }

        // If player is in jail, show button to pay bail
        if (game.getEntities().getYou().isInJail()) {
            payBailBtn.setVisible(true);
        } else {
            payBailBtn.setVisible(false);
        }

        // Store your player's position
        int yourPosition = game.getEntities().getYou().getPosition();

        // PROPERTY TILE HANDLING
        if (game.getBoard().getTileType(yourPosition) == Board.PROPERTY) {
            cardContainer.getChildren().clear();

            // Draw property card with
            Pane card = GameControllerDrawFx.createPropertyCard(game.getEntities().getPropertyAtPosition(yourPosition));
            cardContainer.getChildren().add(card);

            // Get owner of property and set the button or label accordingly
            String propertyOwner = game.getEntities().getOwnerAtProperty(yourPosition);
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
        if (game.getBoard().getTileType(yourPosition) == Board.FREE_PARKING) {
            game.getEntities().getYou().setFreeParking(true);
            freeParkingCard.setVisible(true);
        }

        // Player is on a chance card tile
        if (game.getBoard().getTileType(yourPosition) == Board.CHANCE) {
            // Get a random chance card and display it
            ChanceCard chanceCard = ChanceCardData.getRandomChanceCard();
            ChanceCardController.display(chanceCard, cardContainer);
        }
    }

    /**
     * Updates all the scene's graphics to reflect the changes in the database
     */
    public void updateBoard() {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update round- and status-value
        roundValue.setText(String.valueOf(game.getRoundNumber() + 1));
        statusValue.setText("Waiting for " + game.getPlayers()[game.getCurrentTurn()] + " to finish their turn");

        // Update info for all players
        if (positions != null) updatePlayersInfo();
    }

    /**
     * This method runs at the start of each new turn, regardless if it's your turn or not
     * A couple things needs to be updated at the start of each turn
     */
    public void yourTurn() {
        // Play a sound to indicate that it is your turn
        String soundFile = "res/sounds/pling.mp3";

        Media sound = new Media(new File(soundFile).toURI().toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();

        // Set buttons state
        payBailBtn.setDisable(false);
        rolldiceBtn.setDisable(false);

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
        container.setOnMouseClicked(e -> {
            showProperties(username);
        });
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
                        MessagePopupController.show(msg, "dollarPositive.png");
                    else
                        MessagePopupController.show(msg, "dollarNegative.png");
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
     * Attempts to pay the player with the current owned property with the proper rent
     */
    public void rentTransaction() {
        GameLogic.payRent();

        freeParkingCard.setVisible(false);
        payRentBtn.setDisable(true);
        int[] currentDice = game.getDice().getLastThrow();
        if (currentDice[0] == currentDice[1] && !game.getEntities().getYou().isInJail()) {
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
            if (GameLogic.purchaseProperty()) {
                // Update board
                updateBoard();

                // Update property information label
                buyPropertyBtn.setVisible(false);
                propertyOwned.setVisible(true);
                propertyOwned.setText("Owned by you");
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
            payBailBtn.setVisible(false);
            payBailBtn.setDisable(true);
        } else {
            Alert messageBox = new Alert(Alert.AlertType.INFORMATION,
                    "You do not have enough funds to pay bail.");
            messageBox.showAndWait();
        }
    }

    public void clickToBuyHouse(Property property){
        //ideally the number of houses on each street cannot be more than 1 greater than that of the other streets in the same colorset, this is not implementet here
        if (((Street) property).addHouse()) {
            GameControllerDrawFx.drawHouse(housegrid, (Street) property);
        }
    }
}