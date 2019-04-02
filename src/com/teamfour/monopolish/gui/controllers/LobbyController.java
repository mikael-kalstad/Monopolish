package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for lobby view,
 * handles all the logic for the lobbyList.
 * <b>Note: some gui elements are created in the LobbyDrawFx class.</b>
 *
 * @author Mikael Kalstad
 * @version 1.7
 */
public class LobbyController {
    private ArrayList<Pane> lobbyList = new ArrayList<>(); // List over all lobby container elements
    private ArrayList<String[]> colorList = new ArrayList<>(); // List containing username and color of players
    private final String USERNAME = Handler.getAccount().getUsername();
    private int current_lobby_id = -1; // Default when user is not in any lobby = -1
    private boolean READY = false;
    private Timer refreshTimer;
    private int time;

    // After refresh keep track of if the game has started
    private boolean game_starting = false;

    // GUI FXML elements
    @FXML private FlowPane lobbiesContainer;
    @FXML private Text noLobbyText;
    @FXML private Pane newLobbyDialog;
    @FXML private Pane newLobbyBackground;
    @FXML private TextField newLobbyNameInput;
    @FXML private Pane maxInputWarning;
    @FXML private Text newLobbyMsg;
    @FXML private Pane countdown;
    @FXML private Text countdownValue;

    // Max length for input in new lobby
    private int MAX_LENGTH_INPUT = 20;

    //  Status msg constants
    private final String STATUS_OPEN = "OPEN";
    private final String STATUS_FULL = "LOBBY FULL";
    private final String STATUS_STARTING = "STARTING";
    private final String STATUS_STARTED = "IN GAME";

    // Btn msg constants
    private final String BTN_LEAVE = "Leave";
    private final String BTN_JOIN = "Join";
    private final String BTN_READY = "Ready";
    private final String BTN_NOT_READY = "Not Ready";

    // Player colors
    private final String PLAYER_COLOR_BLUE = "#03A9F4";
    private final String PLAYER_COLOR_RED = "#ef5350";
    private final String PLAYER_COLOR_GREEN = "#66BB6A";
    private final String PLAYER_COLOR_YELLOW = "#FFF176";

    // Input colors
    private final String INPUT_COLOR_NORMAL = "white";
    private final String INPUT_COLOR_REQUIRED = "orange";

    // Ids for gui elements
    private final String PLAYER_CONTAINER_ID = "playerContainer";
    private final String BUTTON_JOIN_ID = "join";
    private final String BUTTON_READY_ID = "ready";
    private final String IMAGE_READY_ID = "readyImg";
    private final String STATUS_VALUE_ID = "statusValue";

    // Countdown constants
    private final int COUNTDOWN_TIME = 5;

    @FXML public void initialize() {
        // Update lobbies periodically to display changes in the database
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> refresh());
            }
        };

        refreshTimer = new Timer();
        long delay = 1000L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        refreshTimer.scheduleAtFixedRate(task, delay, period);

        refresh();

        // Limit newLobby input length
        FxUtils.limitInputLength(MAX_LENGTH_INPUT, newLobbyNameInput, maxInputWarning);

        // When window is closed
        Handler.getSceneManager().getWindow().setOnCloseRequest(e -> {
            // If the user is in a lobby
            if (current_lobby_id > 0) {
                e.consume(); // Override default closing method

                Alert alertDialog = AlertBox.display(
                        Alert.AlertType.CONFIRMATION,
                        "Warning", "Do you want to leave?",
                        "If you leave, you will lose you position in the current lobby"
                );
                alertDialog.showAndWait();

                // Check if yes button is pressed
                if (alertDialog.getResult().getButtonData().isDefaultButton()) {
                    // Remove user if in a lobby
                    Handler.getAccountDAO().setInactive(Handler.getAccount().getUsername());    // Sets user to inactive in DB
                    Handler.getLobbyDAO().removePlayer(USERNAME, current_lobby_id);

                    refreshTimer.cancel(); // Stop refreshTimer thread

                    // Logout user
                    Handler.getAccountDAO().setInactive(USERNAME);

                    // Close the window
                    Handler.getSceneManager().getWindow().close();
                }
            } else {
                // Logout user
                Handler.getAccountDAO().setInactive(USERNAME);
            }
        });
    }

    /**
     * This method will get data from database about all the lobbies,
     * and then refresh all the gui elements to be up to date.
     *
     * By using this method one can separate the actual gui "drawing" and the data that should be displayed. <br/>
     * <b>This method should be called whenever some data is changed in the database to update the gui.</b>
     * <br/><br/>
     *
     *  Expected data from the database about each player in the lobby:
     *  <ul>
     *      <li>1. Lobby id</li>
     *      <li>2. Username</li>
     *      <li>3. Ready (boolean)</li>
     *      <li>4. lobby-name</li>
     *  </ul>
     */
    public void refresh() {
        // Check if any lobbies in database are empty
        Handler.getLobbyDAO().removeEmptyLobbies();

        // Clear all lobbies locally and set default variable values
        lobbiesContainer.getChildren().clear();
        lobbyList.clear();
        current_lobby_id = -1;

        // Clear list containing username and color of players
        colorList.clear();

        // Get data from database about all lobbies
        ArrayList<String[]> lobbyInfo = Handler.getLobbyDAO().getAllLobbies();

        // Check if there are any lobbies and set "placeholder" text
        if (lobbyInfo.isEmpty()) noLobbyText.setVisible(true);
        else noLobbyText.setVisible(false);

        // Go through all the data and update content
        for (String[] data : lobbyInfo) {
            int lobby_id = Integer.valueOf(data[0]);
            String username = data[1];
            boolean ready = Boolean.valueOf(data[2]);
            String lobbyName = data[3];

            Pane container = getLobbyContainer(lobby_id);

            // Create lobby if it does not exists
            if (container == null) {
                container = LobbyDrawFx.drawNewLobby(lobbyName);
                container.setId(String.valueOf(lobby_id));
                lobbyList.add(container);
                lobbiesContainer.getChildren().add(container);
            }

            // Draw player in the lobby
            drawPlayer(username, container);

            // Check if current data is for the user
            if (username.equals(this.USERNAME)) {
                current_lobby_id = lobby_id;
                READY = ready;
            }

            // Update "local" player elements
           updatePlayerElements(container, username, ready);
        }

        // Go through all lobbies and update "global" lobby elements
        for (Pane lobby: lobbyList) {
            updateLobbyElements(lobby);
        }
    }

    /**
     * Get container element for the actual lobby given lobby_id
     *
     * @param lobby_id id for the lobby
     * @return lobby container if found
     */
    private Pane getLobbyContainer(int lobby_id) {
        for (Pane container : lobbyList) {
            if (container.getId().equals(String.valueOf(lobby_id))) return container;
        }
        return null;
    }

    /**
     * Get container element for players given a lobby container and id
     * @param lobby actual lobby
     * @return Players Container for actual lobby
     */
    private Pane getContainerById(Pane lobby, String id) {
        Object[] elements = lobby.getChildren().toArray();
        for (Object elem : elements) {
            if (elem == null) continue;

            // If container is a pane and has the right id
            if (elem instanceof Pane && ((Pane) elem).getId() != null && ((Pane) elem).getId().equals(id)) {
                return (Pane)elem;
            }
        }
        return null;
    }

    /**
     * Get button element for a given lobby container and id
     *
     * @param lobby actual lobby
     * @return a button of class Button
     */
    private Button getBtnById(Pane lobby, String id) {
        Object[] elements = lobby.getChildren().toArray();
        for (Object elem : elements) {
            if (elem == null) continue;

            // If element is a button and has the right id
            if (elem instanceof Button && ((Button) elem).getId() != null && ((Button) elem).getId().equals(id)) {
                return (Button) elem;
            }
        }
        return null;
    }

    /**
     * Get text element for a given lobby container and id
     *
     * @param lobby actual lobby
     * @return a button of class Button
     */
    private Text getTextById(Pane lobby, String id) {
        Object[] elements = lobby.getChildren().toArray();
        for (Object elem : elements) {
            // If element is a button and has the right id
            if (elem instanceof Text && ((Text) elem).getId() != null && ((Text) elem).getId().equals(id)) {
                return (Text) elem;
            }
        }
        return null;
    }
    private ImageView getImageById(Pane lobby, String id) {
        Object[] elements = lobby.getChildren().toArray();
        for (Object elem : elements) {
            // If element is a button and has the right id
            if (elem instanceof ImageView && ((ImageView) elem).getId() != null && ((ImageView) elem).getId().equals(id)) {
                return (ImageView) elem;
            }
        }
        return null;
    }

    /**
     * Will make a dialog for making a new lobby appear
     */
    public void showNewLobbyDialog() {
        // Show backgroundOverlay
        newLobbyBackground.setVisible(true);

        // Show a new lobby dialog
        newLobbyDialog.setVisible(true);
    }

    /**
     * Will hide the dialog for a new lobby
     */
    public void closeNewLobbyDialog() {
        // Hide backgroundOverlay
        newLobbyDialog.setVisible(false);

        // Hide new lobby dialog
        newLobbyBackground.setVisible(false);

        // Reset input text
        newLobbyNameInput.setText("");
    }

    /**
     * Method that will create a new lobby with a lobby name
     */
    public void createNewLobby() {
        // Change input styling if input is empty
        if (newLobbyNameInput.getText().trim().isEmpty()) {
            LobbyDrawFx.setBorderStyle(newLobbyNameInput, INPUT_COLOR_REQUIRED);
            newLobbyMsg.setVisible(true);
            FxUtils.setTextColor(newLobbyMsg, INPUT_COLOR_REQUIRED);
        }
        // If input is not empty create new lobby
        else {
            Handler.getLobbyDAO().newLobby(USERNAME, newLobbyNameInput.getText());

            // If user is already in a lobby
            if (current_lobby_id > 0) {
                Handler.getLobbyDAO().removePlayer(USERNAME, current_lobby_id);
            }

            // Update GUI
            LobbyDrawFx.setBorderStyle(newLobbyNameInput, INPUT_COLOR_NORMAL); // Reset border style
            newLobbyMsg.setVisible(false); // Hide msg in lobbies container
            closeNewLobbyDialog();
        }
        refresh();
    }

    /**
     * Add player to a lobby
     *
     * @param username
     * @param container Node target, which lobby to join
     */
    private void drawPlayer(String username, Pane container) {
        Pane playerContainer = getContainerById(container, PLAYER_CONTAINER_ID);
        if (playerContainer == null) return;

        // Change color based on index
        String color = PLAYER_COLOR_BLUE; // Default if numOfPlayers = 0
        int numOfPlayers = playerContainer.getChildren().size();

        switch (numOfPlayers) {
            case 1:
                color = PLAYER_COLOR_RED;
                break;
            case 2:
                color = PLAYER_COLOR_GREEN;
                break;
            case 3:
                color = PLAYER_COLOR_YELLOW;
                break;
        }

        // Update list containing username and color of players
        colorList.add(new String[]{username, color});

        // Render player in container
        GridPane playerRow = LobbyDrawFx.drawPlayerRow(color, username, numOfPlayers);
        playerRow.setId(username);
        playerContainer.getChildren().add(playerRow);
    }

    /**
     * This method will update player elements in the gui for an individual user.
     *
     * @param container lobby-container
     * @param username the target user
     * @param playerReady is the player ready to play?
     */
    private void updatePlayerElements(Pane container, String username, boolean playerReady) {
        // Find the player container
        Pane playersContainer = getContainerById(container, PLAYER_CONTAINER_ID);

        // Getting ready image-view
        // Location: lobbyContainer > playerContainer > playerRow > imageView
        ImageView readyImg = null;
        Pane playerRow = null;
        if (playersContainer != null) playerRow = getContainerById(playersContainer, username);
        if (playerRow != null) readyImg = getImageById(playerRow, IMAGE_READY_ID);
        if (readyImg == null) return; // Avoid exception

        // Set ready images
        if (playerReady) readyImg.setImage(new Image("file:res/gui/Lobby/ready.png"));
        else readyImg.setImage(new Image("file:res/gui/lobby/notReady.png"));
    }

    /**
     * This method will update elements in the lobby,
     * excluding individual player elements which are updated in the method updatePlayerElements.
     *
     * @param container lobby-container
     */
    private void updateLobbyElements(Pane container) {
        // Find gui elements
        Pane playersContainer = getContainerById(container, PLAYER_CONTAINER_ID);
        Button joinBtn = getBtnById(container, BUTTON_JOIN_ID);
        Button readyBtn = getBtnById(container, BUTTON_READY_ID);
        Text statusValue = getTextById(container, STATUS_VALUE_ID);

        // If any of these elements are null return to avoid nullpointerexception
        if (playersContainer == null || joinBtn == null || statusValue == null || readyBtn == null) return;

        int numOfPlayers = playersContainer.getChildren().size();
        int lobby_id = Integer.valueOf(container.getId());
        int numOfReady = Handler.getLobbyDAO().getAllReadyInLobby(lobby_id);

        // If the user is in the actual lobby
        if (current_lobby_id == lobby_id) {
            // Enable and change button style
            readyBtn.setDisable(false);
            joinBtn.setDisable(false);
            LobbyDrawFx.setBtnStyle(joinBtn, BTN_LEAVE, PLAYER_COLOR_RED);

            // Check if user is ready or not and change btn accordingly
            if (READY) LobbyDrawFx.setBtnStyle(readyBtn, BTN_NOT_READY, PLAYER_COLOR_RED);
            else LobbyDrawFx.setBtnStyle(readyBtn, BTN_READY, PLAYER_COLOR_GREEN);

            // Check if game has started
            if (numOfReady != numOfPlayers || numOfReady == 1) {
                LobbyDrawFx.setBtnStyle(joinBtn, BTN_LEAVE, PLAYER_COLOR_RED);
                statusValue.setText(numOfReady + " / " + numOfPlayers + " ready");
                readyBtn.setDisable(false);
            }

            // Check if game should start
            if (numOfPlayers > 1 && numOfReady == numOfPlayers && !game_starting) {
                statusValue.setText(STATUS_STARTING);
                FxUtils.setTextColor(statusValue, PLAYER_COLOR_RED);
                startGame(lobby_id);
                game_starting = true;
            }

            // Check if game is starting
            if (game_starting) {
                statusValue.setText(STATUS_STARTING);
                FxUtils.setTextColor(statusValue, PLAYER_COLOR_RED);
            }
        }

        // User not in any lobby
        else {
            // Check if game is started
            if (numOfPlayers > 1 && numOfReady == numOfPlayers) {
                joinBtn.setDisable(true);
                statusValue.setText(STATUS_STARTED);
                FxUtils.setTextColor(statusValue, PLAYER_COLOR_RED);
            }

            // Check if game is full
            else if (numOfPlayers == 4) {
                joinBtn.setDisable(true);
                statusValue.setText(STATUS_FULL);
                FxUtils.setTextColor(statusValue, PLAYER_COLOR_RED);
            }

            // Lobby open to join
            else {
                LobbyDrawFx.setBtnStyle(joinBtn, BTN_JOIN, "#FF9800");
                readyBtn.setDisable(true);
                joinBtn.setDisable(false);
                statusValue.setText(STATUS_OPEN);
                FxUtils.setTextColor(statusValue, PLAYER_COLOR_GREEN);
            }
        }

        //Set logic when player uses the "join" button (i.e. joins or leaves the lobby)
        joinBtn.setOnAction(click -> {
            // If user joins lobby
            if (joinBtn.getText().equals(BTN_JOIN)) {
                Handler.getLobbyDAO().addPlayer(USERNAME, lobby_id);

                // Remove player if already in a lobby
                if (current_lobby_id > 0 ) {
                    Handler.getLobbyDAO().removePlayer(USERNAME, current_lobby_id);
                }
            }

            // If player leaves lobby
            else {
                Handler.getLobbyDAO().removePlayer(USERNAME, lobby_id);
            }

            refresh();
        });

        // Set logic when player uses the "userReady" button (i.e. sets userReady or not)
        readyBtn.setOnAction(click -> {
            Handler.getLobbyDAO().setReady(lobby_id, USERNAME, readyBtn.getText().equals(BTN_READY));
            refresh();
        });
    }

    /**
     * This method will start the countdown, and then start the game after countdown is finished.
     * It will also update some GUI elements when game is starting: <br/>
     * - Ready button will be disabled
     *
     */
    private void startGame(int lobby_id) {
        System.out.println("STARTING GAME!");
        // Countdown refreshTimer when game start
        Timer countDownTimer = new Timer();
        countdown.setVisible(true);
        countdownValue.setVisible(true);
        time = COUNTDOWN_TIME;

        TimerTask countDownTask = new TimerTask() {
            @Override
            public void run() {
                int numOfPlayers = Handler.getLobbyDAO().getUsersInLobby(lobby_id).size();
                int numOfReady = Handler.getLobbyDAO().getAllReadyInLobby(lobby_id);

                Platform.runLater(() -> {
                    // Check if player left the lobby
                    if (current_lobby_id == -1 || numOfReady != numOfPlayers) {
                        countDownTimer.cancel();
                        countdown.setVisible(false);
                        game_starting = false;
                    }
                    // Logic when game should start
                    else if (time == 0) {
                        countDownTimer.cancel();
                        refreshTimer.cancel();

                        // Make a new game in database
                        int gameId = Handler.getGameDAO().insertGame(current_lobby_id, Handler.getAccount().getUsername());
                        Handler.setCurrentGameId(gameId);

                        // Set colorlist in Handler
                        Handler.setColorList(colorList);

                        // Switch so that the timers are turned off in the next run
                        time = -1;

                        // Switch to game scene
                        Handler.getSceneManager().setScene(ViewConstants.GAME.getValue());
                    } else if (time == -1) {
                        // Stop the timers
                        countDownTimer.cancel();
                        refreshTimer.cancel();
                    } else {
                        time--;

                        // Update countdown value
                        countdownValue.setText(String.valueOf(time));
                    }
                });
            }
        };

        long delay = 500L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        countDownTimer.scheduleAtFixedRate(countDownTask, delay, period);
    }

    /**
     * Go back to the dashboard view
     */
    public void leave() {
        // Remove player from lobby if user is in a lobby
        if (current_lobby_id != -1)
            Handler.getLobbyDAO().removePlayer(USERNAME, Handler.getLobbyDAO().getLobbyId(USERNAME));

        // Switch to dashboard
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());

        // Stop refreshTimer thread
        refreshTimer.cancel();
        refreshTimer.purge();
    }
}