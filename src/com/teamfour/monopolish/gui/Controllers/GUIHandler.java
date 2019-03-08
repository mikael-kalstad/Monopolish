package com.teamfour.monopolish.gui.Controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIHandler {
    private Stage window;
    private SceneSwitcher sceneSwitcher;
    private Scene login;
    private Scene register;
    private Scene dashboard;
    private Scene board;

    GUIHandler(Stage window, SceneSwitcher sceneSwitcher) {
       this.sceneSwitcher = sceneSwitcher;
       this.window = window;
    }

    GUIHandler() {
        try {
            login = new LoginController().getScene();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public Stage getWindow() { return window; }
    public SceneSwitcher getSceneSwitcher() { return sceneSwitcher; }

    public Scene getLogin() {
        return login;
    }

    public Scene getRegister() {
        return register;
    }

    public Scene getDashboard() {
        return dashboard;
    }

    public Scene getBoard() {
        return board;
    }
}
