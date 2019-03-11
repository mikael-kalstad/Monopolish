package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.view.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML
    TextField usernameInput;
    @FXML TextField emailInput;
    @FXML TextField passwordInput;
    @FXML
    Button signupBtn;

    public void logout() {
        Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }

    public void handleInputChange() {
        if (usernameInput.getText().trim().isEmpty() ||
                emailInput.getText().trim().isEmpty() ||
                passwordInput.getText().trim().isEmpty()) {
            signupBtn.setDisable(true);
        } else {
            signupBtn.setDisable(false);
        }
    }

    public void checkUsername() {
        // Database request here
    }

    public void checkEmail() {

    }

    public void register() {

    }
}
