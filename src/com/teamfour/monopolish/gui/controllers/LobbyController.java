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

import java.util.ArrayList;

/**
 * Controller class for lobby view
 *
 * @author Mikael Kalstad
 * @version 1.0
 */
public class LobbyController {
    @FXML private FlowPane lobbiesContainer;
    @FXML private Pane newLobbyDialog;
    @FXML private Pane newLobbyBackground;
    @FXML private TextField newLobbyNameInput;
    @FXML private Text newLobbyMsg;

    private ArrayList<Button> btnIds = new ArrayList<>();
    private ArrayList<GridPane> containerIds = new ArrayList<>();

    //  Status msg constants
    private final String STATUS_OPEN = "OPEN";
    private final String STATUS_FULL = "LOBBY FULL";
    private final String STATUS_STARTED = "IN GAME";

    // Player colors
    private final String PLAYER_COLOR_BLUE = "#03A9F4";
    private final String PLAYER_COLOR_RED = "#ef5350";
    private final String PLAYER_COLOR_GREEN = "#66BB6A";
    private final String PLAYER_COLOR_YELLOW = "#FFF176";

    // Input colors
    private final String INPUT_COLOR_NORMAL = "white";
    private final String INPUT_COLOR_REQUIRED = "orange";
    private final String INPUT_COLOR_WARNING = "red";

    @FXML public void initialize() {
        createLobby("Mikael", "myLobby");
        createLobby("giske", "damer");
        createLobby("Mikael", "myLobby");
    }

    public void showNewLobbyDialog() {
        // Set the background to visible and transparent grey'ish
        newLobbyBackground.setVisible(true);
        newLobbyBackground.setStyle("-fx-background-color: rgba(160,160,160,0.4)");

        // Show a new lobby dialog
        newLobbyDialog.setVisible(true);
    }

    public void closeNewLobbyDialog() {
        newLobbyDialog.setVisible(false);
        newLobbyBackground.setVisible(false);
    }

    public void createNewLobby() {
        if (newLobbyNameInput.getText().trim().isEmpty()) {
            setBorderStyle(newLobbyNameInput, INPUT_COLOR_REQUIRED);
            newLobbyMsg.setVisible(true);
            setTextColor(newLobbyMsg, INPUT_COLOR_REQUIRED);
        } else {
            setBorderStyle(newLobbyNameInput, INPUT_COLOR_NORMAL);
            newLobbyMsg.setVisible(false);
            newLobbyDialog.setVisible(false);
            newLobbyBackground.setVisible(false);
            createLobby("MY_USERNAME", newLobbyNameInput.getText());
            newLobbyNameInput.setText(""); // Reset text
        }
    }

    private void setBorderStyle(TextField element, String color) {
        element.setStyle(
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-background-color: white;" +
                "-fx-text-inner-color: black;");
    }

    private void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
    }

    /**
     * Add player to a lobby
     *
     * @param username
     * @param container Node target, which lobby to join
     * @param color of the box beside username
     * @param index where to place the row
     */
    private void addPlayer(String username, Pane container, String color, int index) {
        container.getChildren().add(createPlayerRow(color, username, index));
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
     * Will render a lobby with the player that made it
     *
     * @param username the player who makes the lobby
     */
    private void createLobby(String username, String lobbyName) {
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

        // Container for players, fits MAXIMUM 4 players
        Pane players = new Pane();
        players.setPrefSize(220, 35 * 4);

        // Add player to container
        players.getChildren().add(createPlayerRow(PLAYER_COLOR_BLUE, username, 0));

        // Add elements to grid
        grid.add(title, 0, 0);
        grid.add(players, 0, 1);
        grid.add(statusGrid, 0, 2);
        grid.add(btn, 0, 3);

        // Set logic when player uses button (i.e. join the lobby)
        btn.setId(lobbyName);
        btn.setOnAction(click -> {
            int numOfPlayers = players.getChildren().size();
            // Disable join btn if there are 3 players when a new user join lobby
            if (numOfPlayers == 3) {
                btn.setDisable(true);
                statusValue.setText(STATUS_FULL);
                setTextColor(statusValue, PLAYER_COLOR_RED);
            }

            // Change color based on index
            String color = "blue";
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

            addPlayer("USERNAME_DB", players, color, numOfPlayers);
        });

        // Add button to the lobbies array
        btnIds.add(btn);
        System.out.println(btnIds.toString());

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
