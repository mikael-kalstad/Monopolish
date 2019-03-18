package com.teamfour.monopolish.game.propertylogic;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.database.DataAccessObject;
import com.teamfour.monopolish.game.entities.Entity;
import com.teamfour.monopolish.game.entities.player.Player;

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
    //Attributes
    Connection connection;
    CallableStatement cStmt;


    /**
     * creates gameproperties in the database and Property objects for the game.
     * @param game_id the game_id the id of the current game
     *
     */
    public ArrayList<Property> getAllProperties(int game_id) throws SQLException {
        ArrayList<Property> props = null;

        connection = ConnectionPool.getMainConnectionPool().getConnection();
        cStmt = connection.prepareCall("{call property_get_all(?)}");

        cStmt.setInt(1, game_id);

        ResultSet rs = cStmt.executeQuery();

        while(rs.next()) {
            int propertyId = rs.getInt(1);
            String name = rs.getString(2);
            int price = rs.getInt(3);
            int position = rs.getInt(4);
            String categoryColor = rs.getString(5);

            props.add(new Property(propertyId, name, price, position, categoryColor));
        }

        releaseConnection();

        return (props);
    }

    /**
     * updates owner and/or pawned-state of a property
     *
     *
     */

    public void updateProperty(Property prop, Entity entity, int game_id){
        String username;
        if(!(entity instanceof Player)){
            username = null;
        }else {
            Player player = (Player) entity;
            username = player.getUsername();
        }

        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call property_update(?, ?, ?, ?)}");
            for(int i = 0; i<10; i++) {
                cStmt.setInt(1, prop.getId());
                cStmt.setInt(2, game_id);
                cStmt.setBoolean(3, prop.isPawned());
                cStmt.setString(4, username);
                cStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }

    /**
     * deletes all gameProperties for the given game_id
     *
     *
     */
    public void endGame(int game_id){
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call property_clean(?)}");

                cStmt.setInt(1, game_id);
                cStmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }

/*
    public ArrayList<Property> getAvailableProperties(){
        return null;
    }

    public ArrayList<Property> getPlayerProperties(){
        return null;
    }
*/
}


