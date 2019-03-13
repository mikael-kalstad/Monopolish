package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class DashboardController {
    @FXML Text username;

    @FXML public void initialize() {
        username.setText(Handler.getAccount().getUsername());
    }

    public void logout() {
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }

    public void play() {
        Handler.getSceneManager().setScene(ViewConstants.GAME.getValue());
    }
}
