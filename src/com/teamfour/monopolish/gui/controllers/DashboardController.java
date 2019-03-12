package com.teamfour.monopolish.gui.Controllers;

import com.teamfour.monopolish.gui.Views.ViewConstants;

public class DashboardController {
    public void logout() {
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }
}
