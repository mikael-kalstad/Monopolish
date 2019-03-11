package com.teamfour.monopolish.game.PropertyLogic;

public class Property {
    private int p_id;
    private String name;
    private int price;
    private int position;
    private boolean pawned = false;
    private int owner = 0;

    public Property(int p_id, String name, int price, int position){
        this.p_id = p_id;
        this.name = name;
        this.price = price;
        this.position = position;
    }

    public int getP_id() {
        return p_id;
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
        return(otherP.getP_id() == this.p_id);
    }
}
