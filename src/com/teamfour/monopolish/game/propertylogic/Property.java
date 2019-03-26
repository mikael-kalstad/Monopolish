package com.teamfour.monopolish.game.propertylogic;

/**
 * Represents the properties in a game
 * @author      lisawil
 * @version     1.1
 */

public class Property {

    //Attributes
    private final int ID;
    private final String NAME;
    private int price;
    private int position;
    private boolean pawned = false;
    private String owner;
    private final String CATEGORYCOLOR;

    /**
     * Constructor
     * @param p_id property id
     * @param name name of property
     * @param price the price of the property
     * @param position the position the property is located on the board
     * @param categorycolor the categorycolor of the property
     * @param owner the current owner of the property
     */
    public Property(int p_id, String name, int price, int position, String categorycolor, String owner){
        this.ID = p_id;
        this.NAME = name;
        this.price = price;
        this.position = position;
        this.CATEGORYCOLOR = categorycolor;
        this.owner = owner;
    }

    @Override
    public String toString() {
        String result = "name: " + NAME + "; Price: " + price + "; Position: " + position;
        return result;
    }

    /**
     * Compares the price of a property to another property
     * @param otherP
     * @return
     */
    public int compareTo(Property otherP){
        if(otherP == null){
            return(-2);
        }
        if(otherP.getPrice() > this.price){
            return(-1);
        }
        if(otherP.getPrice()<this.price){
            return(1);
        }
        else{
            return(0);
        }
    }

    public boolean equals(Property otherP){
        if(otherP == null){
            return(false);
        }
        if(this == otherP){
            return (true);
        }
        return(otherP.getId() == this.ID);
    }

    // SETTERS & GETTERS

    public int getId() {
        return ID;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return NAME;
    }

    public int getPrice() {
        return price;
    }

    public String getCategorycolor(){
        return CATEGORYCOLOR;
    }

    public boolean isPawned() {
        return pawned;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPawned(boolean pawned) {
        this.pawned = pawned;
    }
}
