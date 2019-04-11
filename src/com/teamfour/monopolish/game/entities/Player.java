package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.GameConstants;
import com.teamfour.monopolish.gui.controllers.Handler;

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
    private boolean freeParking = false;

    /**
     * Constructor
     * @param USERNAME username of this player
     */
    public Player(String USERNAME) {
        super();
        this.USERNAME = USERNAME;
    }

    /**
     * Constructor for existing player
     * @param username Username of this player
     * @param money Current money
     * @param position Current position
     * @param inJail Current jail status
     * @param bankrupt Current bankrupt status
     * @param active Current active status
     * @param freeParking Current status of free parking
     */
    public Player(String username, int money, int position, boolean inJail,
                  boolean bankrupt, int active, boolean freeParking) {
        super(money);
        this.USERNAME = username;
        this.money = money;
        this.position = position;
        this.inJail = inJail;
        this.bankrupt = bankrupt;
        this.active = active;
        this.freeParking = freeParking;
    }

    /**
     * Checks if this player is bankrupt
     * @return True if bankrupt
     */
    public boolean checkBankrupt() {
        // If user has money, then they're definitely not bankrupt
        if (money > 0) {
            return false;
        }

        // If user has no money, and all their properties already are pawned, bankrupt is true
        return (money == 0 && getNumberOfUnpawnedProperties() == 0);
    }

    /**
     * Gets the number of unpawned properties this player currently has
     * @return Number of unpawned properties
     */
    public int getNumberOfUnpawnedProperties() {
        int numberOfValidProperties = 0;
        for (int i = 0; i < properties.size(); i++) {
            if (!properties.get(i).isPawned())
                numberOfValidProperties++;
        }

        return numberOfValidProperties;
    }

    /**
     * Moves a specified amount
     * @param steps Steps to move
     */
    public void move(int steps) {
        while (true) {
            position++;
            steps--;
            // If you pass the length of board, reset to start
            if (position == GameConstants.BOARD_LENGTH) {
                position = 0;
            }
            if (steps == 0) {
                break;
            }
        }
    }

    /**
     * Gets all your properties from the database
     * @param gameId Game Id this entity belongs to
     */
    @Override
    public void updatePropertiesFromDatabase(int gameId) {
        properties.clear();
        properties = Handler.getPropertyDAO().getPropertiesByOwner(gameId, USERNAME);
    }

    /**
     * Moves to a specified position
     * @param position
     */
    public void moveTo(int position) {
        if (position >= (GameConstants.BOARD_LENGTH) || position < 0)
            throw new IllegalArgumentException("Position is not within board bounds");

        this.position = position;
    }

    // GETTERS & SETTERS

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

    public boolean hasFreeParking() { return freeParking; }

    public void setFreeParking(boolean freeParking) { this.freeParking = freeParking; }
}