package com.teamfour.monopolish.game.entities.player;

import com.teamfour.monopolish.game.entities.Entity;

/**
 * Represents the players in a game
 *
 * @author      lisawil
 * @version     1.2
 */

public class Player extends Entity {
    private final String username;
    private int position = 0;
    private boolean inJail = false;
    private boolean bankrupt = false;
    private int active = 0;
    private int score = 0;

    public Player(String username) {
        super();
        this.username = username;
        this.position = 0;
    }

    public Player(String username, int money, int position, boolean inJail,
                  boolean bankrupt, int active, int score) {
        super(money);
        this.username = username;
        this.position = position;
        this.inJail = inJail;
        this.bankrupt = bankrupt;
        this.active = active;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

    public void setBankrupt(boolean bankrupt) {
        this.bankrupt = bankrupt;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void move(int steps){
        if(position + steps >31){
            position = position + steps - 31;
        }
        position+=steps;
    }

    public boolean isInJail() {
        return inJail;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public int getActive() {
        return active;
    }

    public int getScore() {
        return score;
    }
}