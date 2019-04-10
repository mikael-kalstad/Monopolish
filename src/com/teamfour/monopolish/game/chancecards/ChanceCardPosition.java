package com.teamfour.monopolish.game.chancecards;

import com.teamfour.monopolish.gui.controllers.Handler;

public class ChanceCardPosition extends ChanceCard {
    private int position;

    ChanceCardPosition(String msg, String logoPath, int position) {
        super(msg, logoPath);
        this.position = position;
    }

    /**
     * Move player to desired position set in constructor
     */
    public void moveToPosition() {
        Handler.getCurrentGame().getEntities().getPlayer(super.getUSERNAME()).moveTo(position);

        // Check if players is in jail
        if (Handler.getCurrentGame().getBoard().getJailPosition() == Handler.getCurrentGame().getEntities().getYou().getPosition()) {
            Handler.getCurrentGame().getEntities().getYou().setInJail(true);
        }
    }
}
