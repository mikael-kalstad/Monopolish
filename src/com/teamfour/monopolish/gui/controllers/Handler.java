package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.game.GameDAO;
import com.teamfour.monopolish.game.entities.player.PlayerDAO;
import com.teamfour.monopolish.gui.views.ViewConstants;
import com.teamfour.monopolish.lobby.LobbyDAO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Handler extends Application {
    private static SceneManager sceneManager;
    private static Account account;
    private static AccountDAO accountDAO = new AccountDAO();
    private static PlayerDAO playerDAO = new PlayerDAO();
    private static LobbyDAO lobbyDAO = new LobbyDAO();
    private static GameDAO gameDAO = new GameDAO();

    private final String APPLICATION_TITLE = "Monopoly";
    private final double ASPECT_RATIO = 16.0/9.0;
    private final String INITIAL_VIEW = ViewConstants.LOGIN.getValue();

    private boolean hasWord(String str, String matchingWord) {
        String arr[] = str.split(" ");
        for (String word : arr) {
            if (word.equals(matchingWord)) return true;
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(APPLICATION_TITLE);

        // Setting full screen size to stage
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screen.getWidth());
        primaryStage.setHeight(screen.getHeight());
        //primaryStage.setMaximized(true);

        // Setting initial view (login)
        sceneManager = new SceneManager(primaryStage, INITIAL_VIEW);
        primaryStage.show();


        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            // Fixing scene scaling issues
            sceneManager.setSceneScale(primaryStage);

            // Setting height
            primaryStage.setHeight(primaryStage.getWidth() / (ASPECT_RATIO));
        };

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);

        // Center window
        //primaryStage.setX((primaryStage.getWidth() - screen.getWidth()) / 2);
        //primaryStage.setX((primaryStage.getHeight() - screen.getHeight()) / 2);

        System.out.println("visualbounds width: " + screen.getWidth()); // screens usable width (no task bars etc.)
        System.out.println("visualbounds height: " + screen.getHeight()); // screens usable height
        System.out.println("Actual res width: " + primaryStage.getWidth());
        System.out.println("Actual res height: " + primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Get and set methods
    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account newAccount) {
        account = newAccount;
    }

    public static AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public static PlayerDAO getPlayerDAO() { return playerDAO; }
    public static LobbyDAO getLobbyDAO() { return lobbyDAO; }

    public static GameDAO getGameDAO() { return gameDAO; }
}
