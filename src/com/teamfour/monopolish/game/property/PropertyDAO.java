package com.teamfour.monopolish.game.property;

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
     * @return ArrayList with all the Properties
     * @throws SQLException
     */
    public ArrayList<Property> getAllProperties(int game_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<Property> props = new ArrayList<>();
        try {
            cStmt = connection.prepareCall("{call property_get_all(?)}");

            cStmt.setInt(1, game_id);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    Property property = getGamePropertyFromResultSet(rs);
                    props.add(property);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return (props);
    }

    /**
     * gets gameproperties from the database and generates Property objects related to the parameter username.
     * @param gameId the game_id the id of the current game
     * @param username the username of the player whose properties are returned
     * @return ArrayList of properties owned by the player
     * @throws SQLException
     */
    public ArrayList<Property> getPropertiesByOwner(int gameId, String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<Property> props = new ArrayList<>();
        try {
            cStmt = connection.prepareCall("{CALL property_get_by_owner(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    Property property = getGamePropertyFromResultSet(rs);
                    props.add(property);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return props;
    }

    /**
     * updates the property in the database
     * @param prop the property that is updated
     * @param game_id the id of the current game
     * @throws SQLException
     */
    public void updateProperty(Property prop, int game_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call property_update(?, ?, ?, ?, ?)}");
                cStmt.setInt(1, prop.getId());
                cStmt.setInt(2, game_id);
                cStmt.setBoolean(3, prop.isPawned());
                cStmt.setString(4, prop.getOwner());
                int rentLevel = 0;
                if (prop.getType() == Property.STREET)
                    rentLevel = ((Street)prop).getHouseAndHotels();

                cStmt.setInt(5, rentLevel);

                cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     * deletes all gameProperties for the given game_id
     * @param game_id the id of the current game
     * @throws SQLException
     */
    public void endGame(int game_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call property_clean(?)}");

            cStmt.setInt(1, game_id);
            cStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }


    /**
     * deletes all gameProperties for the given game_id
     * @param gameId the id of the current game
     * @param colorHex the hexCode of the color
     * @return ArrayList of Properties of the given color
     * @throws SQLException
     */
    public ArrayList<Property> getColorSet(int gameId, String colorHex) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<Property> properties = new ArrayList<>();
        try {
            cStmt = connection.prepareCall("{call property_get_color_set(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, colorHex);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    properties.add(getGamePropertyFromResultSet(rs));
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return properties;
    }

    // HELPER METHODS

    /**
     * Takes an MySQL ResultSet and inserts the data into a Property object.
     * USE WITH CAUTION!! The inserted resultset must have 8 columns of data
     * @param rs ResultSet to extract from
     * @return Resulting property
     * @throws SQLException
     */
    private Property getGamePropertyFromResultSet(ResultSet rs) throws SQLException {
        // Get all attributes from resultset
        int propertyId = rs.getInt(1);
        String name = rs.getString(2);
        int price = rs.getInt(3);
        int position = rs.getInt(4);
        String categoryColor = rs.getString(5);
        String owner = rs.getString(6);
        int propertyType = rs.getInt(7);
        int houses = rs.getInt(8);
        int hotels = 0;
        if (houses > 4) {
            houses = 4;
            hotels = 1;
        }

        // Check which type of property this is and cast accordingly
        Property property;
        if (propertyType == Property.STREET)
            property = new Street(propertyId, name, price, position, categoryColor, owner, houses, hotels);
        else if (propertyType == Property.BOAT)
            property = new Boat(propertyId, name, price, position, categoryColor, owner);
        else
            property = new Train(propertyId, name, price, position, categoryColor, owner);

        return property;
    }
}