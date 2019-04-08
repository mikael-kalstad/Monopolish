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

    @FXML private Label buyHouseLabel, numOfHousesLabel, numOfHotelsLabel, errorLabel;
    @FXML private FlowPane buyHouseCardContainer;
    @FXML private Button buyHouseBtn, buyHouseCloseBtn;

    private final Property PROPERTY = Handler.getBuyHouseProperty();

    public void initialize() {

        if (PROPERTY instanceof Street){

            Pane card = GameControllerDrawFx.createPropertyCard(PROPERTY);
            buyHouseCardContainer.getChildren().add(card);

            buyHouseLabel.setText(PROPERTY.getName());
            numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());
            numOfHotelsLabel.setText("Number of hotels: " + ((Street) PROPERTY).getHotels());

            buyHouseCloseBtn.setOnAction(event -> {
                Pane container = Handler.getBuyHouseContainer();
                if (container != null) container.setVisible(false);
            });

//            if(game.getEntities().getPlayer(PROPERTY.getOwner()).hasFullSet(Handler.getCurrentGameId(), PROPERTY.getCategorycolor())){

                if (((Street) PROPERTY).getHotels() != 1) {
                    buyHouseBtn.setDisable(false);
                }

                if (((Street) PROPERTY).getHouses() == 4) {
                    buyHouseBtn.setText("Buy hotel");
                }

                buyHouseBtn.setOnAction(event -> {

                    if(((Street) PROPERTY).getHouses() < 4) {

                        if (GameLogic.buyHouse((Street) PROPERTY)) {
                            numOfHousesLabel.setText("Number of houses: " + ((Street) PROPERTY).getHouses());
                            if (((Street) PROPERTY).getHouses() == 4) {
                                buyHouseBtn.setText("Buy hotel");
                            }
                        } else {
                            errorLabel.setVisible(true);
                        }

                    } else if(((Street) PROPERTY).getHouses() == 4) {
                        if(GameLogic.buyHotel((Street)PROPERTY)) {
                            numOfHotelsLabel.setText("Number of hotels:  " + ((Street) PROPERTY).getHotels());
                            buyHouseBtn.setDisable(true);
                        }
                    } else {
                        errorLabel.setVisible(true);
                    }

                });
//            }
        }
    }
}
