package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.Dice;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiceTest {
    private static Dice instance = null;
    private static Dice instance2 = null;

    @BeforeAll
    public static void setInstance() {
        instance = new Dice(2, 6);
        instance2 = new Dice(3, 8);
    }

    @AfterAll
    public static void tearDown() {
        instance = null;
    }

    @Test
    @DisplayName("2 Normal dices with 6 eyes)")
    public void throwNormalDice() {
        int[] result = instance.throwDice();
        assertTrue(result[0] >= 1 && result[0] <= 6
                && result[1] >= 1 && result[1] <= 6);
    }

    @Test
    @DisplayName("3 Normal dices with 7 eyes)")
    public void throwSpecialDice() {
        int[] result = instance2.throwDice();
        assertTrue(result[0] >= 1 && result[0] <= 7
                && result[1] >= 1 && result[1] <= 7
                && result[2] >= 1 && result[2] <= 7);
    }

    @Test
    public void testThrowDice() {
        int[] result = null;

        for (int i = 0; i < 1000; i++) {
            result = instance.throwDice();
            if (result[0] == result[1]) {
                System.out.println("Two equal dices at i=" + i);
            }
        }
    }
}
