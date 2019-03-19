package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller class for lobby view,
 * handles all the logic for the lobbyList.
 * Note: some gui elements are created in LobbyDrawFx class.
 *
 * @author Mikael Kalstad
 * @version 1.3
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

    //  Status msg constants
    private final String STATUS_OPEN = "OPEN";
    private final String STATUS_FULL = "LOBBY FULL";
    private final String STATUS_STARTED = "IN GAME";

    // Btn msg constants
    private final String BTN_LEAVE = "Leave";
    private final String BTN_JOIN = "Join";

    // Player colors
    private final String PLAYER_COLOR_BLUE = "#03A9F4";
    private final String PLAYER_COLOR_RED = "#ef5350";
    private final String PLAYER_COLOR_GREEN = "#66BB6A";
    private final String PLAYER_COLOR_YELLOW = "#FFF176";

    // Input colors
    private final String INPUT_COLOR_NORMAL = "white";
    private final String INPUT_COLOR_REQUIRED = "orange";
    private final String INPUT_COLOR_WARNING = "red";

    // Ids for gui elements
    private final String PLAYER_CONTAINER_ID = "playerContainer";
    private final String BUTTON_JOIN_ID = "join";
    private final String STATUS_VALUE_ID = "statusValue";

    @FXML public void initialize() {
        ArrayList<String[]> lobbyInfo = new ArrayList<>();
        try {
            lobbyInfo = Handler.getLobbyDAO().getAllLobbies();
        }
        catch (SQLException e) { e.printStackTrace(); }

        for (String[] data : lobbyInfo) {
            Pane container;
            int lobby_id = Integer.valueOf(data[0]);
            String username = data[1];
            boolean ready = Boolean.valueOf(data[2]);

            // Add player to lobby if lobby already exists
            if ((container = getLobbyContainer(lobby_id)) != null) {
                addPlayer(username, container, lobby_id);
            }
            // Create a new lobby and add player
            else {
                container = LobbyDrawFx.drawNewLobby(data[1]);
                addLobby(container, String.valueOf(lobby_id));
                addPlayer(username, container, lobby_id);
            }

            if (username.equals(this.USERNAME))
                current_lobby_id = lobby_id;
        }
    }

    /**
     * This method will take care of everything when making a new lobby
     * <br/><br/>
     * Note: lobby container is the gui container element of a lobby
     * <br/><br/>
     * <b>It will do the following:</b>
     * <br/>
     * <ul>
     *     <li>1. Set the a lobby_id to the lobby container</li>
     *     <li>2. Set on action for join button in lobby container</li>
     *     <li>3. Add lobby to a lobby list</li>
     *     <li>4. Add lobby container to the lobby view</li>
     * </ul>
     *
     * @param lobbyContainer the lobby that will be added
     */
    private void addLobby(Pane lobbyContainer, String lobby_id) {
        lobbyContainer.setId(lobby_id);
        setBtnOnAction(lobbyContainer);
        lobbyList.add(lobbyContainer);
        lobbiesContainer.getChildren().add(lobbyContainer);
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

            try {
                lobby_id = Handler.getLobbyDAO().newLobby(USERNAME);
            }
            catch (SQLException e) { e.printStackTrace(); }
            System.out.println("lobby id" + lobby_id);

            // Add new lobby if lobby is registered to database successfully
            if (lobby_id > 0) {
                Pane lobby = LobbyDrawFx.drawNewLobby(newLobbyNameInput.getText());
                addLobby(lobby, String.valueOf(lobby_id));
                addPlayer(USERNAME, lobby, lobby_id);

                // If user is already in a lobby
                if (current_lobby_id > 0) {
                    removePlayer(getLobbyContainer(current_lobby_id), current_lobby_id, 0); // CHAAAAAAAAAANGE THIS!
                }
            }
            newLobbyNameInput.setText(""); // Reset text
        }
    }

    private void startGame(int lobby_id, String[] users) {
        int game_id = 0;
        try {
            game_id = Handler.getGameDAO().insertGame(lobby_id);
        }
        catch (SQLException e) { e.printStackTrace(); }
        Handler.getPlayerDAO().createPlayers(game_id, users);
    }

    /**
     * Add player to a lobby
     *
     * @param username
     * @param container Node target, which lobby to join
     */
    private void addPlayer(String username, Pane container, int lobby_id) {
        // Lobby id should be larger than 0
        if (lobby_id < 0) return;

        boolean exists = false;
        try {
            if (Handler.getLobbyDAO().getUsersInLobby(lobby_id).indexOf(username) != -1) {
                exists = true;
            }
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Change color based on index
        String color = PLAYER_COLOR_BLUE; // Default if numOfPlayers = 0

        Pane playerContainer = getContainerById(container, PLAYER_CONTAINER_ID);
        int numOfPlayers = 0;
        if (playerContainer != null)
            numOfPlayers = playerContainer.getChildren().size();

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

        // If player does not exists make a new player
        if (!exists) {
            // Try to register player to the lobby
            try {
                Handler.getLobbyDAO().addPlayer(username, lobby_id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Render player in container
        container.getChildren().add(LobbyDrawFx.drawPlayerRow(color, username, numOfPlayers));
    }

    /**
     * Remove player from a lobby
     *
     * @param container Node target, which lobby to remove player from
     * @param index player position in lobby
     */
    private void removePlayer(Pane container, int lobby_id, int index) {
        Pane playerContainer = getContainerById(container, PLAYER_CONTAINER_ID);

        try {
            boolean res = Handler.getLobbyDAO().removePlayer(USERNAME, lobby_id);
            if (res && playerContainer != null)
                playerContainer.getChildren().remove(index);
        }
        catch (SQLException e) { e.printStackTrace(); }

        // If lobby is empty, remove it
        if (container.getChildren().size() == 0)
            lobbiesContainer.getChildren().remove(container);
    }

    private void setBtnOnAction(Pane container) {
        // Find the player container and the button
        Pane playersContainer = getContainerById(container, PLAYER_CONTAINER_ID);
        Button btn = getBtnById(container, BUTTON_JOIN_ID);
        Text statusValue = getTextById(container, STATUS_VALUE_ID);

        // If any of these elements are null return to avoid nullpointerexception
        if (btn == null || playersContainer == null || statusValue == null) return;

        int lobby_id = Integer.valueOf(container.getId());

        if (current_lobby_id == lobby_id) {
            LobbyDrawFx.changeBtnStyle(btn, BTN_LEAVE, BTN_JOIN);
        }

        //Set logic when player uses button (i.e. joins or leaves the lobby)
        btn.setOnAction(click -> {
            System.out.println("Button click!");
            int numOfPlayers = playersContainer.getChildren().size();

            // If user is in the actual lobby
            if (current_lobby_id == lobby_id) {
                LobbyDrawFx.changeBtnStyle(btn, BTN_LEAVE, BTN_JOIN);
                removePlayer(playersContainer, lobby_id, numOfPlayers-1);
            }

            LobbyDrawFx.changeBtnStyle(btn, BTN_LEAVE, BTN_JOIN);

            // Disable join btn if lobby is full
            if (numOfPlayers == 4 && current_lobby_id != lobby_id) {
                btn.setDisable(true);
                statusValue.setText(STATUS_FULL);
                LobbyDrawFx.setTextColor(statusValue, PLAYER_COLOR_RED);
            } else {
                btn.setDisable(false);
                statusValue.setText(STATUS_OPEN);
                LobbyDrawFx.setTextColor(statusValue, "white");
            }
        });
    }

    /**
     * Go back to the dashboard view
     */
    public void leave() {
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
    }
}