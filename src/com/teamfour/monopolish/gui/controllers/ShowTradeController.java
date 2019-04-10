package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.entities.PlayerDAO;
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
    private final String YOU = Handler.getCurrentGame().getEntities().getYou().getUsername();
    private final String TRADE_USERNAME = Handler.getTradeUsername();

    PlayerDAO playerDAO = new PlayerDAO();


    @FXML public void initialize() {
        /*
        try {
            entityManager.updateFromDatabase();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }*/

        //ArrayList<Property> props = Handler;
        ArrayList<String[]> props = new ArrayList<>();

        props.addAll(playerDAO.getTrade(YOU));
        props.trimToSize();
        System.out.println(props.toString());


        ArrayList<Property> offeredProperties = new ArrayList<>();
        ArrayList<Property> requestedProperties = new ArrayList<>();

        ArrayList<Property> offeredPropertiesNew = new ArrayList<>();
        ArrayList<Property> requestedPropertiesNew = new ArrayList<>();

        offeredProperties.addAll(entityManager.getYou().getProperties());
        requestedProperties.addAll(entityManager.getPlayer(TRADE_USERNAME).getProperties());


        offeredProperties.trimToSize();
        requestedProperties.trimToSize();


        ArrayList<Property> test = new ArrayList<>();



        int index =0;
        int index2 = 0;

        String sellerPrice = "";
        String buyerPrice = "";


        if (props.isEmpty()) {
            System.out.println("props is empty");
        } else if (props.get(0)[0].isEmpty()) {
            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEeEE");
        } else {
            for (int i = 0; i < props.size(); i++) {
                String seller = props.get(i)[0];
                String  buyer = props.get(i)[1];
                sellerPrice = props.get(i)[2];
                buyerPrice = props.get(i)[3];
                String propId = props.get(i)[4];
                //Handler.getCurrentGame().getEntities().getYou().ge
                System.out.println("Seller: "+seller);
                if (entityManager.getPlayer(seller).getUsername().equalsIgnoreCase(YOU)) {

                    System.out.println("Equals......seller......");
                    offeredPropertiesNew.add(offeredProperties.get(i));

                } else if (entityManager.getPlayer(buyer).getUsername().equalsIgnoreCase(YOU)) {

                    System.out.println("Equals .........buyer........");
                    requestedPropertiesNew.add(requestedProperties.get(i));
                    //Handler.getCurrentGame().getEntities().;


                }
            }
            offeredPropertiesNew.trimToSize();
            requestedPropertiesNew.trimToSize();
        }
        int sPrice = Integer.parseInt(sellerPrice);
        int bPrice = Integer.parseInt(buyerPrice);

        showTrade(TRADE_USERNAME, offeredPropertiesNew, requestedPropertiesNew, sPrice, bPrice);
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
            //entityManager.acceptTrade(entityManager.getPlayer(playername), entityManager.getPlayer(TRADE_USERNAME));

            entityManager.doTrade(entityManager.getYou(), entityManager.getPlayer(TRADE_USERNAME), offeredMoney, offeredproperties);

            entityManager.removeTrade(entityManager.getYou().getUsername());

            // Close dialog
            Handler.getTradeContainer().setVisible(false);
        });

        refuseTradeBtn.setOnAction(e -> {
            // delete trades
            System.out.println("Trade refused!");
            entityManager.removeTrade(entityManager.getYou().getUsername());

            // Close dialog
            Handler.getTradeContainer().setVisible(false);
        });
    }

}
