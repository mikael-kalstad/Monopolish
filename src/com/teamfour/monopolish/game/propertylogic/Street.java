package com.teamfour.monopolish.game.propertylogic;

public class Street extends Property {
    private final int MAX_HOUSES = 4;
    private final int MAX_HOTELS = 1;

    // Attributes
    private int[] rent = new int[9];
    private int houses;
    private int hotels;

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
    public Street(int p_id, String name, int price, int position, String categorycolor, String owner) {
        super(p_id, name, price, position, categorycolor, owner);
        calculateRent();

        rent[7] = price;
        rent[8] = price;
    }

    public int getRent() {
        int currentRent = 0;
        if (hotels == 0) {
            currentRent = rent[houses];
        } else {
            currentRent = rent[5];
        }

        return currentRent;
    }

    @Override
    public String[] getAllRent() {
        String[] rentString = new String[rent.length];
        for (int i = 0; i < rent.length; i++) {
            rentString[i] = "" + rent[i];
        }
        return rentString;
    }

    /**
     * Calculates rent of the different levels of house/hotel construction
     */
    private void calculateRent() {
        for (int i = 0; i < rent.length - 2; i++) {
            rent[i] = (int)(price * 0.1 * (i + 1));
        }
    }

    /**
     * Adds a house to this street
     * @return True if successful
     */
    public boolean addHouse() {
        if (houses == MAX_HOUSES)
            return false;

        houses++;
        return true;
    }

    /**
     * Adds a hotel if enough houses
     * @return True if successful
     */
    public boolean addHotel() {
        if (hotels == MAX_HOTELS || houses < MAX_HOUSES)
            return false;

        hotels++;
        return true;
    }

    // GETTERS

    public int getHousePrice() {
        return rent[7];
    }

    public int getHotelPrice() {
        return rent[8];
    }

    public int getHouses() {
        return houses;
    }

    public int getHotels() {
        return hotels;
    }
}
