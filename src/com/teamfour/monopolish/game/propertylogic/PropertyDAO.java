package com.teamfour.monopolish.game.propertylogic;

import com.teamfour.monopolish.database.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;

public class PropertyDAO {
    Connection connection;
    CallableStatement cStmt;

    public ArrayList<Property> getAllProperties(int game_id){
        ArrayList<Property> props = null;
        Property temp;
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call create_property(?, ?)}");
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
        return (props);
    }

/*
    public ArrayList<Property> getAvailableProperties(){
        return null;
    }

    public ArrayList<Property> getPlayerProperties(){
        return null;
    }
*/

    public void updateProperty(Property prop){
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call update_property(?, ?, ?)}");
            //works with property id's being 10000 inkremented and 10 properties
            int prop_id = 10000;
            for(int i = 0; i<10; i++) {
                cStmt.setInt(1, prop.getId());
                cStmt.setBoolean(2, prop.isPawned());
                cStmt.setInt(3, prop.getOwner());
                cStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }


    private void releaseConnection() {
        ConnectionPool.getMainConnectionPool().releaseConnection(connection);
        try {
            cStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = null;
    }
}


