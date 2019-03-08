package com.teamfour.monopolish.gui.Controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Application {
    private SceneSwitcher sceneSwitcher;
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;

    Scene getScene() throws Exception {
        // Load fxml file to scene
        Parent login = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
        Scene loginScene = new Scene(login);
        loginScene.getStylesheets().add("login.css");

        return loginScene;
    }

    public void login() {
        // Database query for checking username/email and password combination
        Boolean res = false;
        if (usernameInput.getText().equals("Mikael") && passwordInput.getText().equals("1234")) {
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
            GUIHandler handler = new GUIHandler();
            SceneSwitcher switcher = handler.getSceneSwitcher();
            switcher.changeScene(handler.getDashboard());

            System.out.println(usernameInput + " you are logged in");
        } else {
            // Remove normal styling and add warning style
            usernameInput.getStyleClass().removeAll();
            usernameInput.getStyleClass().add("warning");

            System.out.println("Login failed, username/email or password is wrong");
        }
    }

    // Testing
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load fxml file to scene
        Parent login = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
        Scene loginScene = new Scene(login);
        loginScene.getStylesheets().add("login.css");

        // Add scene to stage
        primaryStage.setTitle("Monopoly");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}
