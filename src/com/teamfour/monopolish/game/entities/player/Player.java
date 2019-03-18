package com.teamfour.monopolish.game.entities.player;

import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.Entity;

/**
 * Represents the players in a game
 *
 * @author      lisawil
 * @version     1.2
 */

public class Player extends Entity {
    // Attributes
    private final String username;
    private int position = 0;
    private boolean inJail = false;
    private boolean bankrupt = false;
    private int active = 0;
    private int score = 0;

    /**
     * Constructor
     * @param username
     */
    public Player(String username) {
        super();
        this.username = username;
        this.position = 0;
    }

    /**
     * Constructor
     * @param username
     * @param money
     * @param position
     * @param inJail
     * @param bankrupt
     * @param active
     * @param score
     */
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

    /**
     * Moves a specified amount
     * @param steps
     */
    public void move(int steps){
        if(position + steps > (Board.BOARD_LENGTH - 1)){
            position = position + steps - (Board.BOARD_LENGTH - 1);
        }
        position+=steps;
    }

    /**
     * Moves to a specified position
     * @param position
     */
    public void moveTo(int position) {
        if (position < (Board.BOARD_LENGTH - 1))
            throw new IllegalArgumentException("Position is not within board bounds");

        this.position = position;
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