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
     *
     *
     */
    public ArrayList<Property> getAllProperties(int game_id){
        ArrayList<Property> props = null;
        Property temp;
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call property_create(?, ?)}");
            ResultSet rs;
            //works with property id's being 10000 inkremented and 10 properties
            int prop_id = 10000;
            for(int i = 0; i<10; i++) {
                cStmt.setInt(1, game_id);
                cStmt.setInt(2, prop_id);

                rs =  cStmt.executeQuery();
                props.add(new Property(rs.getInt("property_id"), rs.getString("name"), rs.getInt("price"), rs.getInt("position"), rs.getString("categorycolor")));

                //ikke helt ideelt?
                rs.close();
                prop_id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
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


