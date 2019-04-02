package com.teamfour.monopolish.game.propertylogic;

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
     * @param categorycolor the categorycolor of the property
     * @param owner         the current owner of the property
     */
    public Boat(int p_id, String name, int price, int position, String categorycolor, String owner) {
        super(p_id, name, price, position, categorycolor, owner);
        calculateRent();
    }

    public int getRent(int rentLevel) {
        return rent[rentLevel];
    }

    @Override
    public String[] getAllRent() {
        String[] rentString = new String[rent.length];
        for (int i = 0; i < rent.length; i++) {
            rentString[i] = "" + rent[i];
        }
        return rentString;
    }

    private void calculateRent() {
        for (int i = 0; i < rent.length; i++) {
            rent[i] = (int)(price * (0.2 * (i + 1)));
        }
    }
}
