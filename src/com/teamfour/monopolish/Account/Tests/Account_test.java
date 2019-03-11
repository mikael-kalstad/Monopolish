package com.teamfour.monopolish.account.Tests;

import com.teamfour.monopolish.account.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;

public class Account_test {
    private Account instance = null;

    @BeforeAll
    public void setInstance() {
        LocalDate date = LocalDate.of(2019, Month.DECEMBER, 24);
        instance = new Account("johhnyBoi23", "johhny@gmail.com", date, 10500);
    }

    @AfterAll
    public void tearDownAll() {
        instance = null;
    }

    @Test
    //@DisplayName("Username")
    public void getUsername() {
        String result = instance.getUsername();
        String expected = "johhnyBoi23";
        assertEquals(result, expected);
    }

    @Test
    @DisplayName("Email")
    public void getEmail() {
        String result = instance.getEmail();
        String expected = "johhny@gmail.com";
        assertEquals(result, expected);
    }

    @Test
    @DisplayName("Date")
    public void getDate() {
        LocalDate result = instance.getRegDate();
        LocalDate expected = LocalDate.of(2019, Month.DECEMBER, 24);
        assertEquals(result, expected);
    }

    @Test
    @DisplayName("Get Highscore")
    public void getHighscore() {
        int result = instance.getHighscore();
        int expected = 10500;
        assertEquals(result, expected);
    }

    @Test
    @DisplayName("Set highscore")
    public void setHighscore() {
        instance.setHighscore(10000);
        int result = instance.getHighscore();
        int expected = 10000;
        assertEquals(result, expected);
    }
}
