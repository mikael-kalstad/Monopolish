package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller class for registration view
 *
 * @author Mikael Kalstad
 * @version 1.6
 */

public class RegisterController {
    @FXML private TextField usernameInput;
    @FXML private Pane usernameRequirement;
    @FXML private TextField emailInput;
    @FXML private TextField passwordInput;
    @FXML private TextField passwordRepeatInput;
    @FXML private Text usernameMsg;
    @FXML private Text emailMsg;
    @FXML private Text passwordMsg;
    @FXML private Text passwordRepeatMsg;
    @FXML private Pane passwordRequirement;

    // Color constants
    private final String COLOR_NORMAL = "white";
    private final String COLOR_REQUIRED = "orange";
    private final String COLOR_WARNING = "red";

    // Msg constants
    private final String MSG_REQUIRED = "*Field is required";
    private final String MSG_PASSWORD_WARNING = "*Check password requirements";
    private final String MSG_PASSWORDREPEAT_WARNING = "*Passwords do not match";
    private final String MSG_USERNAME_WARNING = "*Username is taken, choose a different one";
    private final String MSG_USERNAME_REQUIREMENTS = "*Check username requirements";
    private final String MSG_EMAIL_WARNING = "*Email is already registered";

    //  Input requirements constants
    private final int MAX_USERNAME_LENGTH = 30;
    private final int MIN_PASSWORD_LENGTH = 6;


    @FXML public void initialize()  {
        // Add focus listener and show password requirement,
        // if password length is lower than required
        passwordInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && passwordInput.getText().length() < MIN_PASSWORD_LENGTH) {
                passwordRequirement.setVisible(true);
            } else {
                passwordRequirement.setVisible(false);
            }
        });

        // Check if password length meets the requirement,
        // and show or hide the requirement accordingly.
        passwordInput.setOnKeyTyped((observable) -> {
            if (passwordInput.getText().length() >= MIN_PASSWORD_LENGTH) {
                passwordRequirement.setVisible(false);
            } else {
                passwordRequirement.setVisible(true);
            }
        });

        usernameInput.setOnKeyTyped((observable) -> {
            if (usernameInput.getText().length() >= MAX_USERNAME_LENGTH) {
                usernameRequirement.setVisible(true);

            } else {
                usernameRequirement.setVisible(false);
            }
        });
    }

    private void setBorderStyle(Node element, String color) {
        element.setStyle("-fx-border-color: " + color);
    }

    private void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
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
     *     <li>2. Username and email is not already taken</li>
     *     <li>3. Passwords match</li>
     * </ul>
     *
     * <br/>
     * <b>Response from database trying to register user</b>
     * <ul>
     *      <li>0 = user registered</li>
     *      <li>1= username taken</li>
     *      <li>2= email taken</li>
     *      <li>3 = username and email taken</li>
     * </ul>
     *
     * If the registration is successful, it will switch to the dashboard view
     */
    public void register() {
        boolean usernameTaken = false;
        boolean usernameRequirement = usernameInput.getText().length() >= MAX_USERNAME_LENGTH;
        boolean emailTaken = false;
        boolean passwordRequirements = checkPasswordRequirements();
        boolean passwordMatch = passwordMatch();

        // Check requirements (details in javadoc above method)
        if (!inputsEmpty() && passwordMatch && !usernameRequirement) {
            int res;
            Account user = new Account(usernameInput.getText().trim(), emailInput.getText().trim(), LocalDate.now(), 0);

            try {
                ConnectionPool.create();
                res = Handler.getAccountDAO().insertAccount(user, passwordInput.getText().trim());
                if (res == 3) usernameTaken = emailTaken = true;
                else if (res == 1) usernameTaken = true;
                else if (res == 2) emailTaken = true;
            }
            catch (SQLException e) {
                // TODO: Display database error
                return;
            }

            if (res == 0) {
                Handler.getSceneManager().setScene(ViewConstants.DASHBOARD.getValue());
            }
        }

        // Username check
        checkField(usernameInput, usernameMsg, MSG_USERNAME_WARNING, usernameTaken);
        checkField(usernameInput, usernameMsg, MSG_USERNAME_REQUIREMENTS, usernameRequirement);

        // Email check
        checkField(emailInput, emailMsg, MSG_EMAIL_WARNING, emailTaken);

        // Password check
        checkField(passwordInput, passwordMsg, MSG_PASSWORD_WARNING, !passwordRequirements);
        checkField(passwordRepeatInput, passwordRepeatMsg, MSG_PASSWORDREPEAT_WARNING, !passwordMatch);
    }

    /**
     * Method that will be called when the login button in the register view is clicked.
     * Will go back to the login view
     */
    public void login() {
        com.teamfour.monopolish.gui.controllers.Handler.getSceneManager().setScene(ViewConstants.LOGIN.getValue());
    }
}
