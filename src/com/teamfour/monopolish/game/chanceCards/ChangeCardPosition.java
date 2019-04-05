package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.gui.controllers.Handler;

public class ChangeCardPosition extends ChanceCard {
    private int position;

    ChangeCardPosition(String msg, String logoPath, int position) {
        super(msg, logoPath);
        this.position = position;
    }

    /**
     * Move player to desired position set in constructor
     */
    public void moveToPosition() {
        Handler.getCurrentGame().getEntities().getPlayer(super.getUSERNAME()).moveTo(position);
    }
}
