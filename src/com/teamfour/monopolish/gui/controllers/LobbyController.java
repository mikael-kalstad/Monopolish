package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javax.sound.midi.SysexMessage;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller class for lobby view,
 * handles all the logic for the lobbyList.
 * <b>Note: the gui elements are created in the LobbyDrawFx class.</b>
 *
 * @author Mikael Kalstad
 * @version 1.5
 */
public class LobbyController {
    @FXML private FlowPane lobbiesContainer;
    @FXML private Pane newLobbyDialog;
    @FXML private Pane newLobbyBackground;
    @FXML private TextField newLobbyNameInput;
    @FXML private Text newLobbyMsg;

    private ArrayList<Pane> lobbyList = new ArrayList<>(); // List over all lobby container elements
    private final String USERNAME = Handler.getAccount().getUsername();
    private int current_lobby_id = -1; // Default when user is not in any lobby = -1
    private boolean READY = false;

    //  Status msg constants
    private final String STATUS_OPEN = "OPEN";
    private final String STATUS_FULL = "LOBBY FULL";
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
    private final String PLAYER_ROW_ID = "playerRow";
    private final String BUTTON_JOIN_ID = "join";
    private final String BUTTON_READY_ID = "ready";
    private final String IMAGE_READY_ID = "readyImg";
    private final String STATUS_VALUE_ID = "statusValue";

    @FXML public void initialize() {
        // Refresh page with enter or space key
//        Handler.getSceneManager().getWindow().getScene().setOnKeyPressed(event -> {
//            System.out.println("key pressed! " + event.getCode());
//            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
//
//                refresh();
//            }
//        });

        lobbiesContainer.setOnKeyPressed(event -> {
            System.out.println("key pressed! " + event.getCode());
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {

                refresh();
            }
        });

        // Update lobbies
        refresh();
    }

    /**
     * This method will get data from database about all the lobbies,
     * and then refresh all the gui elements to be up to date.
     *
     * By using this method one can separate the actual gui "drawing" and the data that should be displayed. <br/>
     * <b>This method should be called whenever some data is changed in the database to update the gui.</b>
     */
    public void refresh() {
        // Clear all lobbies
        lobbiesContainer.getChildren().clear();
        lobbyList.clear();
        current_lobby_id = -1;

        // Get data from database about all lobbies
        ArrayList<String[]> lobbyInfo = new ArrayList<>();
        try { lobbyInfo = Handler.getLobbyDAO().getAllLobbies(); }
        catch (SQLException e) { e.printStackTrace(); }

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
            drawPlayer(username, container, lobby_id);

            if (username.equals(this.USERNAME)) {
                current_lobby_id = lobby_id;
                READY = ready;
            }
            updatePlayerElements(container, username, ready);
        }

        // Go through all lobbies and update btn action and styling
        for (Pane lobby: lobbyList) {
            updateLobbyElements(lobby);
        }
    }

    /**
     * Get container for the actual lobby given lobby_id
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
        // Set the background to visible and transparent grey'ish
        newLobbyBackground.setVisible(true);
        newLobbyBackground.setStyle("-fx-background-color: rgba(160,160,160,0.4)");

        // Show a new lobby dialog
        newLobbyDialog.setVisible(true);
    }

    /**
     * Will hide the dialog for a new lobby
     */
    public void closeNewLobbyDialog() {
        newLobbyDialog.setVisible(false);
        newLobbyBackground.setVisible(false);
    }

    /**
     * Method that will create a new lobby with a lobby name
     */
    public void createNewLobby() {
        // Change input styling if input is empty
        if (newLobbyNameInput.getText().trim().isEmpty()) {
            LobbyDrawFx.setBorderStyle(newLobbyNameInput, INPUT_COLOR_REQUIRED);
            newLobbyMsg.setVisible(true);
            LobbyDrawFx.setTextColor(newLobbyMsg, INPUT_COLOR_REQUIRED);
        }
        // If input is not empty create new lobby
        else {
            LobbyDrawFx.setBorderStyle(newLobbyNameInput, INPUT_COLOR_NORMAL);
            newLobbyMsg.setVisible(false);
            newLobbyDialog.setVisible(false);
            newLobbyBackground.setVisible(false);

            int lobby_id = -1;

            lobby_id = Handler.getLobbyDAO().newLobby(USERNAME, newLobbyNameInput.getText());

            System.out.println("lobby id" + lobby_id);

            // If user is already in a lobby
            if (current_lobby_id > 0) {

                Handler.getLobbyDAO().removePlayer(USERNAME, current_lobby_id);

                newLobbyNameInput.setText(""); // Reset text
                refresh();
            }
        }
    }

    /**
     * Add player to a lobby
     *
     * @param username
     * @param container Node target, which lobby to join
     */
    private void drawPlayer(String username, Pane container, int lobby_id) {
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
        if (playerReady) {
            readyImg.setImage(new Image("file:res/gui/ready.png"));
        } else {
            readyImg.setImage(new Image("file:res/gui/notReady.png"));
        }
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

        // Check if game should start
        if (current_lobby_id == lobby_id && numOfPlayers > 1 && numOfReady == numOfPlayers) {
            statusValue.setText(STATUS_STARTED);
            LobbyDrawFx.setTextColor(statusValue, PLAYER_COLOR_RED);
            startGame(lobby_id);
        }

        // If the user is in the actual lobby
        if (current_lobby_id == lobby_id) {
            LobbyDrawFx.setBtnStyle(joinBtn, BTN_LEAVE, PLAYER_COLOR_RED);
            statusValue.setText(numOfReady + " / " + numOfPlayers + " ready");
            readyBtn.setDisable(false);
            System.out.println("is user ready? " + READY);

            // Check if user is ready or not and change btn accordingly
            if (READY) {
                LobbyDrawFx.setBtnStyle(readyBtn, BTN_NOT_READY, PLAYER_COLOR_RED);
            } else {
                LobbyDrawFx.setBtnStyle(readyBtn, BTN_READY, PLAYER_COLOR_GREEN);
            }

        } else {
            LobbyDrawFx.setBtnStyle(joinBtn, BTN_JOIN, "#FF9800");
            readyBtn.setDisable(true);
        }

        // Disable join btn if lobby is full
        if (numOfPlayers == 4 && current_lobby_id != lobby_id) {
            joinBtn.setDisable(true);
            statusValue.setText(STATUS_FULL);
            LobbyDrawFx.setTextColor(statusValue, PLAYER_COLOR_RED);
        } else if (current_lobby_id != lobby_id) {
            joinBtn.setDisable(false);
            statusValue.setText(STATUS_OPEN);
            LobbyDrawFx.setTextColor(statusValue, PLAYER_COLOR_GREEN);
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


                // If there are no players left, delete the lobby
                if (numOfPlayers == 1) {
                     Handler.getLobbyDAO().deleteLobby(lobby_id);

                    System.out.println("deleting lobby....");
                }
            }

            refresh();
        });

        // Set logic when player uses the "userReady" button (i.e. sets userReady or not)
        readyBtn.setOnAction(click -> {
             Handler.getLobbyDAO().setReady(lobby_id, USERNAME, readyBtn.getText().equals(BTN_READY));
            refresh();
        });
    }

    private void startGame(int lobby_id) {
//        int game_id = -1;
//        String[] players = null;
//        try {
//            game_id = Handler.getGameDAO().insertGame(lobby_id);
//            //players = Handler.getLobbyDAO().getUsersInLobby(lobby_id).toArray();
//        }
//        catch (SQLException e) { e.printStackTrace(); }
        //Handler.getPlayerDAO().createPlayers(game_id, players);

        Handler.getSceneManager().setScene(ViewConstants.GAME.getValue());
    }

    /**
     * Go back to the dashboard view
     */
    public void leave() {
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
    }
} // SNOOOOP DOOOOG! :O ================~~~~~~~~