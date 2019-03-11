package com.teamfour.monopolish.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ConnectionPoolTest {

    private static ConnectionPool instance;

    @BeforeAll
    public static void init() {
        try {
            instance = ConnectionPool.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Instance is not null")
    public void testNULL() {
        assertNotNull(instance);
    }
}
