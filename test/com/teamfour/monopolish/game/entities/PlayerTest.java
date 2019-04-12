package com.teamfour.monopolish.game.entities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private static Player instance = null;

    @BeforeAll
    public static void setInstance(){
        instance = new Player("Grethe", 5000, 0, false, false, 1, false);
    }

    @AfterAll
    public static void tearDown(){
        instance = null;
    }

    @Test
    @DisplayName("move test")
    public void moveTest() {
        instance.moveTo(0);
        instance.move(10);
        assertEquals(10, instance.getPosition());
    }

    @Test
    @DisplayName("Test move around board")
    public void movePastStart() {
        instance.moveTo(0);
        instance.move(38);
        assertEquals(2, instance.getPosition());
    }

    @Test
    @DisplayName("moveTo test")
    public void moveToTest() {
        instance.move(0);
        instance.moveTo(30);
        assertEquals(30, instance.getPosition());
    }

    @Test
    @DisplayName("move Outside board test")
    public void moveOutsideTest(){
        int startPos = instance.getPosition();
        try{
            instance.moveTo(36);
        }catch(IllegalArgumentException ie){
        }

        assertEquals(instance.getPosition(), startPos);
    }
}
