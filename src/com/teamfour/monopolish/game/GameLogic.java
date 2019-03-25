package com.teamfour.monopolish.game;

import com.teamfour.monopolish.account.Account;
import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.propertylogic.Property;
import com.teamfour.monopolish.gui.controllers.Handler;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Contains all the gameplay logic for the game
 *
 * @author      eirikhem
 * @version     1.1
 */

public class GameLogic {
    // Read-only variables
    private final int START_MONEY = 5000;
    private final int BAIL_MONEY = 500;

    // Database and entities
    private GameDAO gameDAO;                // Database connection
    private EntityManager entityManager;

    // Game logic attributes
    private int gameId;                     // The current's session global game id
    private Board board;                    // Bård object
    private Dice dice = new Dice(2,6); // Dice object
    private String[] turns;                 // Order of player actions
    private int turnNumber = 0;             // Which turn it is
    private int roundNumber = 0;
    private String currentPlayer;
    private boolean finished = false;       // Is the game finished?

    /**
     * Constructor
     * @param gameId
     */
    public GameLogic(int gameId) {
        gameDAO = new GameDAO();
        this.gameId = gameId;
    }

    /**
     * Set up the game session
     * @throws SQLException
     */
    public void setupGame() throws SQLException {

        //Handler.setAccount(new Account("helgeingstad", "giske@damer.no", LocalDate.now(), 0));

        // Load board, graphics, etc.
        ConnectionPool.create();

        // 1. Load players from database by gameid
        // 2. Load all properties from database into bank's properties
        // Initialize board and get players from database
        System.out.println("Loading board...");
        System.out.println("Gameid: " + gameId);
        board = new Board();
        System.out.println("Loading players...");
        entityManager = new EntityManager(gameId);
        entityManager.updateFromDatabase();

        // 3. Transfer money from bank to players
        System.out.println("Distributing money...");
        entityManager.distributeMoneyFromBank(START_MONEY);
        // 4. Generate random turn order
        System.out.println("Getting turn order...");
        turns = entityManager.generateTurnOrder();
        for (int i = 0; i < turns.length; i++) {
            System.out.println((i + 1) + ": " + turns[i]);
        }
        System.out.println("\n");

        // 5. Write current player and money amounts to database
        System.out.println("Writing back to database...");
        gameDAO.setCurrentPlayer(gameId, turns[0]);
        entityManager.updateToDatabase();
        // 6. Start!

        // Load yourPlayer

        currentPlayer = turns[0];
        // Main game loop
        System.out.println("Game is starting!");
    }

    public int[] throwDice() throws SQLException {
        int[] throwResult = dice.throwDice();
        int steps = throwResult[0] + throwResult[1];
        if (entityManager.getYou().isInJail() && throwResult[0] == throwResult[1]) {
            moveYourPlayer(steps);
        } else {
            moveYourPlayer(steps);
        }

        return throwResult;
    }

    public void moveYourPlayer(int steps) throws SQLException {
        entityManager.getYou().move(steps);
        finishYourTurn();
    }

    public Player getYourPlayer() {
        return entityManager.getYou();
    }

    // HELPER METHODS

    private void propertyTransaction() {
        Property property = entityManager.getPropertyAtPosition(entityManager.getYou().getPosition());
        if (property == null) {
            System.out.println("No property here, get lost, punk.");
            return;
        }

        if (property.getOwner().equals("")) {
            System.out.println("You have landed on " + property.getName() + ". You are buying it.");
            // TODO: Press button to buy property if you have enough money
            if (entityManager.getYou().getMoney() >= property.getPrice()) {
                entityManager.transactProperty(entityManager.getYou(), property);
                System.out.println("You now have " + entityManager.getYou().getMoney() + " caches");
                System.out.println(entityManager.getYou().toString());
            } else {
                System.out.println("You can't afford it, you doofus.");
            }
        } else {
            System.out.println("You don't own this property, prepare to get buried in debt.");
        }
    }

    public void startYourTurn() throws SQLException {
        currentPlayer = gameDAO.getCurrentPlayer(gameId);
        updateFromDatabase();
        for (int i = 0; i < turns.length; i++) {
            if (turns[i].equals(currentPlayer)) {
                turnNumber = i;
            }
        }
    }

    public void updateFromDatabase() throws SQLException {
        entityManager.updateFromDatabase();
    }

    public void finishYourTurn() throws SQLException {
        if (turnNumber >= entityManager.getPlayers().size() - 1) {
            roundNumber++;
            turnNumber = 0;
        } else {
            turnNumber++;
        }
        System.out.println(entityManager.getYou().getPosition());

        currentPlayer = turns[turnNumber];
        updateToDatabase();
    }

    public void updateToDatabase() throws SQLException {
        gameDAO.setCurrentPlayer(gameId, currentPlayer);
        entityManager.updateToDatabase();
    }

    public void newTurn(boolean yourTurn) throws SQLException {
        currentPlayer = gameDAO.getCurrentPlayer(gameId);
        updateFromDatabase();
        for (int i = 0; i < turns.length; i++) {
            if (turns[i].equals(currentPlayer)) {
                turnNumber = i;
            }
        }
    }

    // SETTERS & GETTERS

    public int getGameId() {
        return gameId;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isFinished() {
        return finished;
    }

    public String[] getTurns() { return turns; }

    public int isNewTurn() throws SQLException {
        String newCurrentUser = gameDAO.getCurrentPlayer(gameId);
        if (newCurrentUser.equals(Handler.getAccount().getUsername())) {
            currentPlayer = newCurrentUser;
            newTurn(true);
            return 1;
        } else if (!newCurrentUser.equals(currentPlayer)) {
            currentPlayer = newCurrentUser;
            newTurn(false);
            return 0;
        }

        return -1;
    }

    public int[] getPlayerPositions() throws SQLException {
        updateFromDatabase();
        ArrayList<Player> players = entityManager.getPlayers();
        int[] positions = new int[players.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = players.get(i).getPosition();
        }

        return positions;
    }
}