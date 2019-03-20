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
    int direction;

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
        p.posToXY(36);
        System.out.println(p);
    }

    public void getDirection() {
        if (posX > 0 && posY == MAX) {
            direction = 1; //venstre
        }
        if (posX == 0 && posY > 0) {
            direction = 2; //opp
        }
        if (posX < MAX && posY == 0) {
            direction = 3; //høyre
        }
        if (posX == MAX && posY < MAX) {
            direction = 4; //ned
        }
    }

    public void move(int steps) {
        getDirection();
        while (steps != 0) {
            switch (direction) {
                case 1:
                    while (posX >= 0 && steps != 0) {
                        posX--;
                        steps--;
                        if (posX == 0) {
                            direction = 2;
                            break;
                        }
                    }
                case 2:
                    while (posY >= 0 && steps != 0) {
                        posY--;
                        steps--;
                        if (posY == 0) {
                            direction = 3;
                            break;
                        }
                    }
                case 3:
                    while (posX <= MAX && steps != 0) {
                        posX++;
                        steps--;
                        if (posX == MAX) {
                            direction = 4;
                            break;
                        }
                    }
                case 4:
                    while (posY <= MAX && steps != 0) {
                        posY++;
                        steps--;
                        if (posY == MAX) {
                            direction = 1;
                            break;
                        }
                    }
            }
        }
    }

    private int[][] positionGenerator() {
        int[][] position = new int[36][2];
        int x = 9;
        int y = 9;
        for (int i = 0; i < 36; i++) {
            position[i][0] = x;
            position[i][1] = y;

            if (x > 0 && i < 9) {
                x--;
            } else if (x < 1 && y > 0) {
                y--;
            } else if (y < 1 && x < 9) {
                x++;
            } else if (x == 9 && i > 1) {
                y++;
            }
        }
        return (position);
    }

    public void posToXY(int pos) {
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
                    posY = MAX;
                    posX = x;
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
                    posY = y;
                    posX = 0;
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
                    posY = 0;
                    posX = x;
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

    public String toString() {
        String s = "Player moved to X: " + posX + ", Y: " + posY;
        return s;
    }
}
