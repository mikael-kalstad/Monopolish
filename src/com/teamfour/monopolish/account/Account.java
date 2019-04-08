package com.teamfour.monopolish.account;

import java.time.LocalDate;

/**
 * Class with info about the user
 *
 * @author Mikael Kalstad
 * @version 1.0
 */

public class Account {
    // Attributes
    private String username;
    private String email;
    private LocalDate regDate;
    private int highscore;
    private boolean active;

    /**
     * Constructor
     * @param username Display name
     * @param email Email
     * @param regDate Date of registration
     * @param highscore Personal high score
     * @param active Active
     */
    public Account(String username, String email, LocalDate regDate, int highscore, boolean active) {
        this.username = username;
        this.email = email;
        this.regDate = regDate;
        this.highscore = highscore;
        this.active = active;
    }

    // SETTERS & GETTERS

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int newHighscore) {
        this.highscore = newHighscore;
    }
}
