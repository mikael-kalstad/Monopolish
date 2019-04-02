package com.teamfour.monopolish.game.entities;

/**
 * Represents the bank in the game. The bank has its own money and properties, along with a number of houses that can be
 * given to players
 *
 * @author      eirikhem
 * @version     1.0
 */

public class Bank extends Entity {
    // Static variables
    public static final int STARTING_HOUSES = 32;
    public static final int STARTING_HOTELS = 8;
    public static final int MAX_GAME_MONEY = 500000;

    // Attributes
    int availableHouses;
    int availableHotels;

    /**
     * Constructor
     */
    public Bank(int gameId) {
        super();
        availableHouses = STARTING_HOUSES;
        availableHotels = STARTING_HOTELS;
        money = MAX_GAME_MONEY;
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