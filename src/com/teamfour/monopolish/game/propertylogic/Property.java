package com.teamfour.monopolish.game.propertylogic;

public class Property {
    private int id;
    private String name;
    private int price;
    private int position;
    private boolean pawned = false;
    private int owner = 0;
    private String categorycolor;

    public Property(int p_id, String name, int price, int position, String categorycolor){
        this.id = p_id;
        this.name = name;
        this.price = price;
        this.position = position;
        this.categorycolor = categorycolor;
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

    public int getOwner() {
        return owner;
    }

    public void setPawned(boolean pawned) {
        this.pawned = pawned;
    }

    public void setOwner(int owner) {
        this.owner = owner;
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
