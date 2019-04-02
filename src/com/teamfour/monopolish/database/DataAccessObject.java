package com.teamfour.monopolish.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract class for all Data Access Objects in the application. DAO's use a connection and a callable statement
 * (using MySQL routines) to connect to the database and perform various actions. Each table has its own DAO class
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

public abstract class DataAccessObject {
    // Attributes
    protected Connection connection;
    protected CallableStatement cStmt;

    /**
     * Gets a connection from the main connection pool
     */
    protected void getConnection() {
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
        } catch (SQLException e) {
            // TODO: SOmething
            e.printStackTrace();
        }

    }

    /**
     * Releases the current connection and closes the using statement
     */
    protected void releaseConnection() {
        ConnectionPool.getMainConnectionPool().releaseConnection(connection);
        try {
            cStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = null;
    }
}
