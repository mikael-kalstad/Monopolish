package com.teamfour.monopolish.game.propertylogic;

public class Train extends Property {
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
    public Train(int p_id, String name, int price, int position, String categorycolor, String owner) {
        super(p_id, name, price, position, categorycolor, owner);
    }

    public int getRent(int trainsOwned, int diceFactor) {
        if (trainsOwned == 1) {
            return diceFactor * 80;
        } else {
            return diceFactor * 200;
        }
    }

    public String[] getAllRent() {
        String[] rent = new String[2];
        rent[0] = "Dice times 80";
        rent[1] = "Dice times 200";

        return rent;
    }
}
