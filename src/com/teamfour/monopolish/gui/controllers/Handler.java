package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.gui.views.ViewConstants;
import javafx.application.Application;
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
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);

        // Initial view is login
        sceneManager = new SceneManager(primaryStage, ViewConstants.LOGIN.getValue());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
