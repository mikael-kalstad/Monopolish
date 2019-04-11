package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

/**
 * Controller class for Send view.
 * @author Bård Hestmark
 * @version 1.1
 */

public class SendController {
    @FXML private Button sendBtn, moneyokBtn, clearBtn, cancelSendBtn;
    @FXML private FlowPane yourproperties, propertiestosend;
    @FXML private Label invalidinput, sendToUsername, moneytosend;
    @FXML private TextField money;

    private final String SEND_USERNAME = Handler.getSendUsername();
    private final String YOU = Handler.getCurrentGame().getEntities().getYou().getUsername();
    private final EntityManager entity = Handler.getCurrentGame().getEntities();

    @FXML public void initialize() {
        ArrayList<Pane> yourCards = new ArrayList<>();
        ArrayList<Pane> cardsToSend = new ArrayList<>();

        //Setting label text
        sendToUsername.setText("Sending to: " + SEND_USERNAME);

        //Generate property cards
        for (Property property : entity.getYou().getProperties()) {
            yourCards.add(GameControllerDrawFx.createPropertyCard(property));
        }

        //adding properties to correct pane on startup
        yourproperties.getChildren().addAll(yourCards);

        //Set on click for cards so they change pane when clicked
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

        //Setting clearBtn so that it clears anything put 'on offer'
        clearBtn.setOnAction(event -> {
            moneytosend.setText(" ");
            propertiestosend.getChildren().clear();
            cardsToSend.clear();
            yourproperties.getChildren().clear();
            yourproperties.getChildren().addAll(yourCards);
        });

        //Setting moneyOkBtn to check input and set the moneytosend label
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

        //setting send button to make lists of properties and send properties and money to the selected player
        sendBtn.setOnAction(event -> {

            ArrayList<String> propertiestosendString = new ArrayList<>();
            ArrayList<Property> propertiesToSend = new ArrayList<>();
            int money = Integer.parseInt(moneytosend.getText());

            //getting the propertynames from the cardpanes
            for (Pane p : cardsToSend) {
                propertiestosendString.add(p.getId());
            }

            //finding matching properties from propertynames
            for (String propertyname : propertiestosendString) {
                for (Property property : entity.getYou().getProperties()) {
                    if (propertyname.equals(property.getName())) {
                        propertiesToSend.add(property);
                    }
                }
            }

            //transfering money
            entity.transferMoneyFromTo(YOU, SEND_USERNAME, money);

            //changing owner of properties
            for (Property property : propertiesToSend) {
                property.setOwner(SEND_USERNAME);
            }

            //show conformation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Transfer successful");
            alert.showAndWait();

            //close/hide window when
            Pane container = Handler.getSendContainer();
            if (container != null) container.setVisible(false);
        });

        //setting cancel button to close/hide the window
        cancelSendBtn.setOnAction(event -> {
            Pane container = Handler.getSendContainer();
            if (container != null) container.setVisible(false);
        });
    }
}