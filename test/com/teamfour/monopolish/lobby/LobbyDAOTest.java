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
    public void testInsert() {
        try {
            assertEquals(2, instance.insertLobby(1));
            assertEquals(2, instance.insertLobby(15));
            assertEquals(2, instance.insertLobby(20));
            assertEquals(3, instance.insertLobby(21));
            assertEquals(3, instance.insertLobby(22));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteUser() {
        try {
            assertTrue(instance.deleteUserFromLobby(2, 22));
        } catch (SQLException e) {
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
