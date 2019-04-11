package com.teamfour.monopolish.game.property;

/**
 * Boat class of type Property. Boats adjust their rent based on how many boats the owner has in their inventory
 *
 * @author      eirikhem
 */

public class Boat extends Property {
    // Attributes
    private int[] rent = new int[3];

    /**
     * Constructor
     *
     * @param p_id          property id
     * @param name          name of property
     * @param price         the price of the property
     * @param position      the position the property is located on the board
     * @param categorycolor the category color of the property
     * @param owner         the current owner of the property
     */
    public Boat(int p_id, String name, int price, int position, String categorycolor, String owner,
                boolean pawned) {
        super(p_id, name, price, position, categorycolor, owner, pawned);
        calculateRent();
    }

    /**
     * Gets a specific level of rent
     * @param rentLevel Rent level to get
     * @return Rent amount
     */
    public int getRent(int rentLevel) {
        return rent[rentLevel];
    }

    /**
     * Gets a string array of all the rent levels
     * @return String array
     */
    @Override
    public String[] getAllRent() {
        String[] rentString = new String[rent.length];
        for (int i = 0; i < rent.length; i++) {
            rentString[i] = "" + rent[i];
        }
        return rentString;
    }

    /**
     * Sets up all the rent levels
     */
    private void calculateRent() {
        for (int i = 0; i < rent.length; i++) {
            rent[i] = (int)(price * (0.2 * (i + 1)));
        }
    }
}
