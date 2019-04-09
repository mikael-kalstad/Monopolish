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

    @FXML
    private Label buyHouseLabel, numOfHousesLabel, numOfHotelsLabel, errorLabel;
    @FXML
    private FlowPane buyHouseCardContainer;
    @FXML
    private Button buyHouseBtn, buyHouseCloseBtn, pawnBtn;

    public void initialize() {

        //Making a card to show property info and adding it to container
        Pane card = GameControllerDrawFx.createPropertyCard(PROPERTY);
        buyHouseCardContainer.getChildren().add(card);

        buyHouseLabel.setText(PROPERTY.getName());

        //set closebutton to hide window
        buyHouseCloseBtn.setOnAction(event -> {
            Pane container = Handler.getBuyHouseContainer();
            if (container != null) container.setVisible(false);
        });

        //change some text accordingly to status:
        if (PROPERTY.isPawned())
            pawnBtn.setText("Unpawn");

        //Set the button for pawning properties
        pawnBtn.setOnAction(event -> {

            if (!(PROPERTY.isPawned())) {
                System.out.println("is not pawned");
                if (GameLogic.pawnProperty(PROPERTY)) {
                    System.out.println("Pawned property");
                    pawnBtn.setText("Unpawn");
                }

            } else {
                if (GameLogic.unpawnProperty(PROPERTY)) {
                    System.out.println("Unpawned property");
                    pawnBtn.setText("Pawn");
                } else {
                    errorLabel.setText("Not enough money to unpawn");
                }
            }
        });

        //check for street
        if (PROPERTY instanceof Street) {

            //Setting labels
            numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());
            numOfHotelsLabel.setText("Number of hotels: " + ((Street) PROPERTY).getHotels());

            //you need all streets in a colorcategory to be able to buy houses, and it can't be pawned
            if (game.getEntities().getPlayer(PROPERTY.getOwner()).hasFullSet(Handler.getCurrentGameId(), PROPERTY.getCategorycolor()) && !(PROPERTY.isPawned())) {

                //Unless there is a hotel on the property, in which case we can't buy any more houses, the 'buy' button is enabled
                if (((Street) PROPERTY).getHotels() != 1)
                    buyHouseBtn.setDisable(false);

                //if there are 4 houses the 'buy' button text is set to 'buy hotel'
                if (((Street) PROPERTY).getHouses() == 4)
                    buyHouseBtn.setText("Buy hotel");

                //setting the 'buy' button
                buyHouseBtn.setOnAction(event -> {

                    //if there are 0-3 houses
                    if (((Street) PROPERTY).getHouses() < 4) {

                        //if the transaction goes through and a house is added, number of houses is updated. Otherwise it shows the error label.
                        if (GameLogic.buyHouse((Street) PROPERTY)) {
                            numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());

                            //again if there are four houses after the 'buyHouse' method, the 'buy' button is set to 'hotel'
                            if (((Street) PROPERTY).getHouses() == 4)
                                buyHouseBtn.setText("Buy hotel");

                        } else {
                            errorLabel.setVisible(true);
                        }

                        //if there are 4 houses we want to add a hotel
                    } else if (((Street) PROPERTY).getHouses() == 4) {

                        //if the transaction goes through, we cant buy anything more and the button is disabled
                        if (GameLogic.buyHotel((Street) PROPERTY)) {
                            numOfHotelsLabel.setText("Number of hotels:  " + ((Street) PROPERTY).getHotels());
                            buyHouseBtn.setDisable(true);
                        }

                    } else {
                        errorLabel.setVisible(true);
                    }
                });
            }
        }
    }
}
