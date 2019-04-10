package com.teamfour.monopolish.game;

/**
 * This class holds all the constants involved in the gameplay aspects of the game.
 *
 * @author      eirikhem
 * @version     1.0
 */

public class GameConstants {
    // Game money and prices
    public static final int START_MONEY = 30000;            // Money each player gets at start of game
    public static final int BAIL_COST = 1000;               // Amount you have to pay to get out of jail
    public static final int ROUND_MONEY = 4000;             // Money to receive each time you complete the board
    public static final int MAX_GAME_MONEY = 500000;        // Max money in circulation
    public static final int INCOME_TAX = 4000;              // Amount to pay in income tax

    // Board settings
    public static final int BOARD_LENGTH = 36;              // Length of the board

    // Game settings
    public static final int MAX_PLAYERS = 4;                // Maximum amount of players
    public static final int MAX_HOUSES = 32;                // Max houses in game
    public static final int MAX_HOTELS = 8;                 // Max hotels in game
}
