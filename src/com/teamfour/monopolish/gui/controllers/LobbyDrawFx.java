package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

class LobbyDrawFx {
    // Used by newLobbyDialog
    static void setBorderStyle(TextField element, String color) {
        element.setStyle(
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-background-color: white;" +
                "-fx-text-inner-color: black;"
        );
    }

    /**
     * Change text color of a text element
     *
     * @param element Target text node
     * @param color New color of the text
     */
    static void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
    }

    /**
     * Change btn style
     *
     * @param btn Target node
     * @param msg Text on the btn
     * @param backgroundColor Of the btn
     */
    static void setBtnStyle(Button btn, String msg, String backgroundColor) {
        btn.setText(msg);
        btn.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13;" +
                "-fx-background-color: " + backgroundColor + ";" +
                "-fx-background-radius: 0;" +
                "-fx-padding: 8 20;"
        );
    }

    static GridPane drawNewLobby(String lobbyName) {
        // Grid layout within the container
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPrefSize(270, 330);
        grid.setMaxSize(270, 330);
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
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
        );
        title.setWrappingWidth(150);
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
        Button joinBtn = new Button();
        setBtnStyle(joinBtn, "Join", "#FF9800");
        joinBtn.setPrefWidth(110);
        joinBtn.setId("join"); // Used to find element

        // "Ready" button in the lobby
        Button readyBtn = new Button();
        setBtnStyle(readyBtn, "Ready", "green");
        readyBtn.setMinWidth(110);
        readyBtn.setId("ready"); // Used to find element
        readyBtn.setDisable(true); // Disable by default

        // Container for playersContainer, fits MAXIMUM 4 playersContainer
        Pane playersContainer = new Pane();
        playersContainer.setPrefSize(220, 35 * 4);
        playersContainer.setId("playerContainer"); // Used to find element
        GridPane.setHalignment(playersContainer, HPos.CENTER);

        // Add elements to grid
        grid.add(title, 0, 0);
        grid.add(playersContainer, 0, 1);
        grid.add(status, 0, 2);
        grid.add(statusValue, 1, 2);
        grid.add(joinBtn, 0, 3);
        grid.add(readyBtn, 1, 3);

        System.out.println(" width " + grid.getWidth());
        return grid;
    }

    /**
     * Will return a player row that can be used in a lobby element
     * Includes a color and the username of the player
     *
     * @param color of the box beside the user
     * @param username of the player
     * @param index where to place the user
     * @return a playerRow inside a GridPane element
     */
    static GridPane drawPlayerRow(String color, String username, int index) {
        GridPane container = new GridPane();
        container.setPrefSize(250, 35);
        container.setMaxSize(250, 35);

        // Setting up columns
        ColumnConstraints colorCol = new ColumnConstraints();
        ColumnConstraints spaceCol = new ColumnConstraints();
        ColumnConstraints userCol = new ColumnConstraints();
        ColumnConstraints imgCol = new ColumnConstraints();
        colorCol.setPrefWidth(35);
        spaceCol.setPrefWidth(30);
        userCol.setPrefWidth(150);
        userCol.setHalignment(HPos.LEFT);
        imgCol.setPrefWidth(25);
        imgCol.setHalignment(HPos.RIGHT);
        container.getColumnConstraints().addAll(colorCol, spaceCol, userCol, imgCol);

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
        container.add(spacing, 1, 0);

        // Username
        Text player = new Text(username);
        player.setStyle("-fx-font-size: 15px;");
        container.add(player, 2,0);

        // Ready or not image
        ImageView img = new ImageView("file:res/gui/notReady.png");
        img.setPreserveRatio(true);
        img.setFitHeight(30);
        img.setId("readyImg");
        container.add(img, 3, 0);

        // Move down according to the index
        container.setTranslateY(35 * index);
        return container;
    }
}
