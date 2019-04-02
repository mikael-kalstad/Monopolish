package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.*;

import java.sql.SQLException;
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

    public Entity(int money) {
        properties = new ArrayList<>();
        this.money = money;
        propertyDAO = new PropertyDAO();
    }

    public Property getPropertyAtPosition(int position) {
        for (Property p : properties) {
            if (p.getPosition() == position)
                return p;
        }

        return null;
    }

    /**
     * Adjusts the amount of money
     * @param amount Money to add or subtract
     * @return New amount
     */
    public int adjustMoney(int amount) {
        // If the amount is negative, check if the entity can afford the transaction
        money += amount;
        return money;
    }

    /**
     * Exchanges money to another entity
     * @param entity The other entity
     * @param amount Amount to transfer
     */
    public boolean transferMoney(Entity entity, int amount) {
        this.adjustMoney(-amount);
        entity.adjustMoney(amount);

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

        Property property = properties.get(id);
        if (entity instanceof Player)
            property.setOwner(((Player) entity).getUsername());
        else
            property.setOwner("");

        entity.getProperties().add(property);
        properties.remove(id);

        return true;
    }

    public void updatePropertiesToDatabase(int gameId) throws SQLException {
        for (Property prop : properties) {
            propertyDAO.updateProperty(prop, gameId);
        }
    }

    public void updatePropertiesFromDatabase(int gameId) throws SQLException {
        properties.clear();
        properties = propertyDAO.getPropertiesByOwner(gameId, null);
    }

    @Override
    public String toString() {
        String result = "Money: " + money + "\n";
        result += "Properties:\n";
        for (Property p : properties) {
            result += p.toString() + "\n";
        }

        return result;
    }

    // GETTERS & SETTERS

    public ArrayList<Property> getProperties() { return properties; }

    public int getMoney() { return money; }
}