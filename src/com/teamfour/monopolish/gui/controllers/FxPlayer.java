package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class for drawing players and player position on the board
 *
 * @author Bård Hestmark
 * @version 1.5
 */

public class FxPlayer extends StackPane {
    // Attributes
    private static int MAX = 9;

    private int tilePosition = 0;
    private int posX, posY;
    private String username;

    public FxPlayer(String username, int posX, int posY) {
        this.username = username;
        this.posX = posX;
        this.posY = posY;

        Circle spiller = new Circle(10);
        spiller.setFill(Color.BLUE);
        spiller.setStroke(Color.BLUE);

        setAlignment(Pos.CENTER);
        getChildren().addAll(spiller);
    }

    public static int getMAX() {
        return MAX;
    }

    public static void main(String[] args) {
        FxPlayer p = new FxPlayer("k", 9, 9);
        p.move(37);
        System.out.println(p);
    }

    public void move (int steps) {
        while (true) {
            tilePosition++;
            steps--;
            if (tilePosition == MAX*4) {
                tilePosition = 0;
            }
            if (steps == 0) {
                break;
            }
        }
        posToXY(tilePosition);
    }

    private void posToXY(int pos) {
        int p, x, y;
        if (pos > (MAX * 4) - 1 || pos < 0) {
            throw new IllegalArgumentException("Player position out of bounds");
        }
        if (pos >= 0 && pos < MAX) {
            p = 0;
            x = MAX;
            while (true) {
                x--;
                p++;
                if (p == pos) {
                    this.posY = MAX;
                    this.posX = x;
                    break;
                }
            }
        }
        if (pos >= MAX && pos < MAX * 2) {
            p = MAX;
            y = MAX;
            while (true) {
                y--;
                p++;
                if (p == pos) {
                    this.posY = y;
                    this.posX = 0;
                    break;
                }
            }
        }
        if (pos >= MAX * 2 && pos < MAX * 3) {
            p = MAX * 2;
            x = 0;
            while (true) {
                x++;
                p++;
                if (p == pos) {
                    this.posY = 0;
                    this.posX = x;
                    break;
                }
            }
        }
        if (pos >= MAX * 3 && pos < MAX * 4) {
            p = MAX * 3;
            y = 0;
            while(true) {
                y++;
                p++;
                if (p == pos) {
                    this.posY = y;
                    this.posX = MAX;
                    break;
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

    public String getUsername() {
        return username;
    }

    public int getTilePosition() {
        return tilePosition;
    }

    public String toString() {
        String s = "Player moved to X: " + posX + ", Y: " + posY;
        return s;
    }
}
