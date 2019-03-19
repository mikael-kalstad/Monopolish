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
    public void testTransferProperty2() {
        Bank otherBank = new Bank(1);

        assertFalse(instance.transferProperty(otherBank, 0));
    }
}
