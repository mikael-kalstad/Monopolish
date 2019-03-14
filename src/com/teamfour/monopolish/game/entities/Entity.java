package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.propertylogic.*;

import java.util.ArrayList;

/**
 * Represents all players and banks in the game. Each entity in a game session has its own array of properties, and
 * a limited amount of money. Money and properties are transferred between entities
 *
 * @author      eirikhem
 * @version     1.0
 */

public abstract class Entity {
    // Attributes
    protected ArrayList<Property> properties;
    protected int money;
    protected PropertyDAO propertyDAO;

    /**
     * Constructor
     */
    public Entity() {
        properties = new ArrayList<>();
        money = 0;
        propertyDAO = new PropertyDAO();
    }

    /**
     * Adjusts the amount of money
     * @param amount Money to add or subtract
     * @return New amount
     */
    public int adjustMoney(int amount) {
        // If the amount is negative, check if the entity can afford the transaction
        if (amount < 0 && Math.abs(amount) > money)
            return -1;

        money += amount;
        return money;
    }

    /**
     * Exchanges money between another entity
     * @param entity The other entity
     * @param amount Amount to transfer
     */
    public boolean transferMoney(Entity entity, int amount) {
        if (this.adjustMoney(-amount) < 0)
            return false;

        if (entity.adjustMoney(amount) < 0)
            return false;

        return true;
    }

    /**
     * Exchanges a property to another entity
     * @param entity the other entity
     * @param id Id of property to exchange
     */
    public boolean transferProperty(Entity entity, int id) {
        if (id >= properties.size() || properties.get(id) == null)
            return false;

        entity.getProperties().add(properties.get(id));
        properties.remove(id);

        return true;
    }

    // GETTERS & SETTERS

    public ArrayList<Property> getProperties() { return properties; }

    public int getMoney() { return money; }
}