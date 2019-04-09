package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.*;
import com.teamfour.monopolish.gui.controllers.Handler;

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

    /**
     * Constructor
     */
    public Entity() {
        properties = new ArrayList<>();
        money = 0;
    }

    /**
     * Constructor for existing entity
     * @param money
     */
    public Entity(int money) {
        properties = new ArrayList<>();
        this.money = money;
    }

    /**
     * Gets a property object that you might own on a specified position
     * @param position Position integer
     * @return Property object
     */
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

    /**
     * Writes all properties on this entity to the database
     * @param gameId GameId to write to
     * @throws SQLException
     */
    public void updatePropertiesToDatabase(int gameId) throws SQLException {
        for (Property prop : properties) {
            Handler.getPropertyDAO().updateProperty(prop, gameId);
        }
    }

    /**
     * Gets all of this entity's properties from the database
     */
    public void updatePropertiesFromDatabase(int gameId) throws SQLException {
        properties.clear();
        properties = Handler.getPropertyDAO().getPropertiesByOwner(gameId, null);
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

    /**
     * Gets how many boats this entity owns
     * @return Number of boats
     */
    public int getBoatsOwned() {
        int result = 0;
        for (Property p : properties) {
            if (p.getType() == Property.BOAT)
                result++;
        }

        return result;
    }

    /**
     * Gets the number of trains this property owns
     * @return
     */
    public int getTrainsOwned() {
        int result = 0;
        for (Property p : properties) {
            if (p.getType() == Property.TRAIN)
                result++;
        }

        return result;
    }

    /**
     * CHecks if the entity has a full colorset of the specified color
     * @param gameId Id of the game
     * @param colorHex Color code
     * @return True if has full set
     */
    public boolean hasFullSet(int gameId, String colorHex) {
        // Get the size of the full set of this color
        int fullSetSize = 0;
        fullSetSize = Property.getFullColorSet(gameId, colorHex).size();
        // If your set size matches, you have a full set!
        int yourSetSize = 0;
        for (Property p : properties) {
            if (p.getCategorycolor().equals(colorHex))
                yourSetSize++;
        }

        return (fullSetSize == yourSetSize);
    }
}