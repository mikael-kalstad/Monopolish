package com.teamfour.monopolish.game.board;

public class Layout {
    // Static variables
    public static final int START = 0;
    public static final int PROPERTY = 1;
    public static final int GOTOJAIL = 2;
    public static final int FREEPARKING = 3;

    // Attributes
    private int[] tiles;

    /**
     * Returns the tile type at the specified position
     * @param position Position integer
     * @return Tile type ID at this position
     */
    public int getTileType(int position) {
        return tiles[position];
    }
}
