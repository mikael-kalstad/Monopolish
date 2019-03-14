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

    public Account(String username, String email, LocalDate regDate) {
        this.username = username;
        this.email = email;
        this.regDate = regDate;
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

}
