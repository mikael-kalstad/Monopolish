package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private Text text_warning;
    @FXML private Button loginBtn;

    private final String COLOR_NORMAL = "white";
    private final String COLOR_WARNING = "red";

    private void newBorderStyle(Node element, String color) {
        element.setStyle("-fx-border-color: " + color);
    }

    public void handleInputChange() {
        if (!usernameInput.getText().isEmpty() && !passwordInput.getText().isEmpty()) {
            loginBtn.setDisable(false); // Enable button
        } else {
            loginBtn.setDisable(true);
        }
    }

    public void login() {
        // Database query for checking username/email and password combination
        boolean res = false;

        // Testing purposes
        if (usernameInput.getText().trim().equals("Mikael")
                && passwordInput.getText().trim().equals("1234")) {
            res = true;
        }

        System.out.println(
            "logging in... \n" +
            "username: " + usernameInput.getText() + "\n" +
            "password: " + passwordInput.getText()
        );

        // If username/email and password is correct, from the request to the database.
        if (res) {
            // Change styling to normal
            newBorderStyle(usernameInput, COLOR_NORMAL);
            newBorderStyle(passwordInput, COLOR_NORMAL);
            text_warning.setVisible(false); // Hide warning text

            // Switch to dashboard screen
            System.out.println(usernameInput.getText() + " you are logged in");
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        } else {
            // Change styling to warning
            newBorderStyle(usernameInput, COLOR_WARNING);
            newBorderStyle(passwordInput, COLOR_WARNING);
            text_warning.setVisible(true); // Show warning text
        }
    }

    public void register() {
        // Go to register view
        Handler.getSceneManager().setScene(ViewConstants.REGISTER.getValue());
    }
}
