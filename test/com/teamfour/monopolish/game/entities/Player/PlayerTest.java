package test.com.teamfour.monopolish.game.entities.Player;


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
        instance = new Player("Grethe", 5000, 0, false, false, 1, false);
    }

    @Test
    @DisplayName("move test")
    public static void moveTest(){
        instance.move(10);
        assertTrue(instance.getPosition() == 10);
    }

    @Test
    @DisplayName("moveTo test")
    public static void moveToTest(){
        instance.moveTo(30);
        assertTrue(instance.getPosition() == 35);
    }

    @Test
    @DisplayName("move Outside board test")
    public static void moveOutsideTest(){
        int startPos = instance.getPosition();
        try{
            instance.moveTo(36);
        }catch(IllegalArgumentException ie){
        }

        assertTrue(instance.getPosition() == startPos);
    }

}
