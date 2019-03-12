package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * Controller class for registration view
 *
 * @author Mikael Kalstad
 * @version 1.3
 */

public class RegisterController {
    @FXML private TextField usernameInput;
    @FXML private TextField emailInput;
    @FXML private TextField passwordInput;
    @FXML private TextField passwordRepeatInput;
    @FXML private Text usernameMsg;
    @FXML private Text emailMsg;
    @FXML private Text passwordMsg;
    @FXML private Text passwordRepeatMsg;

    // Color constants
    private final String COLOR_NORMAL = "white";
    private final String COLOR_REQUIRED = "orange";
    private final String COLOR_WARNING = "red";

    // Msg constants
    private final String MSG_REQUIRED = "*Field is required";
    private final String MSG_PASSWORD_WARNING = "*Check password requirements";
    private final String MSG_PASSWORDREPEAT_WARNING = "*Passwords do not match";
    private final String MSG_USERNAME_WARNING = "*Username is taken, choose a different one";
    private final String MSG_EMAIL_WARNING = "*Email is already registered";

    private final int MIN_PASSWORD_LENGTH = 6;

    private void setBorderStyle(Node element, String color) {
        element.setStyle("-fx-border-color: " + color);
    }

    private void setTextColor(Text element, String color) {
        //element.setStyle("-fx-text-inner-color: color");
        element.setFill(Paint.valueOf(color));
    }

    public void logout() {
        com.teamfour.monopolish.gui.controllers.Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }

    /**
     * Check if any inputs are empty
     *
     * @return true if any inputs are empty, false if all inputs are not empty
     */
    private boolean inputsEmpty() {
        return (usernameInput.getText().trim().isEmpty() ||
                emailInput.getText().trim().isEmpty() ||
                passwordInput.getText().trim().isEmpty() ||
                passwordRepeatInput.getText().trim().isEmpty());
    }

    /**
     * Check a input for requirements (not empty) and warnings (specified in parameters),
     * and change styling accordingly.
     *
     * @param input A Textfield that will be checked
     * @param msg A Text that will have styling applied
     * @param msgWarning A warning msg if warning is true
     * @param warning If true show warning msg
     */
    private void checkField(TextField input, Text msg, String msgWarning, boolean warning) {
        // Required styling
        if (input.getText().trim().isEmpty()) {
            setBorderStyle(input, COLOR_REQUIRED);
            msg.setVisible(true);
            msg.setText(MSG_REQUIRED);
            setTextColor(msg, COLOR_REQUIRED);
        }
        // Warning styling
        else if (warning) {
            setBorderStyle(input, COLOR_WARNING);
            msg.setVisible(true);
            msg.setText(msgWarning);
            setTextColor(msg, COLOR_WARNING);
        }
        // Normal styling
        else {
            setBorderStyle(input, COLOR_NORMAL);
            msg.setVisible(false);
        }
    }

    /**
     * This method will check if a username is already taken,
     * with a call to the database.
     *
     * @return username taken or not
     */
    private boolean usernameTaken() {
        // Database request here
        return usernameInput.getText().equals("Mikael"); // Testing purposes
    }

    /**
     * This method will check if an email is already registered,
     * with a call to the database.
     *
     * @return email already registered or not
     */
    private boolean emailTaken() {
        // Database request here
        return emailInput.getText().equals("mikaelk@live.no"); // Testing purposes
    }

    /**
     * Check if password meets all requirements required.
     * <br/><br/>
     *      <b>Password requirements:</b>
     *      <ul>
     *          <li>1. Has minimum 6 characters</li>
     *      </ul>
     *
     * @return if all requirements are met
     */
    private boolean checkPasswordRequirements() {
        return passwordInput.getText().length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Check if passwords are <b>exactly</b> the same'
     *
     * @return passwords match or not
     */
    private boolean passwordMatch() {
        return passwordInput.getText().equals(passwordRepeatInput.getText());
    }


    /**
     * Method that will be called when the register button in the register view is clicked.
     * It will performs checks to make sure all requirements are met before registering user.
     * <br/><br/>
     * <b>Requirements for a registration to succeed:</b>
     * <ul>
     *     <li>1. All inputs are not empty</li>
     *     <li>2. Username and email is not already taken (db check)</li>
     *     <li>3. Passwords match</li>
     * </ul>
     *
     * <br/>
     *
     * Also make sure the database registration is successful before switching to dashboard view
     */
    public void register() {
        boolean usernameTaken = usernameTaken();
        boolean emailTaken = emailTaken();
        boolean passwordRequirements = checkPasswordRequirements();
        boolean passwordMatch = passwordMatch();

        // Username check
        checkField(usernameInput, usernameMsg, MSG_USERNAME_WARNING, usernameTaken);

        // Email check
        checkField(emailInput, emailMsg, MSG_EMAIL_WARNING, emailTaken);

        // Password check
        checkField(passwordInput, passwordMsg, MSG_PASSWORD_WARNING, !passwordRequirements);
        checkField(passwordRepeatInput, passwordRepeatMsg, MSG_PASSWORDREPEAT_WARNING, !passwordMatch);

        // Check requirements (details in javadoc above method)
        if (!inputsEmpty() && !usernameTaken && !emailTaken && passwordMatch) {
            boolean res = true; // Some database call
            if (res) {
                Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
            }
            // Show some error msg...
        }
    }
}
