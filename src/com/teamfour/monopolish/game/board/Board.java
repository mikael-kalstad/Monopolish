package com.teamfour.monopolish.game.board;

/**
 * Represents the tile data in the board. Each tile on the board is represented by
 * an Id to tell what kind of tile it is.
 *
 * @author      eirikhem
 * @version     1.3
 */

public class Board {
    // Static variables
    public static final int BOARD_LENGTH = 36;
    public static final int START = 0;
    public static final int PROPERTY = 1;
    public static final int GO_TO_JAIL = 2;
    public static final int FREE_PARKING = 3;
    public static final int JAIL = 4;
    public static final int CHANCE = 5;
    public static final int SPECIAL_PROPERTY = 6;

    // Attributes
    private int[] tiles = {0, 1, 6, 1, 6, 1, 5, 1, 1,
                            4, 1, 6, 1, 1, 1, 6, 1, 1,
                            3, 1, 5, 1, 1, 1, 1, 5, 1,
                            2, 1, 1, 6, 1, 5, 1, 6, 1};

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
    public int getGoToJailPosition() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == GO_TO_JAIL) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the position where jail is located
     * @return The int position where jail is located
     */
    public int getJailPosition() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == JAIL) {
                return i;
            }
        }

        return -1;
    }

    public int getFreeParkingPosition() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == FREE_PARKING) {
                return i;
            }
        }

        return -1;
    }
}