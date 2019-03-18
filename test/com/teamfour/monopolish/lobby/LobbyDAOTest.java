package com.teamfour.monopolish.lobby;

import com.teamfour.monopolish.database.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class LobbyDAOTest {
    private static LobbyDAO instance;

    @BeforeAll
    public static void setInstance() {
        instance = new LobbyDAO();
        try {
            ConnectionPool.create();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertLobby() {
        try {
            instance.deleteLobby(1);
            instance.insertLobby("helgeingstad");
            instance.insertLobby("giske");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteLobby() {
        try {
            assertTrue(instance.deleteLobby(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsersInLobby() {
        try {
            assertEquals(0, instance.getUsersInLobby(1).size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
