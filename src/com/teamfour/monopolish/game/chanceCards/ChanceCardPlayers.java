package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;

/**
 * Class for chance cards with transfers between players
 */
public class ChanceCardPlayers extends ChanceCard {
    ChanceCardPlayers(String msg, String logoPath, int amount) {
        super(msg, logoPath, amount);
    }

    /**
     * Transfer money from between players
     */
    public void moneyTransaction(String toUser) {
        // Amount is negative, transfer from current user to player
        if (super.getAmount() < 0)
            Handler.getGameLogic().getEntityManager().transferMoneyFromTo(super.getUSERNAME(), toUser, super.getAmount());

            // Amount is positive, transfer from player to current user
        else
            Handler.getGameLogic().getEntityManager().transferMoneyFromTo(toUser, super.getUSERNAME(), super.getAmount());
    }


}
