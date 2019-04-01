package com.teamfour.monopolish.game.entities.player;

import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.Entity;

import java.sql.SQLException;

/**
 * Represents the players in a game
 *
 * @author      lisawil & eirikhem
 * @version     1.2
 */

public class Player extends Entity {
    // Attributes (all these will be synced to database)
    private final String USERNAME;
    private int position = 0;
    private boolean inJail = false;
    private boolean bankrupt = false;
    private int active = 0;
    private int score = 0;

    // Client-side only attributes
    private boolean freeParking = false;

    /**
     * Constructor
     * @param USERNAME
     */
    public Player(String USERNAME) {
        super();
        this.USERNAME = USERNAME;
    }

    /**
     * Constructor
     * @param username
     * @param money
     * @param position
     * @param inJail
     * @param bankrupt
     * @param active
     * @param money
     */
    public Player(String username, int money, int position, boolean inJail,
                  boolean bankrupt, int active) {
        super(money);
        this.USERNAME = username;
        this.money = money;
        this.position = position;
        this.inJail = inJail;
        this.bankrupt = bankrupt;
        this.active = active;
    }

    public boolean checkBankrupt() {
        if (money > 0) {
            return false;
        }

        int numberOfValidProperties = 0;
        for (int i = 0; i < properties.size(); i++) {
            if (!properties.get(i).isPawned())
                numberOfValidProperties++;
        }

        return (money == 0 && numberOfValidProperties == 0);
    }

    /**
     * Calculates this player's score based on their money and property values
     */
    public int calculateScore() {
        score = money;

        for (int i = 0; i < properties.size(); i++) {
            score += properties.get(i).getPrice() / 2;
        }

        return score;
    }

    /**
     * Moves a specified amount
     * @param steps
     */
    public void move(int steps) {
        while (true) {
            position++;
            steps--;
            if (position == Board.BOARD_LENGTH) {
                position = 0;
            }
            if (steps == 0) {
                break;
            }
        }
    }

    @Override
    public void updatePropertiesFromDatabase(int gameId) throws SQLException {
        properties.clear();
        properties = propertyDAO.getPropertiesByOwner(gameId, USERNAME);
        System.out.println("Properties amount: " + properties.size() + "from Player");
    }

    /**
     * Moves to a specified position
     * @param position
     */
    public void moveTo(int position) {
        if (position >= (Board.BOARD_LENGTH) || position < 0)
            throw new IllegalArgumentException("Position is not within board bounds");

        this.position = position;
    }

    public String getUsername() {
        return USERNAME;
    }

    public int getPosition() {
        return position;
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

    public void setMoney(int money) {
        this.money = money;
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

    public int getMoney() {
        return money;
    }

    public boolean hasFreeParking() { return freeParking; }

    public void setFreeParking(boolean freeParking) { this.freeParking = freeParking; }
}