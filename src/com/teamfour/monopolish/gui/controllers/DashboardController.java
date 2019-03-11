package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.view.ViewConstants;

public class DashboardController {
    public void logout() {
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }
}
