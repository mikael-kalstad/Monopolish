package com.teamfour.monopolish.game;

import java.util.Random;

public class Dice {
    private Random random = new Random();
    private int numOfDices;
    private int numOfEyes;

    public Dice(int numOfDices, int numOfEyes) {
        this.numOfDices = numOfDices;
        this.numOfEyes = numOfEyes;
    }

    private int randomNum(int min, int max) {
        return random.nextInt(max-min) + min;
    }

    public int[] throwDice() {
        int[] result = new int[numOfDices];

        for (int i = 0; i < numOfDices; i++) {
            result[i] = randomNum(1, numOfEyes);
        }

        return result;
    }

    public static void main(String[] args) {
        Dice dice = new Dice(2, 6);
        int[] result = dice.throwDice();

        for (int i = 0; i < result.length; i++) {
            System.out.println("Dice " + i + " " + result[i]);
        }
    }
}
