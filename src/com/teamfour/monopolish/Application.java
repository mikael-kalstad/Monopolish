package com.teamfour.monopolish;

import com.teamfour.monopolish.database.ConnectionPool;

import java.sql.SQLException;

/**
 * Master class of Monopolish
 *
 * @author      Eirik Hemstad
 * @version     a_0.1
 */

public class Application {
    // Attributes
    String title;

    public Application(String title) {
        this.title = title;
    }

    public void start() {
        try {
            ConnectionPool.create();
        } catch (SQLException e) {
            System.out.println("Couldn't gain access to database server.");
        }
    }
}
