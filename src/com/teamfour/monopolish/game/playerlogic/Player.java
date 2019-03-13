package com.teamfour.monopolish.game.playerlogic;

public class Player {
    private final String username;
    private int money = 8000;
    private int position = 0;

    public Player(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getMoney() {
        return money;
    }

    public int getPosition() {
        return position;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void addMoney(int newMoney){
        money += newMoney;
    }

    public void move(int steps){
        if(position + steps >31){
            position = position + steps - 31;
        }
        position+=steps;
    }
}
