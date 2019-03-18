package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller class for lobby view
 *
 * @author Mikael Kalstad
 * @version 1.1
 */
public class LobbyController {
    @FXML private FlowPane lobbiesContainer;
    @FXML private Pane newLobbyDialog;
    @FXML private Pane newLobbyBackground;
    @FXML private TextField newLobbyNameInput;
    @FXML private Text newLobbyMsg;

    /*
    -- SYNTAX --

    [
        [lobby_id, username, ready],
        [lobby_id, username, ready]
    ]
     */
    private String[][] fakeData = {
            {"1234", "Mikael", "0", "true"},
            {"1234", "Bård", "1", "false"},
            {"1235", "Torbjørn", "0","false"},
            {"1235", "Eirik", "1","false"},
            {"1236", "Lisa", "0","false"}
    };

    private ArrayList<Pane> lobbies = new ArrayList<>();
    private String username = Handler.getAccount().getUsername();
    private int current_lobby_id = -1;

    //  Status msg constants
    private final String STATUS_OPEN = "OPEN";
    private final String STATUS_FULL = "LOBBY FULL";
    private final String STATUS_STARTED = "IN GAME";

    // Btn msg constants
    private final String BTN_JOIN = "Join";
    private final String BTN_LEAVE = "Leave";

    // Player colors
    private final String PLAYER_COLOR_BLUE = "#03A9F4";
    private final String PLAYER_COLOR_RED = "#ef5350";
    private final String PLAYER_COLOR_GREEN = "#66BB6A";
    private final String PLAYER_COLOR_YELLOW = "#FFF176";

    // Input colors
    private final String INPUT_COLOR_NORMAL = "white";
    private final String INPUT_COLOR_REQUIRED = "orange";
    private final String INPUT_COLOR_WARNING = "red";

    // Testing purposes
    @FXML public void initialize() {
        // -- FAKEDATA SHOULD BE DATA FROM DATABASE! --

        if (fakeData.length == 0) {
            createLobby("myLobby");
        } else {
            for (String[] data : fakeData) {
                Pane container;
                int lobby_id = Integer.valueOf(data[0]);
                String username = data[1];
                int index = Integer.valueOf(data[2]);
                boolean ready = Boolean.valueOf(data[3]);

                if ((container = getLobbyContainer(lobby_id)) != null) {
                    addPlayer(username, container, lobby_id, index);
                } else {
                    createLobby("myLobby");
                    container = getLobbyContainer(lobby_id);
                    addPlayer(username, container, lobby_id, index);
                }
            }
        }
    }

    private Pane getLobbyContainer(int lobby_id) {
        for (Pane container : lobbies) {
            if (container.getId().equals(String.valueOf(lobby_id))) return container;
        }
        return null;
    }

    // Used by newLobbyDialog
    private void setBorderStyle(TextField element, String color) {
        element.setStyle(
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-background-color: white;" +
                "-fx-text-inner-color: black;");
    }

    /**
     * Will change between two different styles
     * 1. Join - user can join lobby - if join is true
     * 2. Leave - user can leave lobby - if join is false
     * @param btn Button target for style change
     * @param join Should button be a join or leave button
     */
    private void changeBtnStyle(Button btn, boolean join) {
        // If the user is in the actual lobby
        if (join) {
            btn.setText(BTN_LEAVE);
            btn.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 15;" +
                    "-fx-background-color: red; " +
                    "-fx-background-radius: 0;" +
                    "-fx-padding: 8 35;"
            );
        }
        // If the user is not in the actual lobby
        else {
            btn.setText(BTN_JOIN);
            btn.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 15;" +
                    "-fx-background-color: #FF9800; " +
                    "-fx-background-radius: 0;" +
                    "-fx-padding: 8 35;"
            );
        }
    }

    private void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
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
            setBorderStyle(newLobbyNameInput, INPUT_COLOR_REQUIRED);
            newLobbyMsg.setVisible(true);
            setTextColor(newLobbyMsg, INPUT_COLOR_REQUIRED);
        }
        // If input is not empty create new lobby
        else {
            setBorderStyle(newLobbyNameInput, INPUT_COLOR_NORMAL);
            newLobbyMsg.setVisible(false);
            newLobbyDialog.setVisible(false);
            newLobbyBackground.setVisible(false);
            createLobby(newLobbyNameInput.getText());
            newLobbyNameInput.setText(""); // Reset text
        }
    }

    /**
     * Add player to a lobby
     *
     * @param username
     * @param container Node target, which lobby to join
     * @param index where to place the row
     */
    private void addPlayer(String username, Pane container, int lobby_id, int index) {
        // Should be larger than 0
        if (lobby_id < 0) return;

        // Change color based on index
        String color = PLAYER_COLOR_BLUE;

        switch (index) {
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

        try {
            // Try to register player to the lobby, and add player in GUI if response is okay.
            boolean res = Handler.getLobbyDAO().addPlayer(username, lobby_id);
            if (res) {
                container.getChildren().add(createPlayerRow(color, username, index));
                Handler.getPlayerDAO().createPlayer(lobby_id, username);
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Remove player from a lobby
     *
     * @param container Node target, which lobby to remove player from
     * @param index player position in lobby
     */
    private void removePlayer(Pane container, int lobby_id, int index) {

        try {
            boolean res = Handler.getLobbyDAO().removePlayer(username, lobby_id);
            if (res) {
                container.getChildren().remove(index);
                Handler.getPlayerDAO().removePlayer(lobby_id, username);
            }
        }

        catch (SQLException e) { e.printStackTrace(); }

    }

    /**
     * Will return a player row that can be used in a lobby element
     * Includes a color and the username of the player
     *
     * @param color of the box beside the user
     * @param username
     * @param index where to place the user
     * @return a playerRow inside a GridPane element
     */
    private GridPane createPlayerRow(String color, String username, int index) {
        GridPane container = new GridPane();
        container.setPrefSize(220, 35);
        container.setMaxSize(220, 35);

        String backgroundColor = "#EEEEEE";
        if (index % 2 != 0) backgroundColor = "white";

        container.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                "-fx-effect: dropshadow(three-pass-box, derive(lightgrey, -20%), 10, 0, 4, 4);"
        );

        // The color box
        Pane colorContainer = new Pane();
        colorContainer.setPrefSize(35, 35);
        colorContainer.setStyle("-fx-background-color: " + color + ";");
        container.add(colorContainer, 0, 0);

        // Spacing between color box and username
        Pane spacing = new Pane();
        spacing.setPrefWidth(30);
        container.add(spacing, 1, 0);

        // Username
        Text player = new Text(username);
        player.setStyle("-fx-font-size: 15px;");
        container.add(player, 2,0);

        // Move down according to the index
        container.setTranslateY(35 * index);

        return container;
    }

    /**
     * Will create a lobby and add the player that made it to the lobby
     */
    private void createLobby(String lobbyName) {
        // Grid layout within the container
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWidth(290);
        grid.setPrefHeight(330);
        grid.setVgap(10);
        grid.setStyle(
            "-fx-background-color: #e5e5e8;" +
            "-fx-padding: 20;"
        );

        // Row layout for grid
        RowConstraints titleRow = new RowConstraints(75);
        RowConstraints playersRow = new RowConstraints(155);
        RowConstraints statusRow = new RowConstraints(50);
        RowConstraints btnRow = new RowConstraints(65);
        grid.getRowConstraints().addAll(titleRow, playersRow, statusRow, btnRow);

        // Title / lobby name
        Text title = new Text(lobbyName);
        title.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold"
        );
        GridPane.setHalignment(title, HPos.CENTER); // Center horizontally

        // Status of the lobby
        Text status = new Text("Status");
        status.setStyle(
                "-fx-font-size: 17px; " +
                "-fx-font-weight: bold;"
        );

        Text statusValue = new Text(STATUS_OPEN);
        statusValue.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );
        setTextColor(statusValue, PLAYER_COLOR_GREEN);

        // Status container
        GridPane statusGrid = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        statusGrid.getColumnConstraints().addAll(col1, col2);

        statusGrid.add(status, 0, 0);
        statusGrid.add(statusValue, 1, 0);

        // "Join" button in the lobby
        Button btn = new Button("Join");
        btn.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 15;" +
                "-fx-background-color: #FF9800; " +
                "-fx-background-radius: 0;" +
                "-fx-padding: 8 35;"
        );
        GridPane.setHalignment(btn, HPos.CENTER);

        // Container for playersContainer, fits MAXIMUM 4 playersContainer
        Pane playersContainer = new Pane();
        playersContainer.setPrefSize(220, 35 * 4);

        // Add elements to grid
        grid.add(title, 0, 0);
        grid.add(playersContainer, 0, 1);
        grid.add(statusGrid, 0, 2);
        grid.add(btn, 0, 3);

        // Id of the current lobby
        int lobby_id = -1;

        try {
            // Make a new lobby in the database
            lobby_id = Handler.getLobbyDAO().newLobby(username);

            // Set id of container to the value of lobby_id
            playersContainer.setId(String.valueOf(lobby_id));

            // Add btn to the lobby arraylist
            lobbies.add(playersContainer);
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Set logic when player uses button (i.e. joins or leaves the lobby)
        int finalLobby_id = lobby_id;
        btn.setOnAction(click -> {
            int numOfPlayers = playersContainer.getChildren().size();

            // If user is in the actual lobby
            if (current_lobby_id == finalLobby_id) {
                changeBtnStyle(btn, false);
                removePlayer(playersContainer, finalLobby_id, numOfPlayers);
            }

            // If the user is not in the actual lobby
            else {
                changeBtnStyle(btn, true);
                addPlayer(Handler.getAccount().getUsername(), playersContainer, finalLobby_id, numOfPlayers);
            }

            // Disable join btn if lobby is full
            if (numOfPlayers == 4 && current_lobby_id != finalLobby_id) {
                btn.setDisable(true);
                statusValue.setText(STATUS_FULL);
                setTextColor(statusValue, PLAYER_COLOR_RED);
            } else {
                btn.setDisable(false);
                statusValue.setText(STATUS_OPEN);
                setTextColor(statusValue, "white");
            }
        });

        // Add grid to the lobby view
        lobbiesContainer.getChildren().add(grid);
    }

    /**
     * Go back to the dashboard view
     */
    public void leave() {
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
    }
}
