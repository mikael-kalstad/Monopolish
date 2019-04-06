package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;
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
        // Play rick roll
        String soundFile = "res/sounds/prank.mp3";

        Media sound = new Media(new File(soundFile).toURI().toString());
        player = new MediaPlayer(sound);
        player.seek(player.getStartTime());
        player.play();
        player.setVolume(100);

        Handler.getCurrentGame().getEntities().transferMoneyFromBank(super.getUSERNAME(), super.getAmount());
    }
}
