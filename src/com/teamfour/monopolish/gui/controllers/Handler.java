package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.account.AccountDAO;
import com.teamfour.monopolish.game.GameDAO;
import com.teamfour.monopolish.game.entities.player.PlayerDAO;
import com.teamfour.monopolish.gui.views.SceneManager;
import com.teamfour.monopolish.gui.views.ViewConstants;
import com.teamfour.monopolish.lobby.LobbyDAO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * This class will hold an instances of objects that will be used throughout the application. <br/>
 * By using this Handler, all classes can refer to the same object more easily.
 */
public class Handler extends Application {
    // Instances
    private static SceneManager sceneManager;
    private static Account account;
    private static AccountDAO accountDAO = new AccountDAO();
    private static PlayerDAO playerDAO = new PlayerDAO();
    private static LobbyDAO lobbyDAO = new LobbyDAO();
    private static GameDAO gameDAO = new GameDAO();
    private static ArrayList<String[]> colorList = new ArrayList<>();
    private static int currentGameId;

    // Constants for GUI
    private final String APPLICATION_TITLE = "Monopoly";
    private final String APPLICATION_LOGO = "res/gui/Dices/dice4.png";
    private final double ASPECT_RATIO = 64.0/35.0;
    private final String INITIAL_VIEW = ViewConstants.LOGIN.getValue();

    /**
     * This method will start the GUI application
     *
     * @param primaryStage The target window
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.getIcons().add(new Image("file:" + APPLICATION_LOGO));

        // Setting full screen size to stage
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screen.getWidth());
        primaryStage.setHeight(screen.getHeight());
        primaryStage.setMaximized(true);

        // Setting initial view (login)
        sceneManager = new SceneManager(primaryStage, INITIAL_VIEW);
        primaryStage.show();

        // Event listener for resizing
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

//        System.out.println("visualbounds width: " + screen.getWidth()); // screens usable width (no task bars etc.)
//        System.out.println("visualbounds height: " + screen.getHeight()); // screens usable height
//        System.out.println("Actual res width: " + primaryStage.getWidth());
//        System.out.println("Actual res height: " + primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get scenemanager instance, takes care of all view switching
     * @return Instance of scenemanager
     */
    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Get account object
     * @return Account instance
     */
    public static Account getAccount() {
        return account;
    }

    /**
     * Set account instance
     * @param newAccount A new account instance with user info
     */
    public static void setAccount(Account newAccount) {
        account = newAccount;
    }

    /**
     * Reset account to remove data of user
     */
    public static void resetAccount() { account = null; }


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

    // Get DAO instances
    public static AccountDAO getAccountDAO() { return accountDAO; }
    public static PlayerDAO getPlayerDAO() { return playerDAO; }
    public static LobbyDAO getLobbyDAO() { return lobbyDAO; }
    public static GameDAO getGameDAO() { return gameDAO; }
    public static int getCurrentGameId() { return currentGameId; }
    public static void setCurrentGameId(int currentGameId) { Handler.currentGameId = currentGameId; }
}
