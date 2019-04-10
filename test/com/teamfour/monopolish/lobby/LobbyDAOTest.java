package com.teamfour.monopolish.lobby;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.database.DataSource;
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
        DataSource.initialize();
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

        assertTrue(instance.deleteLobby(1));
    }
    @Test
    public void testGetUsersInLobby() {
            assertEquals(0, instance.getUsersInLobby(1).size());
    }
}
