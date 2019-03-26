package com.teamfour.monopolish.game;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents a dice that can be used in a game
 *
 * @author Mikael Kalstad
 * @version 1.0
 */

public class Dice extends StackPane {
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

    public ArrayList<Circle> getDots(int numOfEyes){
        ArrayList<Circle> dots = new ArrayList<>();

        for (int i  = 0; i <= numOfEyes; i++) {
            dots.add(new Circle(10));
            dots.get(i).setFill(Color.BLACK);
            dots.get(i).setStroke(Color.BLACK);
        }
        return dots;
    }

    /**
     * Helper method that will give a random number within some boundaries
     *
     * @param min minimal value
     * @param max maximum value
     * @return random number within boundaries
     */
    private int randomNum(int min, int max) {
        return random.nextInt(max-min+1) + min;
    }


    /**
     * Throw the dice and get the random results
     *
     * @return array with dice values
     */
    public int[] throwDice() {
        int[] result = new int[numOfDices];

        for (int i = 0; i < numOfDices; i++) {
            //result[i] = randomNum(1, numOfEyes);
            result[i] = (int)(Math.random() * ((numOfEyes - 1) + 1)) + 1;
        }

        return result;
    }
}