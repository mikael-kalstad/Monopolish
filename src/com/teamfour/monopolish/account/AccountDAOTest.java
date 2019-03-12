package com.teamfour.monopolish.account;

import com.teamfour.monopolish.database.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AccountDAOTest {
    private static AccountDAO instance;

    @BeforeAll
    public static void setInstance() {
        instance = new AccountDAO();
        try {
            ConnectionPool.create();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertAccount() {
        Account user = new Account("eirikhem", "eirik@eirik.eirik", LocalDate.now(), 0);
        String password = "Hahahaha";
        try {
            assertFalse(instance.insertAccount(user, password));
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    @Test
    public void testLogin() {
        try {
            assertNotNull(instance.getAccountByCredentials("eirikhem", "Hahahaha"));
            assertNull(instance.getAccountByCredentials("eirikhem", "hahahaha"));
        }
        catch (SQLException e) { e.printStackTrace(); }
    }
}
