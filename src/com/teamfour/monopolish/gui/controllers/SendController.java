package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class SendController {

    @FXML private Button sendBtn, moneyokBtn, clearBtn, cancelSendBtn;
    @FXML private FlowPane yourproperties, propertiestosend;
    @FXML private Label invalidinput, sendToUsername, moneytosend;
    @FXML private TextField money;

    private final String SEND_USERNAME = Handler.getTradeUsername();
    private final String YOU = Handler.getCurrentGame().getEntities().getYou().getUsername();
    private final EntityManager entity = Handler.getCurrentGame().getEntities();

    @FXML
    public void initialize() {

        ArrayList<Pane> yourCards = new ArrayList<>();
        ArrayList<Pane> cardsToSend = new ArrayList<>();


        sendToUsername.setText("Sending to: " + SEND_USERNAME);

        for (Property property : entity.getYou().getProperties()) {
            yourCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        for (Pane card : yourCards) {
            card.setOnMouseClicked(event -> {
                try {
                    propertiestosend.getChildren().add(card);
                    cardsToSend.add(card);
                } catch (IllegalArgumentException e) {
                    yourproperties.getChildren().add(card);
                    cardsToSend.remove(card);
                }
            });
        }

        clearBtn.setOnAction(event -> {
            moneytosend.setText(" ");
            propertiestosend.getChildren().clear();
            cardsToSend.clear();
            yourproperties.getChildren().clear();
            yourproperties.getChildren().addAll(yourCards);
        });

        moneyokBtn.setOnAction(event -> {
            String input = money.getText();
            try {
                int check = Integer.parseInt(input);
                if (check > entity.getYou().getMoney()) {
                    throw new IllegalArgumentException("Not enough money");
                }
                invalidinput.setVisible(false);
                moneytosend.setText(input);
            } catch (NumberFormatException e) {
                invalidinput.setText("Invalid input");
                invalidinput.setVisible(true);
            } catch (IllegalArgumentException e) {
                invalidinput.setText(e.getMessage());
                invalidinput.setVisible(true);
            }
        });

        yourproperties.getChildren().addAll(yourCards);

        sendBtn.setOnAction(event -> {

            ArrayList<String> propertiestosendString = new ArrayList<>();
            ArrayList<Property> propertiesToSend = new ArrayList<>();
            int money = Integer.parseInt(moneytosend.getText());


            for (Pane p : cardsToSend) {
                propertiestosendString.add(p.getId());
            }

            for (String propertyname : propertiestosendString) {
                for (Property property : entity.getYou().getProperties()) {
                    if (propertyname.equals(property.getName())) {
                        propertiesToSend.add(property);
                    }
                }
            }

            entity.transferMoneyFromTo(YOU, SEND_USERNAME, money);

            for (Property property : propertiesToSend) {
                property.setOwner(SEND_USERNAME);
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Transfer successful");
            alert.showAndWait();

            Pane container = Handler.getTradeContainer();
            if (container != null) container.setVisible(false);
        });

        cancelSendBtn.setOnAction(event -> {
            Pane container = Handler.getTradeContainer();
            if (container != null) container.setVisible(false);
        });
    }
}