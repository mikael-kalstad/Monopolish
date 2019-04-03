package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;

/**
 * Class for chance cards with transfers between the bank and the player
 */
public class ChanceCardBank extends ChanceCard {
    ChanceCardBank(String msg, String logoPath, int amount) {
        super(msg, logoPath, amount);
    }

    /**
     * Transfer money from the bank to a player or from the player to the bank.
     */
    public void moneyTransaction() {
        // Amount is negative, transfer from player to bank
        if (super.getAmount() < 0)
            Handler.getGameLogic().getEntityManager().transferMoneyToBank(super.getUSERNAME(), super.getAmount());

            // Amount is positive, transfer from bank to player
        else
            Handler.getGameLogic().getEntityManager().transferMoneyFromBank(super.getUSERNAME(), super.getAmount());
    }
}
