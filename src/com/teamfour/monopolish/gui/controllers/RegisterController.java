package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.Views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML
    TextField usernameInput;
    @FXML TextField emailInput;
    @FXML TextField passwordInput;
    @FXML Button signupBtn;

    private final String COLOR_NORMAL = "white";
    private final String COLOR_WARNING = "red";

    private void newBorderStyle(Node element, String color) {
        element.setStyle("-fx-border-color: " + color);
    }

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

    private boolean checkUsername() {
        // Database request here
        // Testing purposes
        return usernameInput.getText().equals("Mikael"); // Testing purposes
    }

    private boolean checkEmail() {
        // Database request here
        return emailInput.getText().equals("mikaelk@live.no"); // Testing purposes
    }

    public void register() {
        if (checkUsername() && checkEmail()) {
            System.out.println("Account created!");
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        } else {
            newBorderStyle(usernameInput, COLOR_WARNING);
            newBorderStyle(emailInput, COLOR_WARNING);
        }
    }
}
