package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.view.ViewConstants;

public class RegisterController {
    public void register() {
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
    }
}
