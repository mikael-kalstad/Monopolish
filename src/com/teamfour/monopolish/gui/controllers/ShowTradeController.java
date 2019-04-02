package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class ShowTradeController {

    //on proposing trade to another player
    @FXML private FlowPane playersOffer, askingFor;
    @FXML private Label playerisproposing, PlayerIsOfferingLabel, offeredMoneyLabel, requestedMoneyLabel;
    @FXML private Button acceptTradeBtn, refuseTradeBtn;

    public void showTrade(String playername, ArrayList<Property> offeredproperties, ArrayList<Property> requestedproperties,
                          int offeredMoney, int requestedMoney) {

        playerisproposing.setText(playername + " is proposing a trade");
        PlayerIsOfferingLabel.setText(playername + " is offering:");

        ArrayList<Pane> offeredCards = new ArrayList<>();
        ArrayList<Pane> requestedCards = new ArrayList<>();

        for (Property property : offeredproperties){
            offeredCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        for (Property property : requestedproperties){
            requestedCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        playersOffer.getChildren().addAll(offeredCards);
        askingFor.getChildren().addAll(requestedCards);

        offeredMoneyLabel.setText(Integer.toString(offeredMoney));
        requestedMoneyLabel.setText(Integer.toString(requestedMoney));

        acceptTradeBtn.setOnAction(e -> {
            //do the transaction
            //clear stuff
            //send to eventlog "X accepted Y's trade"
        });

        refuseTradeBtn.setOnAction(e -> {
            //close window
            //clear stuff
            //send to eventlog "X refused Y's trade"
        });
    }

}
