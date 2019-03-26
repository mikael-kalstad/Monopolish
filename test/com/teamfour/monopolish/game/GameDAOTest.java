package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class GameDAOTest {
    private static GameDAO instance;

    @BeforeAll
    public static void setInstance() {
        instance = new GameDAO();
        try {
            ConnectionPool.create();
        } catch (SQLException e) {

        }

    }

    @Test
    public void testInsert() {
        assertEquals(1, instance.insertGame(1));
    }

    @Test
    public void testInsertNoLobby() {
        assertEquals(-1, instance.insertGame(8));
    }

    @Test
    public void testGetCurrentPlayer() {
        try {
            assertEquals(-1, instance.getCurrentPlayer(4));
            assertEquals(15, instance.getCurrentPlayer(5));
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testFinishGame() {
        try {
            assertTrue(instance.finishGame(4, 13));
            assertTrue(instance.finishGame(5, 0));
        } catch (SQLException e){
        }
    }
}
