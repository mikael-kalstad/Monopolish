package com.teamfour.monopolish.account;

import java.time.LocalDate;

/**
 * Class with info about the user
 *
 * @author Mikael Kalstad
 * @version 1.0
 */

public class Account {
    private String username;
    private String email;
    private LocalDate regDate;
    private int highscore;

    public Account(String username, String email, LocalDate regDate, int highscore) {
        this.username = username;
        this.email = email;
        this.regDate = regDate;
        this.highscore = highscore;
    }

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
