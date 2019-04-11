package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlayerTest {

    private static Player instance = null;

    @BeforeAll
    public static void setInstance(){
        instance = new Player("Grethe", 5000, 1, false, false, 1, false);
    }

    @Test
    @DisplayName("move test")
    public void moveTest(){
        instance.moveTo(0);
        instance.move(10);
        assertTrue(instance.getPosition() == 10);
    }

    @Test
    @DisplayName("moveTo test")
    public void moveToTest(){
        instance.moveTo(35);
        assertTrue(instance.getPosition() == 35);
    }

    @Test
    @DisplayName("move Outside board test")
    public void moveOutsideTest(){
        int startPos = instance.getPosition();
        try{
            instance.moveTo(36);
        }catch(IllegalArgumentException ie){
        }

        assertTrue(instance.getPosition() == startPos);
    }

}
