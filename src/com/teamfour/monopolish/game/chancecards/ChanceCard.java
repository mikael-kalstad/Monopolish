package com.teamfour.monopolish.game.chancecards;

import com.teamfour.monopolish.gui.Handler;

/**
 * Super class for chance cards, contains general data about chance cards.
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

    ChanceCard(String msg, String logoPath) {
        this.msg = msg;
        this.logoPath = logoPath;
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

    public void setAmount(int amount) { this.amount = amount; }
}
