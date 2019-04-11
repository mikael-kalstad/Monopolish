package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.game.GameDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class EntityManagerTest {
    private static EntityManager instance = null;

    @BeforeAll
    public static void setInstance(){
        Account huey = new Account("huey", "huey@duck.no", LocalDate.now(), 0, true);
        Account dewey = new Account("dewey", "dewey@duck.no", LocalDate.now(), 0, true);
        Account louie = new Account("louie", "louie@duck.no", LocalDate.now(), 0, true);
        AccountDAO accountDAO = new AccountDAO();


        accountDAO.insertAccount(huey, "brorNr1");
        accountDAO.insertAccount(dewey, "brorNr2");
        accountDAO.insertAccount(louie, "brorNr3");
        GameDAO game = new GameDAO();
        game.insertGame(1);
        game.insertGame(1);
        int gameId =  game.insertGame(1);
        instance = new EntityManager(gameId);
        instance.updateFromDatabase();
    }

    @Test
    @DisplayName("getPlayer test")
    public void testGetPlayer(){
        assertTrue(instance.getPlayer("huey") instanceof Player);
    }

//    @Test
//    @DisplayName("getPropertyAtPosition test")
//    public void testGetPropertyAtPosition(){
//        Property prop = new Street(22, "Kalvskinnet", 8000, 36, "#1565C0", null);
//        assertTrue(instance.getPropertyAtPosition(36).getId() == prop.getId()
//                && instance.getPropertyAtPosition(36).getPrice() == prop.getPrice()
//                && instance.getPropertyAtPosition(36).getCategorycolor() == prop.getCategorycolor()
//                && instance.getPropertyAtPosition(36).getName().equals(prop.getName()));
//    }
 /*   @Test
    public void testTransferMoneyFromBank(){

    }
    @Test
    public void testDistributeMoneyFromBank(){

    }
    @Test
    public void testGetOwnerAtProperty(){

    }
    @Test
    public void testTransactProperty(){

    }
    @Test
    public void testUpdateFromDatabase(){

    }
    @Test
    public void testUpdateToDatabase(){

    }
    @Test
    public void testGenerateTurnOrder(){

    }
    @Test
    public void testToString(){

    }
    @Test
    public void testRemovePlayer(){

    }
 */
    @AfterAll
    public static void slutt(){
        instance=null;

    }

}
