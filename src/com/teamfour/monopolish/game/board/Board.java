package com.teamfour.monopolish.game.board;

/**
 * Represents the tile data in the board. Each tile on the board is represented by
 * an Id to tell what kind of tile it is.
 *
 * @author      eirikhem
 * @version     1.0
 */

public class Board {
    // Static variables
    public static final int BOARD_LENGTH = 32;
    public static final int START = 0;
    public static final int PROPERTY = 1;
    public static final int GO_TO_JAIL = 2;
    public static final int FREE_PARKING = 3;
    public static final int JAIL = 4;

    // Attributes
    private int[] tiles;

    public Board() {
        tiles = new int[BOARD_LENGTH];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = PROPERTY;
        }

        tiles[4] = GO_TO_JAIL;
        tiles[16] = JAIL;
    }

    /**
     * Returns the tile type at the specified position
     * @param position Position integer
     * @return Tile type ID at this position
     */
    public int getTileType(int position) {
        return tiles[position];
    }

    /**
     * Returns the position where jail is located
     * @return The int position where jail is located
     */
    public int getJailPosition() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == JAIL) {
                System.out.println("Jail is at " + i);
                return i;
            }
        }

        return -1;
    }
}