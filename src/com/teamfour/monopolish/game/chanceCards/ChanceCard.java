package com.teamfour.monopolish.game.chanceCards;

import com.teamfour.monopolish.gui.controllers.Handler;

/**
 * Super class with data about a chance card
 */
public class ChanceCard {
    private final String USERNAME = Handler.getAccount().getUsername();
    private String msg;
    private String logoPath;
    private int amount;

    ChanceCard(String msg, String logoPath, int amount) {
        this.msg = msg;
        this.logoPath = logoPath;
        this.amount = amount;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getMsg() {
        return msg;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public int getAmount() {
        return amount;
    }
}
