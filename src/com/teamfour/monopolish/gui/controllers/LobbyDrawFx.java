package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * Class for drawing and modifying some gui elements in the lobby view,
 * separated from the logic of the lobby.
 */

public class LobbyDrawFx {
    // Used by newLobbyDialog
    public static void setBorderStyle(TextField element, String color) {
        element.setStyle(
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-background-color: white;" +
                "-fx-text-inner-color: black;"
        );
    }

    public static void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
    }

    /**
     * Will change between two different styles
     *
     * <ul>
     *     <li>1. Join - user can join lobby - if join is true</li>
     *     <li>2. Leave - user can leave lobby - if join is false</li>
     * </ul>
     *
     * @param btn Button target for style change
     */
    public static void changeBtnStyle(Button btn, String BTN_LEAVE, String BTN_JOIN, boolean join) {
        // If the user is in the actual lobby
        if (!join) {
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


    public static GridPane drawNewLobby(String lobbyName) {
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

        Text statusValue = new Text("OPEN");
        statusValue.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );
        setTextColor(statusValue, "green");
        statusValue.setId("statusValue");

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
        btn.setId("join"); // Used to find element
        GridPane.setHalignment(btn, HPos.CENTER);

        // Container for playersContainer, fits MAXIMUM 4 playersContainer
        Pane playersContainer = new Pane();
        playersContainer.setPrefSize(220, 35 * 4);
        playersContainer.setId("playerContainer"); // Used to find element

        // Add elements to grid
        grid.add(title, 0, 0);
        grid.add(playersContainer, 0, 1);
        grid.add(status, 0, 2);
        grid.add(statusValue, 1, 2);
        grid.add(btn, 0, 3);

        return grid;
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
    public static GridPane drawPlayerRow(String color, String username, int index) {
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
}
