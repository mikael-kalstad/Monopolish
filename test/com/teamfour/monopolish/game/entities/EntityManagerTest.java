package com.teamfour.monopolish.game.entities;


import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.Street;
import com.teamfour.monopolish.gui.Handler;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class EntityManagerTest {
    private static EntityManager instance = null;

    @BeforeAll
    public static void setInstance() {
        instance = new EntityManager();

        Street street_1 = new Street(1, "Testveien 123", 2000, 4, "#00685E", "", 0, 0, false);
        Street street_2 = new Street(1, "Duppen 45", 6000, 7, "#00685E", "", 0, 0, false);
        Street street_3 = new Street(1, "Kreaturstien 6", 1200, 10, "#00685E", "", 0, 0, false);
        Bank bank = new Bank();
        bank.addProperty(street_1);
        bank.addProperty(street_2);
        bank.addProperty(street_3);

        instance.setBank(bank);

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("helge"));
        players.add(new Player("sven"));
        players.add(new Player("bente"));
        players.add(new Player("ingrid"));
        instance.setPlayers(players);
    }

    @AfterAll
    public static void removeInstance() {
        instance = null;
    }

    @Test
    @Description("Player can be found by username")
    public void testGetPlayer() {
        Player player = instance.getPlayer("helge");
        assertNotNull(player);
    }

    @Test
    @Description("Money can be transferred from bank to one player")
    public void testTransferMoney() {
        int currBankMoney = instance.getBank().getMoney();
        instance.transferMoneyFromBank("helge", 2000);
        assertEquals(instance.getPlayer("helge").getMoney(), 2000);
        assertEquals(instance.getBank().getMoney(), currBankMoney - 2000);
    }

    @Test
    @Description("Money can be distributed to all players from bank")
    public void testDistributeMoney() {
        int currBankMoney = instance.getBank().getMoney();
        instance.distributeStartMoney(2000);
        assertEquals(instance.getPlayer("helge").getMoney(), 2000);
        assertEquals(instance.getPlayer("sven").getMoney(), 2000);
        assertEquals(instance.getPlayer("bente").getMoney(), 2000);
        assertEquals(instance.getPlayer("ingrid").getMoney(), 2000);
        assertEquals(instance.getBank().getMoney(), currBankMoney - 2000 * 4);
    }

    @Test
    @Description("Money can be transferred between players")
    public void testTransferMoneyFromTo() {
        instance.transferMoneyFromBank("helge", 5000);
        instance.transferMoneyFromTo("helge", "ingrid", 1500);
        assertEquals(instance.getPlayer("helge").getMoney(), 3500);
        assertEquals(instance.getPlayer("ingrid").getMoney(), 1500);
    }

    @Test
    @Description("Player can purchase property if enough money")
    public void testPurchaseProperty() {
        // Can afford
        Player buyer1 = instance.getPlayer("helge");
        Property property1 = instance.getPropertyAtPosition(4);
        instance.transferMoneyFromBank("helge", 5000);
        assertTrue(instance.purchaseProperty(buyer1, property1));
        assertEquals( 1, instance.getPlayer("helge").getProperties().size());

        // Can not afford
        Player buyer2 = instance.getPlayer("ingrid");
        Property property2 = instance.getPropertyAtPosition(7);
        assertFalse(instance.purchaseProperty(buyer2, property2));
        assertEquals( 0, instance.getPlayer("ingrid").getProperties().size());
    }

    @Test
    @Description("Your player can easily be found")
    public void testGetYou() {
        Account loggedInAccount = new Account("bente", "bente@hente.no", LocalDate.now(), 0, true);
        Handler.setAccount(loggedInAccount);
        assertEquals("bente", instance.getYou().getUsername());
        Handler.setAccount(null);
    }

    @Test
    @Description("Winner can be found if all but one player are bankrupt")
    public void testFindWinner() {
        // Finds winner
        instance.getPlayer("helge").setBankrupt(true);
        instance.getPlayer("sven").setBankrupt(true);
        instance.getPlayer("bente").setBankrupt(true);

        assertEquals("ingrid", instance.findWinner());

        // Will not find winner
        instance.getPlayer("sven").setBankrupt(false);
        assertNull(instance.findWinner());
    }
}
