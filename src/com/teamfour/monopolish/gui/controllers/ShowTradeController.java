package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.Game;
import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class ShowTradeController {
    // FXML elements in GUI
    @FXML private FlowPane playersOffer, askingFor;
    @FXML private Label playerisproposing, PlayerIsOfferingLabel, offeredMoneyLabel, requestedMoneyLabel;
    @FXML private Button acceptTradeBtn, refuseTradeBtn;

    // Constants
    private final EntityManager entityManager = Handler.getCurrentGame().getEntities();

    @FXML public void initialize() {
        /*
        1. Get trade data from DAO
        2. Call showTrade method with data
        */
    }

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
            System.out.println("Trade accepted......");
            //do the transaction
            entityManager.acceptTrade(entityManager.getPlayer(playername), entityManager.getPlayer(Handler.getTradeUsername()));

            entityManager.doTrade(entityManager.getYou(), entityManager.getPlayer(Handler.getTradeUsername()), offeredMoney, offeredproperties);

            entityManager.removeTrade(entityManager.getYou().getUsername());

            // Close dialog
            GameController.tradeContainer.setVisible(false);
        });

        refuseTradeBtn.setOnAction(e -> {
            // delete trades
            System.out.println("Trade refused!");
            entityManager.removeTrade(entityManager.getYou().getUsername());

            // Close dialog
            GameController.tradeContainer.setVisible(false);
        });
    }

}
