package com.teamfour.monopolish.game.entities.player;

import com.teamfour.monopolish.game.entities.Entity;

/**
 * Represents the players in a game
 *
 * @author      lisawil
 * @version     1.1
 */

public class Player extends Entity {
    private final String username;
    private int position = 0;

    public Player(String username) {
        super();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void move(int steps){
        if(position + steps >31){
            position = position + steps - 31;
        }
        position+=steps;
    }
}