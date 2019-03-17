package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.propertylogic.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class EntityTest {
    static Bank instance;

    @BeforeAll
    public static void setInstance() {
        instance = new Bank(1);
        instance.adjustMoney(2000);
    }

    @Test
    public void testTransferMoney() {
        Bank otherBank = new Bank(1);
        otherBank.adjustMoney(2000);

        otherBank.transferMoney(instance, 500);

        assertEquals(2500, instance.getMoney());
        assertEquals(1500, otherBank.getMoney());
    }

    @Test
    public void testTransferMoney2() {
        Bank otherBank = new Bank(1);

        assertFalse(otherBank.transferMoney(instance, 500));
    }

    @Test
    public void testTransferProperty() {
        Bank otherBank = new Bank(1);
        instance.getProperties().add(new Property(1, "Test", 2000, 4, "Red"));

        instance.transferProperty(otherBank, 0);

        System.out.println(otherBank.getProperties().get(0));
        assertEquals(1, otherBank.getProperties().size());
        assertEquals(0, instance.getProperties().size());
    }

    @Test
    public void testTransferProperty2() {
        Bank otherBank = new Bank(1);

        assertFalse(instance.transferProperty(otherBank, 0));
    }
}
