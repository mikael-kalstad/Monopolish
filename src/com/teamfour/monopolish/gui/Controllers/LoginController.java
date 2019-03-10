package com.teamfour.monopolish.gui.Controllers;

import com.teamfour.monopolish.gui.View.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;

    private final String STYLE_NORMAL = "login";
    private final String STYLE_WARNING = "login-warning";

    private void removeStyle(Node element, String css_class) {
        element.getStyleClass().remove(css_class);
    }
    private void addStyle(Node element, String css_class) {
        element.getStyleClass().add(css_class);
    }

    public void login() {
        // Database query for checking username/email and password combination
        boolean res = false;

        // Testing purposes
        if (usernameInput.getText().trim().equals("Mikael") && passwordInput.getText().trim().equals("1234")) {
            res = true;
        }

        System.out.println(
            "logging in... \n" +
            "username: " + usernameInput.getText() + "\n" +
            "password: " + passwordInput.getText()
        );

        // If username/email and password is okay.
        if (res) {
            // Switch to dashboard screen


            removeStyle(usernameInput, STYLE_WARNING);
            removeStyle(passwordInput, STYLE_WARNING);
            addStyle(usernameInput, STYLE_NORMAL);
            addStyle(passwordInput, STYLE_NORMAL);

            System.out.println(usernameInput.getText() + " you are logged in");
            System.out.println("Switching to dashboard...");
            Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
        } else {
            // Remove normal styling and add warning style
            removeStyle(usernameInput, STYLE_NORMAL);
            removeStyle(passwordInput, STYLE_NORMAL);
            addStyle(usernameInput, STYLE_WARNING);
            addStyle(passwordInput, STYLE_WARNING);

            System.out.println("Login failed, username/email or password is wrong");
        }
    }

    public void register() {
        // Go to register
        Handler.getSceneManager().setScene(ViewConstants.REGISTER.getValue());
    }
}
