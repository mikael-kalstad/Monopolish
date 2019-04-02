package com.teamfour.monopolish.game.propertylogic;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.*;
import java.util.ArrayList;

/**
 * Handles Property-DB connection and methods
 *
 *
 * @author      lisawil
 * @version     1.1
 */

public class PropertyDAO extends DataAccessObject {
    /**
     * creates gameproperties in the database and Property objects for the game.
     * @param game_id the id of the current game
     */
    public ArrayList<Property> getAllProperties(int game_id) throws SQLException {
        ArrayList<Property> props = new ArrayList<>();
        try {
            getConnection();
            cStmt = connection.prepareCall("{call property_get_all(?)}");

            cStmt.setInt(1, game_id);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                Property property = getPropertyFromResultSet(rs);
                props.add(property);
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
        ArrayList<Property> props = new ArrayList<>();

        try {
            getConnection();
            cStmt = connection.prepareCall("{CALL property_get_by_owner(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, username);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                Property property = getPropertyFromResultSet(rs);
                props.add(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return props;
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

    // HELPER METHODS

    /**
     * Takes an MySQL ResultSet and inserts the data into a Property object.
     * USE WITH CAUTION!! The inserted resultset must be have 7 columns of data
     * @param rs ResultSet to extract from
     * @return Resulting property
     * @throws SQLException
     */
    private Property getPropertyFromResultSet(ResultSet rs) throws SQLException {
        // Get all attributes from resultset
        int propertyId = rs.getInt(1);
        String name = rs.getString(2);
        int price = rs.getInt(3);
        int position = rs.getInt(4);
        String categoryColor = rs.getString(5);
        String owner = rs.getString(6);
        int propertyType = rs.getInt(7);

        // Check which type of property this is and cast accordingly
        Property property;
        if (propertyType == Property.STREET)
            property = new Street(propertyId, name, price, position, categoryColor, owner);
        else if (propertyType == Property.BOAT)
            property = new Boat(propertyId, name, price, position, categoryColor, owner);
        else
            property = new Train(propertyId, name, price, position, categoryColor, owner);

        return property;
    }
}