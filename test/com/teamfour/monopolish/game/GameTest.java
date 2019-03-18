package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class GameTest {
    static GameLogic instance;

    @BeforeAll
    public static void setInstance() {
        try {
            ConnectionPool.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = new GameLogic(1);
    }

    @Test
    public void testRun() {
        try {
            instance.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

