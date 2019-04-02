package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.game.GameDAO;
import com.teamfour.monopolish.game.GameLogic;
import com.teamfour.monopolish.game.entities.player.PlayerDAO;
import com.teamfour.monopolish.gui.views.SceneManager;
import com.teamfour.monopolish.gui.views.ViewConstants;
import com.teamfour.monopolish.lobby.LobbyDAO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * This class will hold an instances of objects that will be used throughout the application. <br/>
 * By using this Handler, all classes can refer to the same object more easily.
 */
public class Handler {
    private static SceneManager sceneManager;
    private static Account account;
    private static GameLogic gameLogic;

    // DAO Objects
    private static AccountDAO accountDAO = new AccountDAO();
    private static PlayerDAO playerDAO = new PlayerDAO();
    private static LobbyDAO lobbyDAO = new LobbyDAO();
    private static GameDAO gameDAO = new GameDAO();

    // Variables used in game
    private static ArrayList<String[]> colorList = new ArrayList<>();
    private static String tradeUsername;
    private static @FXML Pane tradeContainer;
    private static @FXML Pane forfeitContainer;
    private static int currentGameId;

    // Setter and getters
    public static SceneManager getSceneManager() {
        return sceneManager;
    }
    public static void setSceneManager(SceneManager sceneManager) { Handler.sceneManager = sceneManager; }

    public static Account getAccount() {
        return account;
    }
    public static void setAccount(Account newAccount) {
        account = newAccount;
    }
    public static void resetAccount() { account = null; }

    public static GameLogic getGameLogic() { return gameLogic; }
    public static void setGameLogic(GameLogic gameLogic) { Handler.gameLogic = gameLogic; }

    /**
     * Set the arrayList containing color info for each player
     * @param arr
     */
    public static void setColorList(ArrayList<String[]> arr) {
        // Deep copy of the array
        for (String[] player : arr) {
            colorList.add(player.clone());
        }
    }
    public static ArrayList<String[]> getColorList() { return colorList; }

    // Getter and setter for DAO objects
    public static AccountDAO getAccountDAO() { return accountDAO; }
    public static PlayerDAO getPlayerDAO() { return playerDAO; }
    public static LobbyDAO getLobbyDAO() { return lobbyDAO; }
    public static GameDAO getGameDAO() { return gameDAO; }

    // Setter and getter for Game variables
    public static String getTradeUsername() { return tradeUsername; }
    public static void setTradeUsername(String tradeUsername) { Handler.tradeUsername = tradeUsername; }
    public static Pane getTradeContainer() { return tradeContainer; }
    public static void setTradeContainer(Pane tradeContainer) { Handler.tradeContainer = tradeContainer; }
    public static Pane getForfeitContainer() { return forfeitContainer; }
    public static void setForfeitContainer(Pane forfeitContainer) { Handler.forfeitContainer = forfeitContainer; }
    public static int getCurrentGameId() { return currentGameId; }
    public static void setCurrentGameId(int currentGameId) { Handler.currentGameId = currentGameId; }
}
