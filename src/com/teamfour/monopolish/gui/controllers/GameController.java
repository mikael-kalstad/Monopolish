package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.propertylogic.Property;
import com.teamfour.monopolish.gui.views.ViewConstants;
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
 * @version 1.8
 */

public class GameController {
    // Timer for checking database updates
    private Timer databaseTimer = new Timer();
    private Timer chatTimer = new Timer();
    private Timer roundTimer = new Timer();
    private final int ROUND_COUNTDOWN_TIME = 60;
    private int time;

    // GameLogic for handling more intricate game operations
    private GameLogic gameLogic = new GameLogic(Handler.getCurrentGameId());

    private final String USERNAME = Handler.getAccount().getUsername();

    // Array for events in game
    private ArrayList<Text> eventList = new ArrayList<>();

    // Background overlay for popups
    @FXML private Pane backgroundOverlay;

    // Elements in board
    @FXML private AnchorPane cardContainer;
    @FXML private Button buypropertyBtn;
    @FXML private Label propertyOwned;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    // Dice images in board
    @FXML ImageView dice1_img, dice2_img;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, endturnBtn;
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

    //trading screen:
    @FXML private Pane tradecontainer;
    @FXML private FlowPane youroffer, askfor, yourproperties, opponentsproperties;
    @FXML private Button offermoneyok, requestmoneyok, clearyou, clearopponent, canceltrade, proposeTradeBtn;
    @FXML private Label tradeusername, yourtrademoney, requestedtrademoney, invalidinput, invalidinput2;
    @FXML private TextField offeredmoney, requestedmoney;

    /**
     * Launches when the scene is loaded.
     */
    @FXML public void initialize() {
        // Load gamelogic and initialize the game setup
        try { gameLogic.setupGame(); }
        catch (SQLException e) { e.printStackTrace(); }

        updateBoard();

        // Start the game!
        waitForTurn();

        // Load chat
        try {
            Node chat = FXMLLoader.load(getClass().getResource(ViewConstants.FILE_PATH.getValue() + ViewConstants.CHAT.getValue()));
            chatContainer.getChildren().add(chat);
        }
        catch (IOException e) { e.printStackTrace(); }

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
                final String USERNAME = Handler.getAccount().getUsername();
                Handler.getAccountDAO().setInactive(USERNAME);
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
                gameLogic.getEntityManager().removePlayer(USERNAME);
                databaseTimer.cancel(); // Stop databaseTimer thread
                databaseTimer.purge();
                chatTimer.cancel();

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
            chatTimer.cancel();
            // Remove player from lobby
            final String USERNAME = Handler.getAccount().getUsername();
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
     * Will run when the forfeit button is clicked.
     * A forfeit dialog will appear on the screen
     */
    public void forfeit() {
        // Load forfeit GUI
        addElementToContainer(ViewConstants.FORFEIT.getValue(), forfeitContainer);
        backgroundOverlay.setVisible(true);
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

        System.out.println("User has " + gameLogic.getPlayer(username).getProperties().size() + " properties");

        // Add all properties that the user owns to the dialog
        for (Property p : gameLogic.getPlayer(username).getProperties()) {
            Pane card = GameControllerDrawFx.createPropertyCard(p);
            propertiesContentContainer.getChildren().add(card);
        }

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
        try { diceValues = gameLogic.throwDice(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Check if diceValues array is initialized or number of dices is not correct
        if (diceValues == null || diceValues.length != 2) return;

        // Update dice images and log on board
        dice1_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[0] + ".png")));
        dice2_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[1] + ".png")));
        String s = USERNAME + " threw dice:  " + diceValues[0] + ",  " + diceValues[1];

        // Animation constants
        /*final int DURATION = 600;
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
        pt.play();*/

        int[] finalDiceValues = diceValues;
        /*pt.onFinishedProperty().set(e -> {*/
            addToEventlog(s);

            // Update board view to show where player moved
            updateBoard();

            // Check the tile you are currently on and call that event
            callTileEvent();

            // Update board view again
            updateBoard();

            // If the player didn't throw two equal dices, disable the dice button. If not, the player can throw dice again
            if (finalDiceValues[0] != finalDiceValues[1]) {
                rolldiceBtn.setDisable(true);
                endturnBtn.setDisable(false);
            }
        /*});*/

    }

    /**
     * Ends your current turn
     */
    public void endTurn() {
        // Stop and reset timer
        //roundTimer.cancel();
        //roundTimeValue.setText(String.valueOf(ROUND_COUNTDOWN_TIME));

        try {
            // Disable buttons
            endturnBtn.setDisable(true);
            buypropertyBtn.setDisable(true);

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
            // Draw property card with
            Pane card = GameControllerDrawFx.createPropertyCard(gameLogic.getEntityManager().getPropertyAtPosition(gameLogic.getPlayer(USERNAME).getPosition()));
            cardContainer.getChildren().add(card);

            // Get owner of property and set the button or label accordingly
            String propertyOwner = gameLogic.getEntityManager().getOwnerAtProperty(yourPosition);
            if (propertyOwner == null || propertyOwner.equals("")) {
                // If property is available, show button
                buypropertyBtn.setDisable(false);
                buypropertyBtn.setVisible(true);
                propertyOwned.setVisible(false);
            } else {
                // If owned, display name of owner
                buypropertyBtn.setDisable(true);
                buypropertyBtn.setVisible(false);
                propertyOwned.setVisible(true);
                propertyOwned.setText("Owned by " + propertyOwner);

                // If this is not your property, prepare to get rented! Or something
                if (!propertyOwner.equals(USERNAME))
                    rentTransaction();
            }
        } else {
            // If no property here, make sure to clear the property
            cardContainer.getChildren().clear();
            buypropertyBtn.setVisible(false);
        }

        // If on free parking, get a free-parking token
        if (gameLogic.getPlayer(USERNAME).getPosition() == gameLogic.getBoard().getFreeParkingPosition()) {
            gameLogic.getPlayer(USERNAME).setFreeParking(true);
        }

        // If go-to jail, go to jail!
        if (gameLogic.getPlayer(USERNAME).getPosition() == gameLogic.getBoard().getGoToJailPosition()) {
            try {
                gameLogic.setPlayerInJail(USERNAME, true);
                System.out.println("YOU ARE GOING TO JAIL: " + USERNAME);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates all the scene's graphics to reflect the changes in the database
     */
    public void updateBoard() {
        System.out.println("Active threads: " + Thread.activeCount());
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
        userMoney.setText(String.valueOf(gameLogic.getPlayer(USERNAME).getMoney()));
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

        // Iniate round timer
        /*time = ROUND_COUNTDOWN_TIME;
        roundTimer = new Timer();

        TimerTask countdown = new TimerTask() {
            @Override
            public void run() {
                if (time > 0) {
                    Platform.runLater(() -> roundTimeValue.setText(String.valueOf(time)));
                    time--;
                } else {
                    System.out.println("END OF YOUR TURN!");
                    endTurn();
                    roundTimer.cancel();
                    roundTimeValue.setText(String.valueOf(ROUND_COUNTDOWN_TIME));
                }
            }
        };

        long delay = 1000L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        roundTimer.scheduleAtFixedRate(countdown, delay, period);*/

        // If this is your turn, stop the database check databaseTimer and enable the button to roll dice
        if (yourTurn) {
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

            // Player is the actual user
            if (player.getUsername().equals(Handler.getAccount().getUsername())) {
                username.setText(player.getUsername());
                userMoney.setText(String.valueOf(player.getMoney()));
                userColor.setStyle("-fx-background-color: " + color + ";");

                // Show your own properties on click
                setPropertyOnClick(userPropertiesIcon, player.getUsername());

                // Set img if it is assigned
                //userColor.getChildren().clear(); // Reset

                if (img != null) {
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
                        String.valueOf(player.getMoney()),
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

    public void rentTransaction() {
        Alert messageBox;
        // TODO: Everything
        if (gameLogic.getPlayer(USERNAME).hasFreeParking()) {
            messageBox = new Alert(Alert.AlertType.INFORMATION,
                    "You have a 'Free Parking' token! You don't have to pay rent here", ButtonType.OK);
            messageBox.showAndWait();
            gameLogic.getPlayer(USERNAME).setFreeParking(false);
        } else {
            try {
                gameLogic.rentTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            messageBox = new Alert(Alert.AlertType.INFORMATION,
                                "You have paid rent!", ButtonType.OK);
            messageBox.showAndWait();
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
                }
                System.out.println("BUYING PROPERTY...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            buyprompt.close();

            // Update board
            updateBoard();

            // Update property information label
            buypropertyBtn.setVisible(false);
            propertyOwned.setVisible(true);
            propertyOwned.setText("Owned by " + USERNAME);
        }
        if (buyprompt.getResult() == ButtonType.NO) {
            buyprompt.close();
        }
    }

    /**
     * Draws a trading screen, letting you choose what to trade
     * @param opponent Username of the opponent you want to trade with
     */
    public void showTradeScreen(String opponent) {

        //loading the trading screen:
        try {
            Pane showTrade = FXMLLoader.load(getClass().getResource("com.teamfour.monopolish.gui.views.trading"));
            tradecontainer.getChildren().clear();
            tradecontainer.getChildren().add(showTrade);
            tradecontainer.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //just some cosmetics
        tradeusername.setText("Trading with: " + opponent);
        yourtrademoney.setText("0$");
        requestedtrademoney.setText("0$");

        //Lists of the properties available
        ArrayList<Pane> yourCards = new ArrayList<>();
        ArrayList<Pane> opponentsCards = new ArrayList<>();

        //Lists for the cards on offer,
        // because its less work than getting the cards out of the flowPane(they get converted to nodes)
        ArrayList<Pane> offeredCards = new ArrayList<>();
        ArrayList<Pane> requestedCards = new ArrayList<>();

        for (Property property : gameLogic.getPlayer(gameLogic.getYourPlayer().getUsername()).getProperties()){
            yourCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        for (Property property : gameLogic.getPlayer(opponent).getProperties()){
            opponentsCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        //setting onclick for the cards so they change pane when clicked upon,
        // also adding them to the onoffer Arraylists
        for (Pane card : yourCards) {
            card.setOnMouseClicked(event -> {
                try {
                    youroffer.getChildren().add(card);
                    offeredCards.add(card);
                }
                catch (IllegalArgumentException e) {
                    yourproperties.getChildren().add(card);
                    offeredCards.remove(card);
                }
            });
        }
        for (Pane card : opponentsCards) {
            card.setOnMouseClicked(event -> {
                try {
                    askfor.getChildren().add(card);
                    requestedCards.add(card);
                }
                catch (IllegalArgumentException e) {
                    opponentsproperties.getChildren().add(card);
                    requestedCards.remove(card);
                }
            });
        }

        //setting buttons for clearing all offers
        clearyou.setOnAction(event -> {
            yourtrademoney.setText(null);
            youroffer.getChildren().clear();
            offeredCards.clear();
            yourproperties.getChildren().clear();
            yourproperties.getChildren().addAll(yourCards);
        });
        clearopponent.setOnAction(event -> {
            requestedtrademoney.setText(null);
            askfor.getChildren().clear();
            requestedCards.clear();
            opponentsproperties.getChildren().clear();
            opponentsproperties.getChildren().addAll(opponentsCards);
        });

        //setting buttons for offering money
        offermoneyok.setOnAction(event -> {
            String input = offeredmoney.getText();
            try {
                int check = Integer.parseInt(input);
                if (check > gameLogic.getYourPlayer().getMoney()) {
                    throw new IllegalArgumentException("Not enough money");
                }
                invalidinput.setVisible(false);
                yourtrademoney.setText(input + "$");
            } catch (NumberFormatException e) {
                invalidinput.setText("Invalid input");
                invalidinput.setVisible(true);
            } catch (IllegalArgumentException e) {
                invalidinput.setText(e.getMessage());
                invalidinput.setVisible(true);
            }
        });
        requestmoneyok.setOnAction(event -> {
            String input = requestedmoney.getText();
            try {
                int check = Integer.parseInt(input);
                if(check > gameLogic.getPlayer(opponent).getMoney()) {
                    throw new IllegalArgumentException("Not enough money");
                }
                invalidinput2.setVisible(false);
                requestedtrademoney.setText(input + "$");
            } catch (NumberFormatException e) {
                invalidinput2.setText("Invalid input");
                invalidinput2.setVisible(true);
            } catch (IllegalArgumentException e) {
                invalidinput2.setText(e.getMessage());
                invalidinput2.setVisible(true);
            }
        });

        //adding properties into panes for showing
        yourproperties.getChildren().addAll(yourCards);
        opponentsproperties.getChildren().addAll(opponentsCards);

        proposeTradeBtn.setOnAction(event -> {
            ArrayList<String> offeredPropertiesNameList = new ArrayList<>();
            ArrayList<String> requestedPropertiesNameList = new ArrayList<>();

            for (Pane p : offeredCards) {
                offeredPropertiesNameList.add(p.getId());
            }
            for (Pane p : requestedCards) {
                requestedPropertiesNameList.add(p.getId());
            }

            int offeredmoney = Integer.parseInt(yourtrademoney.getText());
            int requestedmoney = Integer.parseInt(requestedtrademoney.getText());

            //These results have to be sent to/through the database to be shown on the recieving player's screen
            //Call a method that takes these variables and sends them to the database?
        });

        canceltrade.setOnAction(event -> {
            tradecontainer.getChildren().clear();
            tradecontainer.setVisible(false);
        });
    }

    /**
     *
     * @param container The trade container
     * @param username The user the player wants to trade with
     */
    public void setTradeOnClick(Pane container, String username) {
        container.setOnMouseClicked(e -> showTradeScreen(username));
    }
}