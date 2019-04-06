package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.Game;
import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.chanceCards.*;
import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Controller class for chanceCard view.
 * Will handle action on click and also display animation.
 */
public class ChanceCardController {
    private static boolean unOpened = true;

    /**
     * Display a chance card in the container specified in setup.
     *
     * @param cardInfo ChanceCard object with data/info (msg, logo-path and action method)
     */
    public static void display(ChanceCard cardInfo, Pane container) {
        Pane card = null;
        try {
            card = FXMLLoader.load(MessagePopupController.class.getResource(ViewConstants.FILE_PATH.getValue() + ViewConstants.CHANCE_CARD.getValue()));
        }
        catch (IOException e) { e.printStackTrace(); }

        // Clear and add chance card to container
        container.getChildren().clear();
        container.getChildren().add(card);
        unOpened = true;

        // Set content msg and logo
        if (card == null) return;
        Pane backside = (Pane) card.getChildren().get(3);
        Text msg = (Text) backside.getChildren().get(0);
        msg.setText(cardInfo.getMsg());

        ImageView logo = (ImageView) backside.getChildren().get(1);

        if (cardInfo.getLogoPath() != null && !cardInfo.getLogoPath().equals("")) {
            logo.setImage(new Image(cardInfo.getLogoPath()));
        }

        // Turn card when it is clicked
        Pane finalCard = card;
        card.setOnMouseClicked(e -> animateTurn(finalCard, backside, cardInfo));
    }

    /**
     * Animate a card flip.
     *
     * @param card Target node / card container
     * @param backside Target node that works as a backside inside card
     */
    private static void animateTurn(Pane card, Pane backside, ChanceCard cardInfo) {
        ScaleTransition st2 = new ScaleTransition(Duration.millis(400), card);
        st2.setFromX(0);
        st2.setToX(1);

        ScaleTransition st1 = new ScaleTransition(Duration.millis(400), card);
        st1.setFromX(1);
        st1.setToX(0);

        st1.setOnFinished(e -> {
            if (backside.isVisible()) backside.setVisible(false);
            else backside.setVisible(true);
        });

        SequentialTransition st = new SequentialTransition(st1, st2);
        st.play();
        st.setOnFinished(e -> {
            // Check if card has been opened before
            if (unOpened) {
                // Chance card of type Prank
                if (cardInfo instanceof ChanceCardPrank) {
                    cardInfo.setAmount(GameController.current_money);
                    ((ChanceCardPrank) cardInfo).moneyTransaction();
                }

                // Chance card of type Players
                else if(cardInfo instanceof ChanceCardPlayers) {
                    ((ChanceCardPlayers) cardInfo).setPlayers(Handler.getCurrentGame().getPlayers());
                    ((ChanceCardPlayers) cardInfo).moneyTransaction();
                }

                // Chance card of type Bank
                else if (cardInfo instanceof ChanceCardBank) {
                    ((ChanceCardBank) cardInfo).moneyTransaction();
                }

                // Chance card of type position
                else if (cardInfo instanceof ChanceCardPosition) {
                    ((ChanceCardPosition) cardInfo).moveToPosition();
                }
            } else {
                // Stop playing sound when turning the card
                if (cardInfo instanceof ChanceCardPrank) ChanceCardPrank.stopSound();
            }
            unOpened = false;
        });
    }
}
