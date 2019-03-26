package com.teamfour.monopolish.gui.controllers;

import com.mysql.cj.result.Row;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;



/**
 * Class for drawing players and player position on the board
 *
 * @author Bård Hestmark
 * @version 1.6
 */

public class GameControllerDrawFx extends StackPane {
    // Attributes
    private static int MAX = 9;

    private int tilePosition = 0;
    private int posX, posY;
    private String username;

    public GameControllerDrawFx(String username, int posX, int posY) {
        this.username = username;
        this.posX = posX;
        this.posY = posY;

        Circle player = new Circle(10);
        player.setFill(Color.BLUE);
        player.setStroke(Color.BLUE);

        setAlignment(Pos.CENTER);
        getChildren().addAll(player);
    }

    public static int getMAX() {
        return MAX;
    }

    public static void main(String[] args) {
        GameControllerDrawFx p = new GameControllerDrawFx("k", 9, 9);
        p.move(37);
        System.out.println(p);
    }

    public void move (int steps) {
        while (true) {
            tilePosition++;
            steps--;
            if (tilePosition == MAX*4) {
                tilePosition = 0;
            }
            if (steps == 0) {
                break;
            }
        }
        posToXY(tilePosition);
    }

    public void posToXY(int pos) {
        int position, x, y;
        if (pos > (MAX * 4) - 1 || pos < 0) {
            throw new IllegalArgumentException("Player position out of bounds");
        }
        if (pos >= 0 && pos < MAX) {
            position = 0;
            x = MAX;
            while (true) {
                x--;
                position++;
                if (position == pos) {
                    this.posY = MAX;
                    this.posX = x;
                    break;
                }
            }
        }
        if (pos >= MAX && pos < MAX * 2) {
            position = MAX;
            y = MAX;
            while (true) {
                y--;
                position++;
                if (position == pos) {
                    this.posY = y;
                    this.posX = 0;
                    break;
                }
            }
        }
        if (pos >= MAX * 2 && pos < MAX * 3) {
            position = MAX * 2;
            x = 0;
            while (true) {
                x++;
                position++;
                if (position == pos) {
                    this.posY = 0;
                    this.posX = x;
                    break;
                }
            }
        }
        if (pos >= MAX * 3 && pos < MAX * 4) {
            position = MAX * 3;
            y = 0;
            while(true) {
                y++;
                position++;
                if (position == pos) {
                    this.posY = y;
                    this.posX = MAX;
                    break;
                }
            }
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getUsername() {
        return username;
    }

    public int getTilePosition() {
        return tilePosition;
    }

    public String toString() {
        return "Player moved to X: " + posX + ", Y: " + posY;
    }

    /**
     * This method is used to draw an opponent row in the sidebar in the game view.
     *
     *
     * @param username of the actual user
     * @param color of the player
     * @param moneyValue The value of the money text field
     * @param opponentsContainer Target container for opponents
     * @return A pane that works as a container for the property icon.
     * Must be returned since onclick must be defined in gameController since this class is static.
     */
    public static Pane createOpponentRow(String username, String color, String moneyValue, Pane opponentsContainer) {
        GridPane container = new GridPane();
        container.setPrefSize(530, 90);
        container.setMaxSize(530, 90);
        container.setStyle("-fx-background-color: #ededed;");

        // Setting up columns
        ColumnConstraints colorCol = new ColumnConstraints();
        ColumnConstraints spaceCol = new ColumnConstraints();
        ColumnConstraints userCol = new ColumnConstraints();
        ColumnConstraints propertyCol = new ColumnConstraints();
        ColumnConstraints moneyCol = new ColumnConstraints();

        colorCol.setPrefWidth(80);
        spaceCol.setPrefWidth(30);
        userCol.setPrefWidth(200);
        userCol.setHalignment(HPos.LEFT);
        propertyCol.setPrefWidth(100);
        propertyCol.setHalignment(HPos.CENTER);
        moneyCol.setPrefWidth(120);
        moneyCol.setHalignment(HPos.CENTER);
        container.getColumnConstraints().addAll(colorCol, spaceCol, userCol, propertyCol, moneyCol);

        // The color box
        Pane colorContainer = new Pane();
        colorContainer.setPrefSize(80, 90);
        colorContainer.setStyle("-fx-background-color: " + color + ";");
        container.add(colorContainer, 0, 0);

        // Spacing between color box and username
        Pane spacing = new Pane();container.add(spacing, 1, 0);

        // Set username
        Text user = new Text();
        user.setText(username);
        user.setStyle("-fx-font-size: 18px;");
        user.setFill(Paint.valueOf("#545454"));
        container.add(user, 2,0);

        // Property img
        ImageView img = new ImageView("file:res/gui/house.png");
        img.setFitHeight(50);
        img.setFitWidth(50);
        Pane imgContainer = new Pane(); // Container for img
        imgContainer.setPrefSize(60, 60);
        imgContainer.setMaxSize(60, 60);
        imgContainer.setStyle(
                "-fx-border-color: #888;" +
                "-fx-border-radius: 15;"
        );
        imgContainer.getChildren().add(img);
        imgContainer.setCursor(Cursor.HAND); // Cursor for buttons
        container.add(imgContainer, 3, 0);

        // Money text
        Text money = new Text("$ " + moneyValue);
        money.setStyle("-fx-font-size: 32px;");
        money.setFill(Paint.valueOf("#009e0f"));
        container.add(money, 4, 0);

        // Move opponentRow down accordingly to how many opponents already exists
        int numOfOpponents = opponentsContainer.getChildren().size();
        container.setTranslateY(125 * numOfOpponents);

        // Add opponentRow to the opponentContainer
        opponentsContainer.getChildren().add(container);

        return imgContainer;
    }

    /**
     * Will create a chat-row and add it to a container,
     * @param username of the user who sent the message
     * @param message content of the chat-row
     * @param time when the message was sent
     */
    public static void createChatRow(Pane chatMessageContainer,String username, String message, String time) {
        GridPane container = new GridPane();
        container.setPrefSize(235, 60);

        // Creating row constraints for info- and message section
        RowConstraints infoRow = new RowConstraints();
        infoRow.setPrefHeight(15);
        RowConstraints msgRow = new RowConstraints();
        msgRow.setPrefHeight(45);
        container.getRowConstraints().addAll(infoRow, msgRow);

        // Creating own grid for info section
        GridPane infoGrid = new GridPane();

        // Creating column constraints for info section
        ColumnConstraints usernameCol = new ColumnConstraints();
        usernameCol.setPercentWidth(50);
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPercentWidth(50);
        infoGrid.getColumnConstraints().addAll(usernameCol, timeCol);

        // Adding content to info section
        Text usernameValue = new Text(username);
        Text timeValue = new Text(time);

        // Set text styling
        setInfoTextStyling(usernameValue);
        setInfoTextStyling(timeValue);

        // Add info text to its grid
        infoGrid.add(usernameValue, 0, 0);
        infoGrid.add(timeValue, 1, 0);

        // Add info grid to container
        container.add(infoGrid, 0, 0);

        // Adding content to message section
        Text msg = new Text(message);
        msg.setStyle(
                "-fx-font-size: 12px;"
        );
        msg.setFill(Paint.valueOf("#1a1a1a"));
        container.add(msg, 0, 1);

        // Set background to give good contrast between different messages
        int numOfMessages = chatMessageContainer.getChildren().size();

        // If num of messages is even
        if (numOfMessages % 2 == 0) {
            container.setStyle(
                    "-fx-background-color: #ededed;" +
                    "-fx-padding: 15px;"
            );
        } else {
            container.setStyle(
                    "-fx-background-color: #fff;" +
                    "-fx-padding: 15px;"
            );
        }

        chatMessageContainer.getChildren().add(container);
    }

    /**
     * Helper method for chat row, will set styling for text in info section
     * @param element Target text element
     */
    private static void setInfoTextStyling(Text element) {
        element.setStyle("-fx-font-size: 10px");
        element.setFill(Paint.valueOf("#787878"));
    }
}
