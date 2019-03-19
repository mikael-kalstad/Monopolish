package com.teamfour.monopolish.game.propertylogic;

/**
 * Represents all properties in a game
 *
 *
 * @author      lisawil
 * @version     1.0
 */

public class Property {

    //Attributes
    private final int id;
    private final String name;
    private int price;
    private int position;
    private boolean pawned = false;
    private String owner;
    private final String categorycolor;


    /**
     * Constructor
     */
    public Property(int p_id, String name, int price, int position, String categorycolor, String owner){
        this.id = p_id;
        this.name = name;
        this.price = price;
        this.position = position;
        this.categorycolor = categorycolor;
        this.owner = owner;
    }

    public String toString() {
        String result = "Name: " + name + "; Price: " + price + "; Position: " + position;
        return result;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategorycolor(){
        return categorycolor;
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
        return(otherP.getId() == this.id);
    }
}
