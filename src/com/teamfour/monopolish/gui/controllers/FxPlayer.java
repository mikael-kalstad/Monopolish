package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class for drawing players and player position on the board
 *
 * @author Bård Hestmark
 * @version 1.4
 */

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

    public void move(int steps) {
        getDirection();
        while (steps != 0) {
            switch (retning) {
                case 1:
                    while (posX >= 0 && steps!= 0) {
                        posX--;
                        steps--;
                        if (posX == 0) {
                            retning = 2;
                            break;
                        }
                    }
                case 2:
                    while (posY >= 0 && steps != 0) {
                        posY--;
                        steps--;
                        if (posY == 0) {
                            retning = 3;
                            break;
                        }
                    }
                case 3:
                    while (posX <= MAX && steps != 0) {
                        posX++;
                        steps--;
                        if (posX == MAX) {
                            retning = 4;
                            break;
                        }
                    }
                case 4:
                    while (posY <= MAX && steps != 0) {
                        posY++;
                        steps--;
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

    private int[][] positionGenerator() {
        int[][] position = new int[36][2];
        int x = 9;
        int y = 9;
        for(int i = 0; i<36; i++){
            position[i][0] = x;
            position[i][1] = y;

            if(x>0 && i<9){
                x--;
            } else if(x<1 && y>0){
                y--;
            } else if(y<1 && x<9){
                x++;
            } else if(x == 9 && i>1){
                y++;
            }
        }
        return(position);
    }

    public String toString() {
        String s = "Player moved to X: " + posX + ", Y: " + posY;
        return s;
    }
}
