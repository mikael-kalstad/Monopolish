package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;
import com.teamfour.monopolish.gui.controllers.MessagePopupController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Class for chance cards of special type prank
 */
public class ChanceCardPrank extends ChanceCard {
    private static MediaPlayer player;

    ChanceCardPrank(String msg, String logoPath) {
        super(msg, logoPath, 0);
    }

    public void setAmount(int amount) {
        super.setAmount(amount);
    }

    public static void stopSound() {
        player.stop();
    }

    /**
     * Transfer money from the bank to a player or from the player to the bank.
     */
    public void moneyTransaction() {
        // Play rick roll song
        Handler.playSound("res/sounds/prank.mp3");

        // Show song credits
        MessagePopupController.show("Now playing: Never gonna give you up, by Rick Astley", "music.png", "Local Disk Jockey");

        // Take all the money to the player
        Handler.getCurrentGame().getEntities().transferMoneyFromBank(super.getUSERNAME(), -super.getAmount());
    }
}
