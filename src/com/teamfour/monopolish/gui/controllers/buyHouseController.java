package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.Street;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import static com.teamfour.monopolish.game.GameLogic.game;

public class buyHouseController {
    private final Property PROPERTY = Handler.getBuyHouseProperty();

    @FXML private Label buyHouseLabel, numOfHousesLabel, numOfHotelsLabel, errorLabel;
    @FXML private FlowPane buyHouseCardContainer;
    @FXML private Button buyHouseBtn, buyHouseCloseBtn, pawnBtn;
    @FXML private Pane overlay;

    public void initialize() {
        //Making a card to show property info and adding it to container
        Pane card = GameControllerDrawFx.createPropertyCard(PROPERTY);
        buyHouseCardContainer.getChildren().add(card);

        buyHouseLabel.setText(PROPERTY.getName());

        // Check if it is your turn
        if (!Handler.getCurrentGame().getPlayers()[Handler.getCurrentGame().getCurrentTurn()].equals(Handler.getAccount().getUsername())) {
            overlay.setVisible(true);
        }

        // Set closebutton to hide window
        buyHouseCloseBtn.setOnAction(event -> {
            Pane container = Handler.getBuyHouseContainer();
            if (container != null) container.setVisible(false);
        });

        //change some text accordingly to status:
        if (PROPERTY.isPawned()) pawnBtn.setText("Unpawn");

        //Set the button for pawning properties
        pawnBtn.setOnAction(event -> {
            if (!PROPERTY.isPawned() && GameLogic.pawnProperty(PROPERTY)) {
                MessagePopupController.show("Your property " + PROPERTY.getName() + " is now pawned", "house.png", "Real estate");
                GameController.refreshBuyHouseDialog(); // Re-render
                GameLogic.updateFromDatabase();
                pawnBtn.setText("Unpawn");
            } else if (PROPERTY.isPawned() && GameLogic.unpawnProperty(PROPERTY)) {
                MessagePopupController.show("Your property " + PROPERTY.getName() + " is now un-pawned", "house.png", "Real estate");
                GameController.refreshBuyHouseDialog(); // Re-render
                GameLogic.updateFromDatabase();
                pawnBtn.setText("Pawn");
            }
            else errorLabel.setText("Not enough money to unpawn");
        });

        // Check if property is of type street
        if (!(PROPERTY instanceof Street)) return;

        // Setting labels
        numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());
        numOfHotelsLabel.setText("Number of hotels: " + ((Street) PROPERTY).getHotels());

        // You need all streets in a colorCategory to be able to buy houses, and it can't be pawned
//        if (game.getEntities().getPlayer(PROPERTY.getOwner()).hasFullSet(Handler.getCurrentGameId(), PROPERTY.getCategorycolor()) && !(PROPERTY.isPawned())) {
           setBuyBtn();

            // Set onclick for the 'buy' button
            buyHouseBtn.setOnAction(event -> {
                // If there are 0-3 houses try to buy a house
                if (((Street) PROPERTY).getHouses() < 4) {
                    if (GameLogic.buyHouse((Street) PROPERTY)) {

                        // If the transaction goes through and a house is added, number of houses is updated. Otherwise it shows the error label.
                        numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());
                        MessagePopupController.show("You bought a house on " + PROPERTY.getName(), "house.png", "Real estate");

                        GameLogic.updateToDatabase();
                        setBuyBtn();
                    }
                    else errorLabel.setVisible(true);
                }

                // If there are 4 houses try to buy a hotel
                else if (((Street) PROPERTY).getHouses() == 4) {
                    if (GameLogic.buyHotel((Street) PROPERTY)) {
                        numOfHotelsLabel.setText("Number of hotels:  " + ((Street) PROPERTY).getHotels());
                        MessagePopupController.show("You bought a hotel on " + PROPERTY.getName(), "house.png", "Real estate");

                        GameLogic.updateToDatabase();
                        setBuyBtn();
                    }
                    else errorLabel.setVisible(true);
                }
            });
//        }
    }

    private void setBuyBtn() {
        // Default values
        buyHouseBtn.setDisable(false);
        buyHouseBtn.setText("Buy house");

        // Unless there is a hotel on the property, in which case we can't buy any more houses, the 'buy' button is enabled
        if (((Street) PROPERTY).getHotels() == 1)
            buyHouseBtn.setDisable(true);

        // if there are 4 houses the 'buy' button text is set to 'buy hotel'
        if (((Street) PROPERTY).getHouses() == 4)
            buyHouseBtn.setText("Buy hotel");
    }
}
