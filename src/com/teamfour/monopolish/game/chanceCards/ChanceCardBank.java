package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;

/**
 * Class for chance cards with transfers between the bank and the pfwrlayer
 */
public class ChanceCardBank extends ChanceCard {
    ChanceCardBank(String msg, String logoPath, int amount) {
        super(msg, logoPath, amount);
    }

    /**
     * Transfer money from the bank to a player or from the player to the bank.
     */
    public void moneyTransaction() {
        Handler.getCurrentGame().getEntities().transferMoneyFromBank(super.getUSERNAME(), super.getAmount());
    }
}
