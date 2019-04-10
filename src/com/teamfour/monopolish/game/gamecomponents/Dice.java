package com.teamfour.monopolish.game.gamecomponents;

/**
 * This class represents a dice that can be used in a game
 *
 * @author Mikael Kalstad
 * @version 1.2
 */

public class Dice {
    private int numOfDice;          // How many dice in this set
    private int numOfEyes;          // How many eyes on each die
    private int[] lastThrow;        // Stores the last throw result for future use

    /**
     * Constructor
     *
     * @param numOfDice how many dices
     * @param numOfEyes how many eyes on each dice
     */
    public Dice(int numOfDice, int numOfEyes) {
        this.numOfDice = numOfDice;
        this.numOfEyes = numOfEyes;
        lastThrow = new int[numOfDice];
    }

    /**
     * Throw the dice and get the random results
     *
     * @return array with dice values
     */
    public int[] throwDice() {
        for (int i = 0; i < numOfDice; i++) {
            lastThrow[i] = (int)(Math.random() * ((numOfEyes - 1) + 1)) + 1;
        }

        return lastThrow;
    }

    public int[] getLastThrow() {
        return lastThrow;
    }
}