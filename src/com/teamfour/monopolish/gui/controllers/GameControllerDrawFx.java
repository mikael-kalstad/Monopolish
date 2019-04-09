package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.property.Boat;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.Street;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Class for drawing different GUI elements used in the game view by the gameController.
 *
 * @author Bård Hestmark
 * @version 1.9
 */

public class GameControllerDrawFx {
    /**
     * This method is used to draw a player piece on the board in the game
     *
     * @param boardGrid Target grid for board
     * @param positions Positions of the players
     * @param colors    Color of associated with the player
     */
    public static void createPlayerPieces(GridPane boardGrid, int[] positions, String[] colors) {
        // Clear the board before creating drawing any player pieces
        boardGrid.getChildren().clear();

        ArrayList<Node> playerPieces = new ArrayList<>();

        for (int i = 0; i < positions.length; i++) {
            // Create a player piece and set color
            Circle piece = new Circle(12);
            piece.setFill(Paint.valueOf(colors[i]));
            piece.setStroke(Paint.valueOf(colors[i]));
            piece.setStyle("-fx-margin: 10px; -fx-padding: 10px");

            // Convert position to XY position
            int[] posXY = posToXY(positions[i]);
            if (posXY == null || posXY.length != 2) return; // Error check

            // Set piece in the right column and row and center it
            GridPane.setConstraints(piece, posXY[0], posXY[1]);
            GridPane.setHalignment(piece, HPos.CENTER);
            GridPane.setValignment(piece, VPos.CENTER);

            // Add piece to boardGrid
            boardGrid.getChildren().add(piece);

            // Add piece to a list
            playerPieces.add(piece);

            // Check if other players are on the tile
            checkForOverlaps(playerPieces, posXY[0], posXY[1]);
        }
    }

    /**
     * Helper method that will translate a position to a X and Y position
     * that works on the grid for the board in the game view
     *
     * @param pos Position on the board
     * @return Array with X and Y position [X, Y]
     */
    private static int[] posToXY(int pos) {
        int position, x, y;
        int MAX = 9;

        if (pos > (MAX * 4) - 1 || pos < 0) {
            throw new IllegalArgumentException("Player position out of bounds");
        }

        // Position between free parking and jail
        else if (pos >= MAX * 2 && pos < MAX * 3) {
            position = MAX * 2;
            x = 0;
            while (true) {
                x++;
                position++;
                if (position == pos) {
                    return new int[]{x, 0};
                }
            }
        }

        // Position between visit jail and free parking
        else if (pos >= MAX && pos < MAX * 2) {
            position = MAX;
            y = MAX;
            while (true) {
                y--;
                position++;
                if (position == pos) {
                    return new int[]{0, y};
                }
            }
        }

        // Position between start and visit jail
        if (pos >= 0 && pos < MAX) {
            position = 0;
            x = MAX;
            while (true) {
                x--;
                position++;
                if (position == pos) {
                    return new int[]{x, MAX};
                }
            }
        }

        // Position between jail and start
        if (pos >= MAX * 3 && pos < MAX * 4) {
            position = MAX * 3;
            y = 0;
            while (true) {
                y++;
                position++;
                if (position == pos) {
                    return new int[]{MAX, y};
                }
            }
        }
        return null;
    }

    /**
     * This method checks if there are other game pieces on the same tile. If there are, position
     * these pieces according to each other
     *
     * @param playerPieces List of all player pieces
     * @param tileX        X position of the tile
     * @param tileY        Y position of the tile
     */
    private static void checkForOverlaps(ArrayList<Node> playerPieces, int tileX, int tileY) {
        // List of pieces on this specific tile
        ArrayList<Node> piecesOnTile = new ArrayList<>();

        // Go through all players pieces and check for pieces that are on the same tile
        for (Node p : playerPieces) {
            // Check if X and Y position are the same
            if (tileX == GridPane.getColumnIndex(p) && tileY == GridPane.getRowIndex(p)) {
                piecesOnTile.add(p);
            }
        }

        // Check how many pieces there are on the tile and position them accordingly on the tile
        if (piecesOnTile.size() == 2) {
            GridPane.setHalignment(piecesOnTile.get(0), HPos.LEFT);
            GridPane.setHalignment(piecesOnTile.get(1), HPos.RIGHT);
        }

        if (piecesOnTile.size() == 3) {
            GridPane.setValignment(piecesOnTile.get(0), VPos.TOP);
            GridPane.setValignment(piecesOnTile.get(1), VPos.TOP);
            GridPane.setValignment(piecesOnTile.get(2), VPos.BOTTOM);
        }

        if (piecesOnTile.size() == 4) {
            GridPane.setHalignment(piecesOnTile.get(2), HPos.LEFT);
            GridPane.setHalignment(piecesOnTile.get(3), HPos.RIGHT);
            GridPane.setValignment(piecesOnTile.get(3), VPos.BOTTOM);
        }
    }

    /**
     * This method is used to draw an opponent row in the sidebar in the game view
     *
     * @param username           of the actual user
     * @param color              of the player
     * @param moneyValue         The value of the money text field
     * @param opponentsContainer Target container for opponents
     * @return A pane that works as a container for the property icon.
     * Must be returned since onclick must be defined in gameController since this class is static.
     */
    public static Pane createOpponentRow(String username, String color, String moneyValue, ImageView logo, Pane opponentsContainer) {
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
        moneyCol.setHalignment(HPos.LEFT);
        container.getColumnConstraints().addAll(colorCol, spaceCol, userCol, propertyCol, moneyCol);

        // The color box
        Pane colorContainer = new Pane();
        colorContainer.setPrefSize(80, 90);
        colorContainer.setStyle("-fx-background-color: " + color + ";");
        container.add(colorContainer, 0, 0);

        // Set img if it is assigned
        if (logo == null) {
            colorContainer.getChildren().clear(); // Reset
        } else {
            logo.setFitHeight(colorContainer.getPrefHeight());
            logo.setFitWidth(colorContainer.getPrefWidth());
            colorContainer.getChildren().add(logo);
        }

        // Spacing between color box and username
        Pane spacing = new Pane();
        container.add(spacing, 1, 0);

        // Set username
        Text user = new Text();
        user.setText(username);
        user.setStyle("-fx-font-size: 20px;");
        user.setFill(Paint.valueOf("#545454"));
        container.add(user, 2, 0);

        // Property img
        Pane imgContainer = new Pane(); // Container for img
        imgContainer.setPrefSize(60, 60);
        imgContainer.setMaxSize(60, 60);
        imgContainer.setStyle(
                "-fx-border-color: #888;" +
                        "-fx-border-radius: 15;" +
                        "-fx-cursor: hand;"
        );

        ImageView img = new ImageView("file:res/gui/Game/house.png");
        img.setFitHeight(55);
        img.setFitWidth(55);

        imgContainer.getChildren().add(img);
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
     *
     * @param username of the user who sent the message
     * @param message  content of the chat-row
     * @param time     when the message was sent
     */
    public static void createChatRow(Pane chatMessageContainer, String username, String message, String time) {
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
        timeValue.setTextAlignment(TextAlignment.RIGHT);

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
        msg.setStyle("-fx-font-size: 12px;");
        msg.setWrappingWidth(220);
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
     *
     * @param element Target text element
     */
    private static void setInfoTextStyling(Text element) {
        element.setStyle("-fx-font-size: 10px");
        element.setFill(Paint.valueOf("#787878"));
    }

    /**
     * Method for creating a card of type special.
     * @param title Will display under the logo
     * @param logoPath Path to the logo
     * @param info Optional, will display under the title
     * @param backgroundColor Of the card
     * @return The special card as a Pane
     */
    static Pane createSpecialCard(String title, String logoPath, String info, String backgroundColor) {
        // Get general card from .fxml file
        Pane card = new Pane();
        try {
            card = FXMLLoader.load(GameControllerDrawFx.class.getResource(ViewConstants.FILE_PATH.getValue() + ViewConstants.SPECIAL_CARD.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set background color of the card
        card.setStyle("-fx-background-color: " + backgroundColor + ";");

        // Set title
        Text titleElement = (Text) card.getChildren().get(0);
        titleElement.setText(title);

        // Set logo
        ImageView logoElement = (ImageView) card.getChildren().get(1);
        logoElement.setImage(new Image(logoPath));

        // Set price if defined
        Text infoText = (Text) card.getChildren().get(2);

        if (info != null && !info.equals("")) infoText.setText(info);
        else infoText.setVisible(false);

        return card;
    }

    /**
     * A method to generate a javafx property card
     *
     * @param property A property object
     */
    static Pane createPropertyCard(Property property) {
        Pane card = new AnchorPane();
        card.setId(property.getName());
        Pane header = new AnchorPane();
        Label propertynamelabel = new Label(property.getName());
        TextFlow propertypriceflow = new TextFlow();
        TextFlow prices = new TextFlow();

        card.getChildren().addAll(header, propertypriceflow, prices);

        card.setPrefSize(190, 240);
        header.setPrefSize(190, 50);

        AnchorPane.setTopAnchor(header, 0.0);

        AnchorPane.setTopAnchor(propertypriceflow, 55.0);
        AnchorPane.setLeftAnchor(propertypriceflow, 5.0);
        AnchorPane.setRightAnchor(propertypriceflow, 45.0);
        AnchorPane.setBottomAnchor(propertypriceflow, 5.0);

        AnchorPane.setTopAnchor(prices, 55.0);
        AnchorPane.setLeftAnchor(prices, 145.0);
        AnchorPane.setRightAnchor(prices, 5.0);
        AnchorPane.setBottomAnchor(prices, 5.0);

        String cursor = "default";

        if (property.getOwner().equals(Handler.getAccount().getUsername()))
            cursor = "hand";

        card.setStyle("-fx-background-color: #ffffff; -fx-cursor: " + cursor + ";");
        header.setStyle("-fx-background-color: " + property.getCategorycolor() + ";");
        propertynamelabel.setStyle("-fx-font-size: 18px");
        propertypriceflow.setStyle("-fx-font-size: 13px");
        prices.setStyle("-fx-font-size: 13px");

        header.getChildren().add(propertynamelabel);
        propertynamelabel.setLayoutX(49.0);
        propertynamelabel.setLayoutY(12.0);

        ArrayList<Text> rentlist = new ArrayList<>();
        Text rentContent;

        // If property is a street
        if (property instanceof Street) {
            rentContent = new Text("" +
                    "Rent:" +
                    "\nRent with colorset:" +
                    "\nRent 1 house:" +
                    "\nRent 2 houses:" +
                    "\nRent 3 houses:" +
                    "\nRent 4 houses:" +
                    "\nRent with hotel:" +
                    "\nHouses cost:" +
                    "\nHotels cost:");
        } else if (property instanceof Boat) {
            rentContent = new Text("" +
                    "Rent 1 railroad: " +
                    "\nRent 2 railroads: " +
                    "\nRent 3 railroads: ");
        } else {
            rentContent = new Text("" +
                    "Rent 1 utility: " +
                    "\nRent 2 utilities: ");
        }

        rentlist.add(rentContent);

        propertypriceflow.getChildren().addAll(rentlist);

        ArrayList<Text> pricesTextList = new ArrayList<>();
        for (String i : property.getAllRent()) {
            pricesTextList.add(new Text(i + "\n"));
        }
        prices.getChildren().addAll(pricesTextList);

        // Display pawned overlay on card if it is pawned
        StackPane card2 = new StackPane();
        if (property.isPawned()) {
            ImageView overlay = new ImageView(new Image("file:res/gui/Game/pawned.png"));
            card2.getChildren().addAll(card, overlay);
            return card2;
        }

        return card;
    }

    /**
     * Method to draw houses on a property
     *
     * @param housegrid GridPane on the board for houses
     * @param street    The property the house will be drawn on
     */
    static boolean drawHouse(GridPane housegrid, Street street) {
        int numberOfHouses = street.getHouses();
        String propertyname = street.getName();
        boolean hascontainer = false;

        if (street.getHotels() == 1) {
            numberOfHouses = 5;
        }

        if(numberOfHouses == 0){return false;}

        for (Node box : housegrid.getChildren()) {
            if (box.getId()!= null && box.getId().equals(propertyname)) {
                hascontainer = true;
                if (numberOfHouses == (((Pane) box).getChildren()).size() || (numberOfHouses == 5 && (((Pane) box).getChildren()).size() == 1)){
                    return false;
                }
            }
        }

        int pos = street.getPosition();
        int[] posXY = posToXY(pos);
        ArrayList<ImageView> houses = new ArrayList<>();

        if (numberOfHouses < 5) {
            for (int i = 0; i < numberOfHouses; i++) {
                houses.add(new ImageView("file:res/gui/Game/house.png"));
            }
        }

        //Setting size and rotation of the houses
        for (ImageView house : houses) {
            house.setFitHeight(21);
            house.setFitWidth(21);

            rotateHouse(house, pos);
        }

        //if its the first house being drawn on a street, it will need a container:
        if (!hascontainer) {
            //Checks which of the 4 sides of the board the street is on to correctly align the container in the GridPane grid:
            //Bottom
            if (pos > 0 && pos < 9) {
                return createHouseContainerV(housegrid, propertyname, posXY, houses, Pos.TOP_CENTER);
            }
            //Left side
            if (pos > 9 && pos < 18) {
                return createHouseContainerH(housegrid, propertyname, posXY, houses, Pos.CENTER_RIGHT);
            }
            //Top
            if (pos > 18 && pos < 27) {
                return createHouseContainerV(housegrid, propertyname, posXY, houses, Pos.BOTTOM_CENTER);
            }
            //Right side
            if (pos > 27 && pos < 36) {
                return createHouseContainerH(housegrid, propertyname, posXY, houses, Pos.CENTER_LEFT);
            }
        }

        //for the rest of the houses we find the already made container
        if (hascontainer && numberOfHouses < 5) {
            for (Node box : housegrid.getChildren()) {
                if (box.getId()!= null && box.getId().equals(propertyname)) {
                    ((Pane) box).getChildren().clear();
                    ((Pane) box).getChildren().addAll(houses);
                    return true;
                }
            }
        }

        //if the street has a hotel
        if (hascontainer && numberOfHouses == 5) {
            for (Node box : housegrid.getChildren()) {
                if (box.getId()!= null && box.getId().equals(propertyname)) {
                    ImageView hotel = new ImageView("file:res/gui/Game/house.png");
                    hotel.setFitWidth(32);
                    hotel.setFitHeight(32);
                    rotateHouse(hotel, pos);
                    ((Pane) box).getChildren().clear();
                    ((Pane) box).getChildren().add(hotel);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean createHouseContainerH(GridPane housegrid, String propertyname, int[] posXY, ArrayList<ImageView> houses, Pos centerRight) {
        VBox box = new VBox();
        box.setId(propertyname);
        box.setSpacing(2);
        box.setPrefHeight(65);
        GridPane.setConstraints(box, posXY[0], posXY[1]);
        box.getChildren().addAll(houses);
        box.setAlignment(centerRight);
        housegrid.getChildren().add(box);
        return true;
    }

    private static boolean createHouseContainerV(GridPane housegrid, String propertyname, int[] posXY, ArrayList<ImageView> houses, Pos topCenter) {
        HBox box = new HBox();
        box.setId(propertyname);
        box.setSpacing(2);
        box.setPrefWidth(65);
        GridPane.setConstraints(box, posXY[0], posXY[1]);
        box.setAlignment(topCenter);
        box.getChildren().addAll(houses);
        housegrid.getChildren().add(box);
        return true;
    }

    private static void rotateHouse(ImageView house, int pos) {
        if (pos > 9 && pos < 18) {
            house.setStyle("-fx-rotate: 90;");
        }
        if (pos > 18 && pos < 27) {
            house.setStyle("-fx-rotate: 180;");
        }
        if (pos > 27 && pos < 36) {
            house.setStyle("-fx-rotate: -90;");
        }
    }
}
