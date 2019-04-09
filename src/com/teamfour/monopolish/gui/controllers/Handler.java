package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.game.Game;
import com.teamfour.monopolish.game.GameDAO;
import com.teamfour.monopolish.game.entities.player.PlayerDAO;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.PropertyDAO;
import com.teamfour.monopolish.gui.views.SceneManager;
import com.teamfour.monopolish.lobby.LobbyDAO;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

/**
 * This class will hold an instances of objects that will be used throughout the application. <br/>
 * By using this Handler, all classes can refer to the same object more easily.
 */
public class Handler {
    private static SceneManager sceneManager;
    private static Account account;
    private static Game currentGame;

    // DAO Objects
    private static AccountDAO accountDAO = new AccountDAO();
    private static PlayerDAO playerDAO = new PlayerDAO();
    private static LobbyDAO lobbyDAO = new LobbyDAO();
    private static GameDAO gameDAO = new GameDAO();
    private static PropertyDAO propertyDAO = new PropertyDAO();

    // Variables used in game
    private static ArrayList<String[]> colorList = new ArrayList<>();
    private static String tradeUsername;
    private static Property buyHouseProperty;
    private static @FXML Pane tradeContainer;
    private static @FXML Pane buyHouseContainer;
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

    public static Game getCurrentGame() { return currentGame; }
    public static void setCurrentGame(Game game) { currentGame = game; }

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

    /**
     * Go through a color list and find the color associated with a player.
     *
     * @param username Target user
     * @return Color associated with user
     */
    public static String getPlayerColor(String username) {
        // Go through the arraylist located in Handler
        for (String[] player : Handler.getColorList()) {

            // Check if username is target username and return color associated with it if it is an match
            if (player[0].equals(username)) {
                return player[1];
            }
        }
        return null;
    }

    /**
     * Play a sound in the application
     * @param soundFile Path to the sound file
     */
    public static void playSound(String soundFile) {
        Media sound = new Media(new File(soundFile).toURI().toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();
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
    public static Property getBuyHouseProperty() { return buyHouseProperty; }
    public static void setBuyHouseProperty(Property buyHouseProperty) { Handler.buyHouseProperty = buyHouseProperty; }
    public static Pane getTradeContainer() { return tradeContainer; }
    public static void setTradeContainer(Pane tradeContainer) { Handler.tradeContainer = tradeContainer; }
    public static Pane getBuyHouseContainer() { return buyHouseContainer; }
    public static void setBuyHouseContainer(Pane buyHouseContainer) { Handler.buyHouseContainer = buyHouseContainer; }
    public static Pane getForfeitContainer() { return forfeitContainer; }
    public static void setForfeitContainer(Pane forfeitContainer) { Handler.forfeitContainer = forfeitContainer; }
    public static int getCurrentGameId() { return currentGameId; }
    public static void setCurrentGameId(int currentGameId) { Handler.currentGameId = currentGameId; }

    public static PropertyDAO getPropertyDAO() {
        return propertyDAO;
    }
}
