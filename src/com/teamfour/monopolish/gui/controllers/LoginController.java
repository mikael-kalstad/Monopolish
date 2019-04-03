package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * Controller class for registration view
 *
 * @author Mikael Kalstad
 * @version 1.5
 */
public class LoginController {
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private Text msg;

    // Color constants
    private final String COLOR_NORMAL = "white";
    private final String COLOR_REQUIRED = "orange";
    private final String COLOR_WARNING = "red";

    // Msg constants
    private final String MSG_REQUIRED = "*Field is required";
    private final String MSG_WARNING = "*Username/email or password is wrong";
    private final String MSG_DATABASE_ERROR = "*Database error, try again";
    private final String MSG_USER_LOGGED_IN_ERROR = "*User already logged in";

    // TESTING
    @FXML private Pane chanceContainer;

    @FXML public void initialize() {
        ChanceCardController.setup(chanceContainer);
        ChanceCardController.display("You can use this card to get out of jail boy", "file:res/gui/MessagePopup/bird.png");
    }

    /**
     * Method to change styling to an input
     *
     * @param element Input target element
     * @param color New color
     */
    private void setBorderStyle(TextField element, String color) {
        element.setStyle(
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-background-color: transparent;" +
                "-fx-text-inner-color: " + COLOR_NORMAL + ";");
    }

    /**
     * Method to change color of a Text element
     *
     * @param element Text target element
     * @param color New color
     */
    private void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
    }

    /**
     * Check if any inputs are empty
     *
     * @return true if any inputs are empty, false if all inputs are not empty
     */
    private boolean inputsEmpty() {
        return (usernameInput.getText().trim().isEmpty() && !passwordInput.getText().trim().isEmpty());
    }

    /**
     * Check a input for requirements (not empty) and warnings (specified in parameters),
     * and change styling accordingly.
     *
     * @param input A Textfield that will be checked
     * @param textElement A Text that will have styling applied
     * @param warning If true show warning msg
     */
    private void checkInput(TextField input, Text textElement, boolean warning, boolean dbError) {
        // Required styling
        if (input.getText().trim().isEmpty()) {
            setBorderStyle(input, COLOR_REQUIRED);
            textElement.setText(MSG_REQUIRED);
            setTextColor(textElement, COLOR_REQUIRED);
            textElement.setVisible(true);
        }
        // Database error
        else if (dbError) {
            setBorderStyle(input, COLOR_WARNING);
            textElement.setText(MSG_DATABASE_ERROR);
            setTextColor(textElement, COLOR_WARNING);
            textElement.setVisible(true);
        }
        // Warning styling
        else if (warning) {
            setBorderStyle(input, COLOR_WARNING);
            textElement.setText(MSG_WARNING);
            setTextColor(textElement, COLOR_WARNING);
            textElement.setVisible(true);
        }
        // Normal styling
        else {
            setBorderStyle(input, COLOR_NORMAL);
            if (inputsEmpty()) textElement.setVisible(false);
        }
    }

    /**
     * Method that will be called when the login button in the login view is clicked.
     * It will performs checks to make sure all requirements are met before logging in.
     * <br/><br/>
     * <b>Requirements for a login to succeed:</b>
     * <ul>
     *     <li>1. All inputs are not empty</li>
     *     <li>2. Username/email and password is correct (db check)</li>
     * </ul>
     *
     * <br/>
     *
     * <b>If login is successful:</b>
     * <ul>
     *     <li>1. Update/save account obj in Handler</li>
     *     <li>2. Switch to dashboard view</li>
     * </ul>
     */
    public void login() {
        Account res;
        boolean canLogin = false;
        boolean dbError = false;

        // Try to register if inputs are not empty
        if (!inputsEmpty()) {
            //Check if user is already logged in
//                if (Handler.getAccountDAO()) {
//                    msg.setText(MSG_USER_LOGGED_IN_ERROR);
//                    setTextColor(msg, COLOR_WARNING);
//                }

            try {
                ConnectionPool.create();
                res = Handler.getAccountDAO().getAccountByCredentials(usernameInput.getText(), passwordInput.getText());

                // Username/email and password is correct
                if (res != null) {
                    Handler.setAccount(res); // Set local account object
                    Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
                    canLogin = true;
                    dbError = false;
                }
            }
            catch (Exception e) {
                dbError = true;
            }
        }

        // Check inputs
        checkInput(usernameInput, msg, !canLogin, dbError);
        checkInput(passwordInput, msg, !canLogin, dbError);
    }

    // Go to register view
    public void register() {
        Handler.getSceneManager().setScene(ViewConstants.REGISTER.getValue());
    }
}
