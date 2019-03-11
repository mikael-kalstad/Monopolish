package com.teamfour.monopolish.game;

import java.util.Random;

/**
 * This class represents a dice that can be used in a game
 *
 * @author Mikael Kalstad
 * @version 1.0
 */

public class Dice {
    private Random random = new Random();
    private int numOfDices;
    private int numOfEyes;

    /**
     * Constructor
     *
     * @param numOfDices how many dices
     * @param numOfEyes how many eyes on each dice
     */
    public Dice(int numOfDices, int numOfEyes) {
        this.numOfDices = numOfDices;
        this.numOfEyes = numOfEyes;
    }

    /**
     * Helper method that will give a random number within some boundaries
     *
     * @param min minimal value
     * @param max maximum value
     * @return random number within boundaries
     */
    private int randomNum(int min, int max) {
        return random.nextInt(max-min) + min;
    }


    /**
     * Throw the dice and get the random results
     *
     * @return array with dice values
     */
    public int[] throwDice() {
        int[] result = new int[numOfDices];

        for (int i = 0; i < numOfDices; i++) {
            result[i] = randomNum(1, numOfEyes);
        }

        return result;
    }
}
