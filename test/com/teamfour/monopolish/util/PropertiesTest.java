package com.teamfour.monopolish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PropertiesTest {

    @Test
    public void TestGetDatabaseUrl() {
        try {
            assertEquals("jdbc:mysql://mysql.stud.iie.ntnu.no:3306/eirikhem", Properties.getDatabaseURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestGetUsername() {
        try {
            assertEquals("eirikhem", Properties.getDatabaseUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
