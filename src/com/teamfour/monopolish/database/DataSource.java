package com.teamfour.monopolish.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.teamfour.monopolish.util.Properties;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class creates a pooled datasource which hands out connections to objects requesting access.
 * Use connection.close() to release the connection
 *
 * @author      eirikhem
 */

public class DataSource {
    // Attributes
    private ComboPooledDataSource pool;

    // Static data source
    private static DataSource dataSource;

    /**
     * Constructor
     */
    public DataSource() {
        pool = new ComboPooledDataSource();
        try {
            pool.setDriverClass("com.mysql.cj.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        try {
            pool.setJdbcUrl(Properties.getDatabaseURL());
            pool.setUser(Properties.getDatabaseUser());
            pool.setPassword(Properties.getDatabasePassword());
        } catch (IOException e) {
            e.printStackTrace();
        }

        pool.setMinPoolSize(1);
        pool.setAcquireIncrement(2);
        pool.setMaxPoolSize(5);
    }

    /**
     * Get a connection from the pool
     * @return First available connection
     */
    public Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Initializes the connection pool
    public static void initialize() {
        if (dataSource == null)
            dataSource = new DataSource();
    }

    // Closes the connection pool
    public void shutdown() {
        pool.close();
    }

    // GETTERS

    public static DataSource getInstance() {
        return dataSource;
    }
}
