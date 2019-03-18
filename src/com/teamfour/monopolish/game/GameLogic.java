package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.gui.controllers.Handler;

import java.sql.SQLException;

public class GameLogic {
    // Attributes
    private final int START_MONEY = 2000;
    private GameDAO gameDAO;                // Database connection
    private int gameId;                     // The current's session global game id
    private Dice dice = new Dice(2,6); // Dice object
    private String[] turns;
    private int turnNumber = 0;             // Which turn it is
    private int roundNumber = 0;
    private String currentPlayer;
    private boolean finished = false;       // Is the game finished?
    private Board board;                    // Bård object
    private EntityManager entityManager;

    /**
     * Constructor
     * @param gameId
     */
    public GameLogic(int gameId) {
        gameDAO = new GameDAO();
        this.gameId = gameId;
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

        // 3. Transfer money from bank to players
        System.out.println("Distributing money...");
        entityManager.distributeMoneyFromBank(START_MONEY);
        // 4. Generate random turn order
        System.out.println("Randomizing turn order...");
        turns = entityManager.generateTurnOrder();
        gameDAO.setCurrentPlayer(gameId, turns[0]);

        for (int i = 0; i < turns.length;) {
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
            // Check to see if currentPlayer has changed
            if (currentPlayer == gameDAO.getCurrentPlayer(gameId)) {
                // If not, wait a second before checking again
                Thread.sleep(1000);
                continue;
            }

            // If changed, increment turn
            newTurn();
            // Update data from database
            currentPlayer = gameDAO.getCurrentPlayer(gameId);
            entityManager.updateFromDatabase();
            System.out.println("It is " + currentPlayer + "'s turn.");
            // TODO: Update view

            // Check to see if it's your turn
            if (turns[turnNumber] != Handler.getAccount().getUsername()) {
                Thread.sleep(1000);
                continue;
            }

            // Your turn!!

            System.out.println("It is your turn!");

            int placesToMove = 0;

            // If you are in jail, try to escape from jail through a bail
            if (!entityManager.getYou().isInJail()) {
                // If not in jail, throw the dice!
                int throwCounter = 0;
                // jailDice indicates that if you get this result, you
                // have to throw again
                int[] throwResult = dice.throwDice();
                while (throwResult[0] == throwResult[1]) {
                    // If you get jailDice three times in a row, move to jail!
                    if (throwCounter < 2) {
                        throwResult = dice.throwDice();

                        // Sum up the dices to placesToMove
                        placesToMove = throwResult[0] + throwResult[1];

                        // Move your player by placesToMove
                        entityManager.getYou().move(placesToMove);

                        // Time to check where you landed!
                        int yourPosition = entityManager.getYou().getPosition();
                        if (board.getTileType(yourPosition) == Board.GO_TO_JAIL) {
                            // TODO: Gotojail
                            entityManager.getYou().setInJail(true);
                            entityManager.getYou().moveTo(board.getJailPosition());
                        } else if (board.getTileType(yourPosition) == Board.START) {
                            // If landing at start, get money from bank
                            entityManager.transferMoneyFromBank(Handler.getAccount().getUsername(), START_MONEY * 2);
                        } else if (board.getTileType(yourPosition) == Board.PROPERTY) {
                            // TODO: Property handling NOTE: Kill me
                        }
                    } else {
                        // TODO: Go to jail
                        entityManager.getYou().setInJail(true);
                        entityManager.getYou().moveTo(board.getJailPosition());
                    }
                }
            } else {
                // TODO: tryBail method
                // If trybail fails:
            }

            // TURN FINISHED!!

            // Check if anyone is bankrupt and update accordingly
            entityManager.updateBankruptcy();

            // TODO: Update you to the database
            entityManager.updateToDatabase();

            // TODO: Check if anyone has won
            if (entityManager.findWinner() != null) {
                finished = true;
            } else {
                newTurn();
            }
        }
    }

    /**
     * Increments the turn number
     */
    public void newTurn() throws SQLException {
        if (turnNumber == entityManager.getPlayers().size()) {
            roundNumber++;
            turnNumber = 0;
        } else {
            turnNumber++;
        }

        // Update current player
        gameDAO.setCurrentPlayer(gameId, turns[turnNumber]);
    }

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
}
