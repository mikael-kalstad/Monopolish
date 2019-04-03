package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.chanceCards.ChanceCard;
import com.teamfour.monopolish.game.chanceCards.ChanceCardBank;
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
                if (cardInfo instanceof ChanceCardBank) {
                    ((ChanceCardBank) cardInfo).moneyTransaction();
                }
//                else if (cardInfo instanceof ChanceCardPlayers) {
//                    ((ChanceCardPlayers) cardInfo).moneyTransaction();
//                }
            }
            unOpened = false;
        });
    }
}
