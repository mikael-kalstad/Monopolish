package com.teamfour.monopolish.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class for all Data Access Objects in the application. DAO's use a connection and a callable statement
 * (using MySQL routines) to connect to the database and perform various actions. Each table has its own DAO class
 *
 * @author      Eirik Hemstad
 * @version     1.1
 */

public abstract class DataAccessObject {
    /**
     * Gets a connection from the main connection pool
     */
    protected Connection getConnection() {
        return DataSource.getInstance().getConnection();
    }

    /**
     * Releases the current connection and closes the using statement
     */
    protected void releaseConnection(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Properly closes a database resource
     * @param object Resource to close
     */
    protected void close(Object object) {
        if (object == null) return;
        try {
            if (object instanceof ResultSet) {
                ((ResultSet) object).close();
            } else if (object instanceof CallableStatement) {
                ((CallableStatement)object).close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
