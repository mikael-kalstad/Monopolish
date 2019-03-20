package com.teamfour.monopolish.game;

import javafx.geometry.Pos;
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

    public void draw(int numOfEyes){
        ArrayList<Circle> dots = new ArrayList<>();
        dots.add(new Circle());
        dots.add(new Circle());
        dots.add(new Circle());
        dots.add(new Circle());
        dots.add(new Circle());
        dots.add(new Circle());

        //switch(numOfEyes){
            //case 1:
                //dots.get(0).setFill(Color.BLACK);
                //dots.get(0).setStroke(Color.BLACK);


                //dots.get(0).setAlignment();

        }
    //}

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
            result[i] = randomNum(1, numOfEyes);
        }

        return result;
    }
}
