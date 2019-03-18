package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FxPlayer extends StackPane{

    private static int MAX = 9;
    int retning;

    private int posX, posY;

    public FxPlayer(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        Circle spiller = new Circle(10);
        spiller.setFill(Color.BLUE);
        spiller.setStroke(Color.BLUE);

        setAlignment(Pos.CENTER);
        getChildren().addAll(spiller);
    }

    public static void main(String[] args) {
        FxPlayer p = new FxPlayer(9, 9);
        p.move(18);

        System.out.println(p);
    }

    public void getDirection() {
        if (posX > 0 && posY == MAX) {
            retning = 1; //venstre
        }
        if (posX == 0 && posY > 0) {
            retning = 2; //opp
        }
        if (posX < MAX && posY == 0) {
            retning = 3; //høyre
        }
        if (posX == MAX && posY < MAX) {
            retning = 4; //ned
        }
    }

    public void move(int count) {
        getDirection();
        while (count != 0) {
            switch (retning) {
                case 1:
                    while (posX >= 0 && count!= 0) {
                        posX--;
                        count--;
                        if (posX == 0) {
                            retning = 2;
                            break;
                        }
                    }
                case 2:
                    while (posY >= 0 && count != 0) {
                        posY--;
                        count--;
                        if (posY == 0) {
                            retning = 3;
                            break;
                        }
                    }
                case 3:
                    while (posX <= MAX && count != 0) {
                        posX++;
                        count--;
                        if (posX == MAX) {
                            retning = 4;
                            break;
                        }
                    }
                case 4:
                    while (posY <= MAX && count != 0) {
                        posY++;
                        count--;
                        if (posY == MAX) {
                            retning = 1;
                            break;
                        }
                    }
            }
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public static int getMAX() {
        return MAX;
    }

    public String toString() {
        String s = "Player moved to X: " + posX + ", Y: " + posY;
        return s;
    }
}
