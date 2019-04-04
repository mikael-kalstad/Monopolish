package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.sql.SQLException;
import java.util.ArrayList;

public class TradeController {
    // Trading elements:
    @FXML private FlowPane youroffer, askfor, yourproperties, opponentsproperties;
    @FXML private Button offermoneyok, requestmoneyok, clearyou, clearopponent, canceltrade, proposeTradeBtn;
    @FXML private Label tradeusername, yourtrademoney, requestedtrademoney, invalidinput, invalidinput2;
    @FXML private TextField offeredmoney, requestedmoney;

    // Username of the opponent you want to trade with
    private final String TRADE_USERNAME = Handler.getTradeUsername();

    /**
     * Draws a trading screen, letting you choose what to trade
     */
    @FXML public void initialize() {
        //just some cosmetics
        tradeusername.setText("Trading with: " + TRADE_USERNAME);

        //Lists of the properties available
        ArrayList<Pane> yourCards = new ArrayList<>();
        ArrayList<Pane> opponentsCards = new ArrayList<>();

        //Lists for the cards on offer,
        // because its less work than getting the cards out of the flowPane(they get converted to nodes)
        ArrayList<Pane> offeredCards = new ArrayList<>();
        ArrayList<Pane> requestedCards = new ArrayList<>();

        for (Property property : Handler.getCurrentGame().getEntities().getYou().getProperties()){
            yourCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        for (Property property : Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME).getProperties()){
            opponentsCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        //setting onclick for the cards so they change pane when clicked upon,
        // also adding them to the onoffer Arraylists
        for (Pane card : yourCards) {
            card.setOnMouseClicked(event -> {
                try {
                    youroffer.getChildren().add(card);
                    offeredCards.add(card);
                }
                catch (IllegalArgumentException e) {
                    yourproperties.getChildren().add(card);
                    offeredCards.remove(card);
                }
            });
        }
        for (Pane card : opponentsCards) {
            card.setOnMouseClicked(event -> {
                try {
                    askfor.getChildren().add(card);
                    requestedCards.add(card);
                }
                catch (IllegalArgumentException e) {
                    opponentsproperties.getChildren().add(card);
                    requestedCards.remove(card);
                }
            });
        }

        //setting buttons for clearing all offers
        clearyou.setOnAction(event -> {
            yourtrademoney.setText(null);
            youroffer.getChildren().clear();
            offeredCards.clear();
            yourproperties.getChildren().clear();
            yourproperties.getChildren().addAll(yourCards);
        });
        clearopponent.setOnAction(event -> {
            requestedtrademoney.setText(null);
            askfor.getChildren().clear();
            requestedCards.clear();
            opponentsproperties.getChildren().clear();
            opponentsproperties.getChildren().addAll(opponentsCards);
        });

        //setting buttons for offering money
        offermoneyok.setOnAction(event -> {
            String input = offeredmoney.getText();
            try {
                int check = Integer.parseInt(input);
                if (check > Handler.getCurrentGame().getEntities().getYou().getMoney()) {
                    throw new IllegalArgumentException("Not enough money");
                }
                invalidinput.setVisible(false);
                yourtrademoney.setText(input);
            } catch (NumberFormatException e) {
                invalidinput.setText("Invalid input");
                invalidinput.setVisible(true);
            } catch (IllegalArgumentException e) {
                invalidinput.setText(e.getMessage());
                invalidinput.setVisible(true);
            }
        });
        requestmoneyok.setOnAction(event -> {
            String input = requestedmoney.getText();
            try {
                int check = Integer.parseInt(input);
                if(check > Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME).getMoney()) {
                    throw new IllegalArgumentException("Not enough money");
                }
                invalidinput2.setVisible(false);
                requestedtrademoney.setText(input);
            } catch (NumberFormatException e) {
                invalidinput2.setText("Invalid input");
                invalidinput2.setVisible(true);
            } catch (IllegalArgumentException e) {
                invalidinput2.setText(e.getMessage());
                invalidinput2.setVisible(true);
            }
        });

        //adding properties into panes for showing
        yourproperties.getChildren().addAll(yourCards);
        opponentsproperties.getChildren().addAll(opponentsCards);

        proposeTradeBtn.setOnAction(event -> {
            ArrayList<String> offeredPropertiesNameList = new ArrayList<>();
            ArrayList<String> requestedPropertiesNameList = new ArrayList<>();

            ArrayList<Object> finaloffer = new ArrayList<>();

            for (Pane p : offeredCards) {
                offeredPropertiesNameList.add(p.getId());
            }
            for (Pane p : requestedCards) {
                requestedPropertiesNameList.add(p.getId());
            }

            int offeredmoney = Integer.parseInt(yourtrademoney.getText());
            int requestedmoney = Integer.parseInt(requestedtrademoney.getText());

            //These results have to be sent to/through the database to be shown on the recieving player's screen
            //Call a method that takes these variables and sends them to the database?

            ArrayList<Property> offeredProperties = new ArrayList<>();
            ArrayList<Property> requestedProperties = new ArrayList<>();

            ArrayList<Property> offeredPropertiesNew = new ArrayList<>();
            ArrayList<Property> requestedPropertiesNew = new ArrayList<>();

            offeredProperties.addAll(Handler.getCurrentGame().getEntities().getYou().getProperties());
            requestedProperties.addAll(Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME).getProperties());


            int index = 0;
            for (Property p : offeredProperties){
                System.out.println("offered props");
                if (offeredProperties.get(index).getName().equalsIgnoreCase(offeredPropertiesNameList.get(index))) {
                    offeredPropertiesNew.add(offeredProperties.get(index));
                    System.out.println("Adding prop at: "+index);
                    index++;
                }
            }
            int index2 = 0;
            for (Property p : requestedProperties) {
                System.out.println("requested props");
                if (requestedProperties.get(index2).getName().equalsIgnoreCase(requestedPropertiesNameList.get(index2))) {
                    requestedPropertiesNew.add(offeredProperties.get(index2));
                    System.out.println("Adding prop at: " + index2);
                    index2++;
                }
            }

            // add offered properties:
            System.out.println("sending trade to entitymanager............");
            Handler.getCurrentGame().getEntities().tradeFromTo(Handler.getCurrentGame().getEntities().getYou(),
                    Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME), offeredmoney, offeredPropertiesNew);

            // add requested properties
            Handler.getCurrentGame().getEntities().tradeFromTo(Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME),
                    Handler.getCurrentGame().getEntities().getYou(), requestedmoney, requestedPropertiesNew);


            ArrayList<String[]> propId = new ArrayList<>();
            System.out.println("getting trade form entitymanager.......");

            //propId.addAll(Handler.getGameLogic().getEntityManager().getTrade(Handler.getGameLogic().getYourPlayer()));

            propId.addAll(Handler.getCurrentGame().getEntities().getTrade(Handler.getCurrentGame().getEntities().getYou()));

            ArrayList<Property> property = new ArrayList<>();
            //Property p = new Property();
/*
            int index3 = 0;
            for (int[] i : propId){
                System.out.println("offered props");
                if (offeredProperties.get(index3).getName().equalsIgnoreCase(offeredPropertiesNameList.get(index3))) {
                    offeredPropertiesNew.add(offeredProperties.get(index3));
                    System.out.println("Adding prop at: "+index3);
                    index3++;
                    System.out.println("hei");
                }
            }
            int index4 = 0;
            for (Property p : requestedProperties) {
                System.out.println("requested props");
                if (requestedProperties.get(index4).getName().equalsIgnoreCase(requestedPropertiesNameList.get(index4))) {
                    requestedPropertiesNew.add(offeredProperties.get(index4));
                    System.out.println("Adding prop at: " + index4);
                    index4++;
                }
            }
*//*
            for (int i = 0; i < propId.size(); i++) {
                int seller = propId.get(i)[0];
                int buyer = propId.get(i)[1];
                int price = propId.get(i)[2];
                int prop = propId.get(i)[3];

                //Handler.getGameLogic().getEntityManager().

            }*/

            System.out.println("Accepting trade............");
            Handler.getCurrentGame().getEntities().acceptTrade(Handler.getCurrentGame().getEntities().getYou(),
                    Handler.getCurrentGame().getEntities().getPlayer(TRADE_USERNAME));

            Handler.getCurrentGame().getEntities().doTrade(Handler.getCurrentGame().getEntities().getYou(),
                    Handler.getCurrentGame().getEntities().getPlayer(Handler.getTradeUsername()), offeredmoney, offeredPropertiesNew);
            System.out.println("Trade done.......");
            try {
                Handler.getCurrentGame().getEntities().updateFromDatabase();
            } catch (SQLException sql) {
                sql.printStackTrace();
            }

        });

        canceltrade.setOnAction(event -> {
            Pane container = Handler.getTradeContainer();
            if (container != null) container.setVisible(false);
        });
    }
}
