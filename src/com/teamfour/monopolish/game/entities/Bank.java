package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.GameConstants;

/**
 * Represents the bank in the game. The bank has its own money and properties, along with a number of houses that can be
 * given to players
 *
 * @author      eirikhem
 * @version     1.2
 */

public class Bank extends Entity {
    // Attributes
    int availableHouses;
    int availableHotels;

    /**
     * Constructor
     */
    public Bank(int gameId) {
        super();
        availableHouses = GameConstants.MAX_HOUSES;
        availableHotels = GameConstants.MAX_HOTELS;
        money = GameConstants.MAX_GAME_MONEY;
        // Get all properties from database
        // TODO: Fix this
        try {
            properties = propertyDAO.getAllProperties(gameId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a requested amount of houses. If not enough houses are available, return available
     * @param amount Requested amount
     * @return Houses
     */
    public int getHouses(int amount) {
        if (availableHouses < amount)
            return availableHouses;

        return amount;
    }

    /**
     * Returns a requested amount of hotels. If not enough hotels are available, return available
     * @param amount Requested amount
     * @return Hotels
     */
    public int getHotels(int amount) {
        if (availableHotels < amount)
            return availableHotels;

        return amount;
    }

    // GETTERS

    public int getAvailableHouses() { return availableHouses; }
    public int getAvailableHotels() { return availableHotels; }

    public void setMoney(int value) { money = value; }
}