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
//        primaryStage.setWidth(1920);
//        primaryStage.setHeight(1080);
       primaryStage.setMaximized(true);
//        primaryStage.setMinWidth(1920);
//        primaryStage.setMinHeight(1080);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        // full bounds
        javafx.geometry.Rectangle2D bounds2 = Screen.getPrimary().getBounds();

        System.out.println("bounds width: " +bounds2.getWidth()); // screens width
        System.out.println("bound height: " +bounds2.getHeight()); // screens height

        // visual bounds
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        System.out.println("visualbounds width: " +visualBounds.getWidth()); // screens usable width (no task bars etc.)
        System.out.println("visualbounds height: " +visualBounds.getHeight()); // screens usable height

        // Initial view is login
        sceneManager = new SceneManager(primaryStage, ViewConstants.LOGIN.getValue());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
