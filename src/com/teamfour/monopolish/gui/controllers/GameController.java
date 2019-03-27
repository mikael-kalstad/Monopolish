package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
 * Controller class for game view. This class acts as a communication layer between the player and the GameLogic class.
 * We also use this class a general layout of the main game loop. Here, we control when the user should be able
 * to click certain buttons, what happens when you click them, and handles all graphical interfaces and updates
 *
 * @author Bård Hestmark
 * @version 1.6
 */

public class GameController {
    // Timer for checking database updates
    private Timer databaseTimer = new Timer();

    // GameLogic for handling more intricate game operations
    private GameLogic gameLogic = new GameLogic(Handler.getCurrentGameId());

    private String yourUsername = Handler.getAccount().getUsername();

    // Array for events in game
    private ArrayList<Text> eventList = new ArrayList<>();

    // Array for players in game
    private ArrayList<GameControllerDrawFx> playerList = new ArrayList<>();

    // Elements in board
    @FXML private AnchorPane phillip;
    @FXML private Button buypropertyBtn;
    @FXML private Label propertyowned;
    @FXML private VBox playerInfo;
    @FXML private TextFlow propertycard;
    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;

    // Dice images in board
    @FXML ImageView dice1_img, dice2_img;

    // Elements in sidebar
    @FXML private Button rolldiceBtn, endturnBtn;
    @FXML private Label roundValue, statusValue;
    @FXML private Label username, userMoney;
    @FXML private Pane userColor, userPropertiesIcon;
    @FXML private Pane opponentsContainer;

    // Chat elements
    @FXML private Pane chatContainer;
    @FXML private Pane chatMessagesContainer;
    @FXML private ScrollPane chatMessageScrollPane;
    @FXML private TextField chatInput;
    @FXML private Pane chatWarning;
    private boolean chatOpen = false;
    private int CHAT_MAX_CHARACTERS = 40;

    // Properties dialog
    @FXML private Pane propertiesContainer;
    @FXML private FlowPane propertiesContentContainer;
    @FXML private Text propertiesUsername;

    /**
     * Launches when the scene is loaded.
     */
    @FXML public void initialize() {
        // Load gamelogic and initialize the game setup
        try { gameLogic.setupGame(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Get usernames in the game
        String[] usernames = gameLogic.getTurns();

        // Create FXPlayers and add to a list for later references
        for (String username : usernames) {
            playerList.add(new GameControllerDrawFx(username, GameControllerDrawFx.getMAX(), GameControllerDrawFx.getMAX()));
        }

        // Update players in the sidebar
        updatePlayersInfo();

        // Draw players on the board
        drawPlayerPieces();

        // Start the game!
        waitForTurn();

        // Update chat messages periodically
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ArrayList<String[]> chatContent = Handler.getGameDAO().getChat(gameLogic.getGameId());

                    // Reset all chat messages
                    chatMessagesContainer.getChildren().clear();

                    for (String[] message : chatContent) {
                        GameControllerDrawFx.createChatRow(
                                chatMessagesContainer,
                                message[0].trim(),
                                message[2].trim(),
                                message[1].trim()
                        );

                        // Scroll to bottom
                        chatMessageScrollPane.setVvalue(1);
                    }
                });
            }
        };

        Timer chatTimer = new Timer();
        long delay = 1000L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        chatTimer.scheduleAtFixedRate(task, delay, period);

        // Check if chat input has reached max number of characters
        chatInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if input is longer than allowed
            if (chatInput.getText().length() > CHAT_MAX_CHARACTERS) {
                // Set input text to value before going over the limit
                chatInput.setText(oldValue);

                // Set cursor to the end of the input
                chatInput.positionCaret(chatInput.getText().length());

                // Change border style and show warning
                chatInput.setStyle("-fx-border-color: orange");
                chatWarning.setVisible(true);
            } else {
                // Reset border style and hide warning
                chatInput.setStyle("-fx-border-color: white");
                chatWarning.setVisible(false);
            }
        });

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
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
                gameLogic.getEntityManager().removePlayer(USERNAME);
                databaseTimer.cancel(); // Stop databaseTimer thread

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

            if (alertDialog.getResult().getButtonData().isDefaultButton()) {
                // Remove player from lobby
                final String USERNAME = Handler.getAccount().getUsername();
                int lobbyId = Handler.getLobbyDAO().getLobbyId(USERNAME);
                System.out.println("lobby id when leaving... " + lobbyId);
                Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));
                gameLogic.getEntityManager().removePlayer(USERNAME);
                // Change view to dashboard
                Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
            }
        }
    }

    public void forfeit() {
        // Some voting gui and logic here...
    }

    /**
     * Show a popup dialog with all the properties belonging to a player
     *
     * @param username Target user
     */
    public void showProperties(String username) {
        propertiesContainer.setVisible(true);
        propertiesUsername.setText(username);
    }

    /**
     * Hide the popup dialog showing the properties to a player
     */
    public void closePropertiesDialog() {
        propertiesContainer.setVisible(false);
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
     * Sends a string text from the chat input box to the chat
     */
    public void addChatMessage() {
        if (chatInput.getText().trim().isEmpty()) {
            chatInput.setStyle("-fx-border-color: yellow;");
        } else {
            chatInput.setStyle("-fx-border-color: white;");
            Handler.getGameDAO().addChatMessage(yourUsername, chatInput.getText().trim());

            // Reset input text
            chatInput.setText("");
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
        try {
            diceValues = gameLogic.throwDice();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Check if diceValues array is initialized or length is less than two
        if (diceValues == null || diceValues.length < 2) return;
        // Update dice images and log on board
        dice1_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[0] + ".png")));
        dice2_img.setImage(new Image(("file:res/gui/dices/dice" + diceValues[1] + ".png")));
        String s = "Threw dice:  " + diceValues[0] + ",  " + diceValues[1];
        addToEventlog(s);

        // Update board view
        updateBoard();

        // Check the tile you are currently on and call that event
        callTileEvent();

        // Update board view again
        updateBoard();

        // If the player didn't throw two equal dices, disable the dice button. If not, the player can throw dice again
        if (diceValues[0] != diceValues[1]) {
            rolldiceBtn.setDisable(true);
            endturnBtn.setDisable(false);
        }
    }

    /**
     * Ends your current turn
     */
    public void endTurn() {
        try {
            // Disable this button
            endturnBtn.setDisable(true);
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
        // If on property, enable button to buy property
        // Display property card in middle of board
        if(gameLogic.getBoard().getTileType(gameLogic.getPlayer(yourUsername).getPosition()) == Board.PROPERTY) {
            // Activate button
            // TODO: This is just a test
            int[] prices = {1, 2, 3, 4, 5, 6, 7, 8, 9};
            GameControllerDrawFx.createPropertyCard(phillip,
                    gameLogic.getEntityManager().getPropertyAtPosition(gameLogic.getPlayer(yourUsername).getPosition()));
        } else {
            phillip.getChildren().clear();
        }

        // If on non-available property, send prompt (OR SOMETHING) to owner of property

        // If go-to jail, go to jail!
        if (gameLogic.getPlayer(yourUsername).getPosition() == gameLogic.getGoToJailPosition()) {
            try {
                gameLogic.setPlayerInJail(yourUsername, true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates all the scene's graphics to reflect the changes in the database
     */
    public void updateBoard() {
        try {
            int[] positions = gameLogic.getPlayerPositions();
            for (int i = 0; i < positions.length; i++) {
                setPiecePosition(playerList.get(i), positions[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        roundValue.setText(String.valueOf(gameLogic.getRoundNumber() + 1));
        userMoney.setText(String.valueOf(gameLogic.getPlayer(yourUsername).getMoney()));
        statusValue.setText("Waiting for " + gameLogic.getCurrentPlayer() + " to finish their turn");
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
            databaseTimer.cancel();
            rolldiceBtn.setDisable(false);
        }
    }

    /**
     * Draw all players on the board on their respective positions
     */
    public void drawPlayerPieces() {
        for (GameControllerDrawFx player : playerList) {
            GridPane.setConstraints(player, player.getPosX(), player.getPosY());
        }
        checkForOthers(playerList.get(0));

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);
    }

    /**
     * Moves the specified player piece to the specified position.
     * @param piece
     * @param position
     */
    public void setPiecePosition(GameControllerDrawFx piece, int position) {
        piece.posToXY(position);
        piece.setAlignment(Pos.CENTER);
        GridPane.setConstraints(piece, piece.getPosX(), piece.getPosY());

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);

        checkForOthers(piece);

        //String pos = player.getUsername() + " moved to X: " + player.getPosX() + " Y:" + player.getPosY();
        String pos = piece.getUsername() + " is at position " + piece.getTilePosition();
        addToEventlog(pos);
    }

    /**
     * This method checks if there are other game pieces on the same place. If there are, position
     * these pieces according to eachother
     * @param player
     */
    private void checkForOthers(GameControllerDrawFx player) {
        ArrayList<GameControllerDrawFx> checklist = new ArrayList<>();

        for (GameControllerDrawFx p : playerList) {
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

        // Go through all the players, update info and render GUI
        for (Player player : players) {
            // Find color associated with user
            color = getPlayerColor(player.getUsername());
            if (color == null) color = "red"; // Check if color has been assigned

            // Player is the actual user
            if (player.getUsername().equals(Handler.getAccount().getUsername())) {
                username.setText(player.getUsername());
                userMoney.setText(String.valueOf(player.getMoney()));
                userColor.setStyle("-fx-background-color: " + color + ";");

                // Show your own properties on click
                setPropertyOnClick(userPropertiesIcon, player.getUsername());
            }

            // Player is an opponent
            else {
                // Render opponentRow in opponentsContainer and save the propertyIcon that is returned
                Pane imgContainer = GameControllerDrawFx.createOpponentRow(
                        player.getUsername(),
                        color,
                        String.valueOf(player.getMoney()),
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
                System.out.println("COLOR SET: " + username + " " + player[1]);
                return player[1];
            }
        }
        return null;
    }

    public void buyPrompt() {
        Alert buyprompt = new Alert(Alert.AlertType.CONFIRMATION, "This property is available,\n do you want to buy it?",
                ButtonType.NO, ButtonType.YES);
        buyprompt.showAndWait();

        if (buyprompt.getResult() == ButtonType.YES) {
            //TODO: do transaction, transfer property to player
            buyprompt.close();
        }

        if (buyprompt.getResult() == ButtonType.NO) {
            buyprompt.close();
        }
    }
}