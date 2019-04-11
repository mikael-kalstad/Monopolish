package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.database.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EntityTest {
    static Bank instance;

    @BeforeAll
    public static void setInstance() {
        DataSource.initialize();
        instance = new Bank(1);
    }

    @Test
    public void testTransferMoney() {
        Bank otherBank = new Bank(1);

        otherBank.transferMoney(instance, 500);

        assertEquals(500500, instance.getMoney());
        assertEquals(499500, otherBank.getMoney());
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

    @AfterAll
    public static void tearDown() {
        DataSource.getInstance().shutdown();
        instance = new Bank(1);
    }
}
