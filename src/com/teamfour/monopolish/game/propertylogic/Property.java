package com.teamfour.monopolish.game.propertylogic;

/**
 * Represents the properties in a game
 * @author      lisawil
 * @version     1.0
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
     */
    public Property(int p_id, String name, int price, int position, String categorycolor, String owner){
        this.ID = p_id;
        this.NAME = name;
        this.price = price;
        this.position = position;
        this.CATEGORYCOLOR = categorycolor;
        this.owner = owner;
    }

    public String toString() {
        String result = "name: " + NAME + "; Price: " + price + "; Position: " + position;
        return result;
    }

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
}
