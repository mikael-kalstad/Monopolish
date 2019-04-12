package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.property.Boat;
import com.teamfour.monopolish.game.property.Street;
import com.teamfour.monopolish.game.property.Train;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyTest {

    private static Boat  boat= null;
    private static Street street = null;
    private static Train train = null;
    @BeforeAll
    public static void setProperties(){
       boat = new Boat(23, "KNM Helge Ingstad", 4000, 4, "#FFFFFF", "Grethe", false);
       street = new Street(22, "Kalvskinnet", 8000, 35, "#1565C0", "Grethe", 0, 0,false);
       train = new Train(27, "AtB", 3000, 34, "#00685E", "Nils", false);
    }

    @AfterAll
    public static void endTest(){
        boat = null;
        street = null;
        train = null;
    }

    @Test
    @DisplayName("getType test for all property types")
    public void getTypeTest(){
        assertTrue(boat.getType() == 1 && street.getType() == 0 && train.getType() == 2);
    }

    @Test
    @DisplayName("toString test")
    public void toStringTest(){
        assertTrue(boat.toString().equals("name: " + boat.getName() + "; Price: " + boat.getPrice() + "; Position: " + boat.getPosition()));
    }

    @Test
    @DisplayName("equals test")
    public void equalsTest(){
        Train tempTrain = new Train(27, "Hei", 200, 39, "#085E", "Nils", false);
        assertTrue(train.equals(tempTrain) && ! train.equals(boat) && train.equals(train) && ! train.equals(null));
    }
/*
    @Test
    @DisplayName("getFullColorset test")
    public void getFullColorsetTest(){
        //uses PropertyDAO.getColorSet() and so is DB related.
    }



*/

}
