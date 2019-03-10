package com.teamfour.monopolish.gui.Controllers;

import com.teamfour.monopolish.gui.View.ViewConstants;

public class RegisterController {
    public void register() {
        Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
    }
}
