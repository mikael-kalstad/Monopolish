package com.teamfour.monopolish.database;

import com.teamfour.monopolish.util.Properties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a connection pool used for the application. The default size of the pool is 10.
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

public class ConnectionPool {
    private String url;
    private String username;
    private String password;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 4;
    private static int MAX_POOL_SIZE = 10;

    private static ConnectionPool mainConnectionPool;

    /**
     * Creates a new connection pool
     * @return Creates the connection pool
     * @throws SQLException
     */
    public static void create() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url;
        String username;
        String password;
        try {
            url = Properties.getDatabaseURL();
            username = Properties.getDatabaseUser();
            password = Properties.getDatabasePassword();
        } catch (IOException e) {
            return;
        }

        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, username, password));
        }
        mainConnectionPool = new ConnectionPool(url, username, password, pool);
    }

    /**
     * Constructor
     * @param url Url of the connection
     * @param user Username of the connection
     * @param password Password for the connection
     * @param pool The connection pool object
     */
    public ConnectionPool(String url, String user, String password, List<Connection> pool) {
        this.url = url;
        this.username = user;
        this.password = password;
        this.connectionPool = pool;
    }

    /**
     * Creates a new connection
     * @param url
     * @param user
     * @param password
     * @return
     * @throws SQLException
     */
    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Retrieves a connection from the pool
     * @return An available connection
     */
    public Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(url, username, password));
            } else {
                throw new RuntimeException("Maximum pool size reached, no available connections!");
            }
        }
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        if (connection == null)
            return false;
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    /**
     * Shutdowns the entire connection pool and closes every connection
     * @throws SQLException
     */
    public void shutdown() throws SQLException {
        usedConnections.forEach(this::releaseConnection);
        for(Connection c : connectionPool) {
            c.close();
        }

        connectionPool.clear();
    }

    // GETTERS & SETTERS

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static ConnectionPool getMainConnectionPool() { return mainConnectionPool; }
}
