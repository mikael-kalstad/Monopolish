package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class ChanceCardController {
    @FXML static private Pane container;

    public static void setup(Pane container) {
        ChanceCardController.container = container;
    }

    /**
     * Display a chance card in the container specified in setup.
     *
     * @param msgValue What should the msg on the card say (backside)
     */
    public static void display(String msgValue) {
        ChanceCardController.display(msgValue, null);
    }

    /**
     * Display a chance card in the container specified in setup.
     *
     * @param msgValue What should the msg on the card say (backside)
     * @param logoPath Logo that will be displayed under msg
     */
    public static void display(String msgValue, String logoPath) {
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
        msg.setText(msgValue);

        ImageView logo = (ImageView) backside.getChildren().get(1);

        if (logoPath != null && !logoPath.equals("")) {
            logo.setImage(new Image(logoPath));
        }

        // Turn card when it is clicked
        Pane finalCard = card;
        card.setOnMouseClicked(e -> animateTurn(finalCard, backside));
    }

    /**
     * Animate a card flip.
     *
     * @param card Target node / card container
     * @param backside Target node that works as a backside inside card
     */
    private static void animateTurn(Pane card, Pane backside) {
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
    }
}
