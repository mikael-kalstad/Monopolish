package com.teamfour.monopolish.game.propertylogic;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.database.DataAccessObject;
import com.teamfour.monopolish.game.entities.Entity;
import com.teamfour.monopolish.game.entities.player.Player;

import java.sql.*;
import java.util.ArrayList;

public class PropertyDAO extends DataAccessObject {
    Connection connection;
    CallableStatement cStmt;
    private int game_id;

    public ArrayList<Property> getAllProperties(int game_id){
        this.game_id = game_id;
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
                //props.add(new Property(rs.getInt("property_id"), rs.getString("name"), rs.getInt("price"), rs.getInt("position"), rs.getString("categorycolor")));

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


    public void updateProperty(Property prop, Entity entity){
        String username;
        if(!(entity instanceof Player)){
            username = null;
        }else{
            Player player = (Player) entity;
            username = player.getUsername();
        }

        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call property_update(?, ?, ?, ?)}");
            //works with property id's being 10000 inkremented and 10 properties
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


    public void endGame(){
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


