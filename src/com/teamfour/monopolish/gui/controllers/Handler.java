package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Handler extends Application {
    private static SceneManager sceneManager;
    private static Account account;
    private static AccountDAO accountDAO = new AccountDAO();

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    // account
    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account newAccount) {
        account = newAccount;
    }

    // AccountDAO
    public static AccountDAO getAccountDAO() {
        return accountDAO;
    }

    // Testing purposes
    @Override
    public void start(Stage primaryStage) throws Exception {
        ConnectionPool.create();
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.sizeToScene();

        // Initial view is login
        sceneManager = new SceneManager(primaryStage, ViewConstants.LOGIN.getValue());

        primaryStage.show();

        // Center window
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primaryStage.getWidth() - visualBounds.getWidth()) / 2);
        primaryStage.setX((primaryStage.getHeight() - visualBounds.getHeight()) / 2);

        System.out.println("visualbounds width: " + visualBounds.getWidth()); // screens usable width (no task bars etc.)
        System.out.println("visualbounds height: " + visualBounds.getHeight()); // screens usable height
        System.out.println("Actual res width: " + primaryStage.getWidth());
        System.out.println("Actual res height: " + primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
