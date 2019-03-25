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

    // TODO: Remove throws exception
    public void run() throws Exception {
        // Load board, graphics, etc.

        // 1. Load players from database by gameid
        // 2. Load all properties from database into bank's properties
        // Initialize board and get players from database
        System.out.println("Loading board...");
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
        gameDAO.setCurrentPlayer(gameId, turns[0]);

        for (int i = 0; i < turns.length; i++) {
            System.out.println((i + 1) + ": " + turns[i]);
        }
        System.out.println("\n");

        // 5. Write current player and money amounts to database
        System.out.println("Writing back to database...");
        entityManager.updateToDatabase();
        // 6. Start!

        // Main game loop
        System.out.println("Game is starting!");
        while (!finished) {
            currentPlayer = "";
            // CHeck if the current player has changed
            if (currentPlayer.equals(gameDAO.getCurrentPlayer(gameId))) {
                // If not, wait a second before checking again
                Thread.sleep(1000);
                continue;
            }
            // Update data from database
            currentPlayer = gameDAO.getCurrentPlayer(gameId);
            entityManager.updateFromDatabase();

            System.out.println("It is " + currentPlayer + "'s turn.");

            Handler.setAccount(new Account("helgeingstad", "giske@damer.no", LocalDate.now(), 0));

            // Check to see if it's your turn
            if (!turns[turnNumber].equals(Handler.getAccount().getUsername())) {
                Thread.sleep(1000);
                continue;
            }

            int placesToMove = 0;

            // If you are in jail, try to escape from jail through a bail
            if (!entityManager.getYou().isInJail()) {
                System.out.println("You are not in jail.");
                // If not in jail, throw the dice!
                int throwCounter = 0;
                // jailDice indicates that if you get this result, you
                // have to throw again
                int[] throwResult = {1, 1};
                do {
                    // If you get jailDice three times in a row, move to jail!
                    if (throwCounter < 2) {
                        // TODO: Press button to throw dice
                        throwResult = dice.throwDice();
                        System.out.println("You got " + throwResult[0] + " and " + throwResult[1]);

                        // Sum up the dices to placesToMove
                        placesToMove = throwResult[0] + throwResult[1];

                        // Move your player by placesToMove
                        System.out.println("You're moving " + placesToMove + " places.");
                        if (placesToMove + entityManager.getYou().getPosition() >= Board.BOARD_LENGTH) {
                            System.out.println("You get money from bank");
                            entityManager.transferMoneyFromBank(entityManager.getYou().getUsername(), START_MONEY);
                        }
                        entityManager.getYou().move(placesToMove);
                        System.out.println("You are at " + entityManager.getYou().getPosition());

                        // Time to check where you landed!
                        int yourPosition = entityManager.getYou().getPosition();
                        if (board.getTileType(yourPosition) == Board.GO_TO_JAIL) {
                            // TODO: Gotojail
                            System.out.println("You are going to jail!");
                            entityManager.getYou().setInJail(true);
                            entityManager.getYou().moveTo(board.getJailPosition());
                            break;
                        } else if (board.getTileType(yourPosition) == Board.PROPERTY) {
                            // TODO: Property handling NOTE: Kill me
                            propertyTransaction();
                        }
                    } else {
                        // TODO: Go to jail
                        entityManager.getYou().setInJail(true);
                        entityManager.getYou().moveTo(board.getJailPosition());
                    }
                    // If dices are the same, continue
                } while (throwResult[0] == throwResult[1]);
            } else {
                System.out.println("You are in jail.");
                // CHOICE: Pay bail or roll dice to escape
            }

            // TURN FINISHED!!

            System.out.println("Hello, the turn has ended for you.");
            // Check if anyone is bankrupt and update accordingly
            entityManager.updateBankruptcy();

            // Write all changes to database
            entityManager.updateToDatabase();

            // TODO: Check if anyone has won
            if (entityManager.findWinner() != null) {
                System.out.println("How can the game be finished??");
                finished = true;
            } else {
                newTurn();
            }
        }
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

    /**
     * Increments the turn number
     */
    public void newTurn() throws SQLException {
        System.out.println("New turn!");
        // If this was not your turn, get the new current player and update database
        if (!currentPlayer.equals(entityManager.getYou().getUsername())) {
            String newCurrentPlayer = gameDAO.getCurrentPlayer(gameId);
            for (int i = 0; i < turns.length; i++) {
                if (turns[i].equals(newCurrentPlayer)) {
                    turnNumber = i;
                }
            }
            currentPlayer = turns[turnNumber];
            entityManager.updateFromDatabase();
        } else {
            if (turnNumber >= entityManager.getPlayers().size() - 1) {
                roundNumber++;
                turnNumber = 0;
            } else {
                turnNumber++;
            }

            // Update current player
            gameDAO.setCurrentPlayer(gameId, turns[turnNumber]);
            entityManager.updateToDatabase();
        }
        currentPlayer = turns[turnNumber];
        System.out.println("Turn " + turnNumber + ": " + currentPlayer);
    }

    public void startYourTurn() throws SQLException {
        currentPlayer = gameDAO.getCurrentPlayer(gameId);
        entityManager.updateFromDatabase();
        for (int i = 0; i < turns.length; i++) {
            if (turns[i].equals(currentPlayer)) {
                turnNumber = i;
            }
        }
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

    public boolean isYourTurn() throws SQLException {
        String newCurrentUser = gameDAO.getCurrentPlayer(gameId);
        return newCurrentUser.equals(Handler.getAccount().getUsername());
    }

    public int[] getPlayerPositions() {
        ArrayList<Player> players = entityManager.getPlayers();
        int[] positions = new int[players.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = players.get(i).getPosition();
        }

        return positions;
    }
}
