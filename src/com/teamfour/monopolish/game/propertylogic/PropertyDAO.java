package com.teamfour.monopolish.game.propertylogic;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.database.DataAccessObject;
import com.teamfour.monopolish.game.entities.Entity;
import com.teamfour.monopolish.game.entities.player.Player;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

/**
 * Handles Property-DB connection and methods
 *
 *
 * @author      lisawil
 * @version     1.0
 */

public class PropertyDAO extends DataAccessObject {
    /**
     * creates gameproperties in the database and Property objects for the game.
     * @param game_id the id of the current game
     *
     */
    public ArrayList<Property> getAllProperties(int game_id) throws SQLException {
        ArrayList<Property> props = new ArrayList<>();
        try {
            getConnection();
            cStmt = connection.prepareCall("{call property_get_all(?)}");

            cStmt.setInt(1, game_id);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                int propertyId = rs.getInt(1);
                String name = rs.getString(2);
                int price = rs.getInt(3);
                int position = rs.getInt(4);
                String categoryColor = rs.getString(5);
                String owner = rs.getString(6);

                props.add(new Property(propertyId, name, price, position, categoryColor, owner));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return (props);
    }

    /**
     * gets gameproperties from the database and generates Property objects related to the parameter username.
     * @param gameId the game_id the id of the current game
     * @param username the username of the player whose properties are returned
     */

    public ArrayList<Property> getPropertiesByOwner(int gameId, String username) throws SQLException {
        ArrayList<Property> properties = new ArrayList<>();

        try {
            getConnection();
            cStmt = connection.prepareCall("{CALL property_get_by_owner(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, username);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                int propertyId = rs.getInt(1);
                String name = rs.getString(2);
                int price = rs.getInt(3);
                int position = rs.getInt(4);
                String categoryColor = rs.getString(5);
                String owner = rs.getString(6);

                properties.add(new Property(propertyId, name, price, position, categoryColor, owner));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return properties;
    }

    /**
     * updates the property in the database
     * @param prop the property that is updated
     * @param game_id the id of the current game
     */

    public void updateProperty(Property prop, int game_id) throws SQLException {
        try {
            getConnection();
            cStmt = connection.prepareCall("{call property_update(?, ?, ?, ?)}");
                cStmt.setInt(1, prop.getId());
                cStmt.setInt(2, game_id);
                cStmt.setBoolean(3, prop.isPawned());
                cStmt.setString(4, prop.getOwner());
                cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }
    }

    /**
     * deletes all gameProperties for the given game_id
     * @param game_id the id of the current game
     *
     */
    public void endGame(int game_id) throws SQLException {
        try {
            getConnection();
            cStmt = connection.prepareCall("{call property_clean(?)}");

                cStmt.setInt(1, game_id);
                cStmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }
    }
}

