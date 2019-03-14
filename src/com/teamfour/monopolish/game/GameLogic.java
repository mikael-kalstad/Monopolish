package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.board.Board;

public class GameLogic {
    // Attributes
    private GameDAO gameDAO;        // Database connection
    private int gameId;             // The current's session global game id
    private Dice dice = new Dice(2,6); // Dice object
    private String[] turn;          // Turn order. Each row's int value represents the player username
    private int turnNumber;         // Which total turn it is
    private boolean finished = false;       // Is the game finished?
    private boolean yourTurn;       // Is it your turn?
    private Board board;            // Bård object

    /**
     * Constructor
     * @param gameId
     */
    public GameLogic(int gameId) {
        gameDAO = new GameDAO();
        this.gameId = gameId;
        turnNumber = 0;

    }

    public void run() {
        // Load board, graphics, etc.

        // 1. Load players from database by gameid
        // 2. Load all properties from database into bank's properties
        // Initialize board with players by gameId
        board = new Board();

        // 3. Transfer money from bank to players
        // 4. Generate random turn order
        // 5. Write current player and money amounts to database
        // 6. Start!

        /*
            while (!finished) {
                if (!turnChanged()) {
                    wait 1.0 seconds
                    continue;
                }

                update();
                if (!myTurn()) {
                    wait 1.0 seconds
                    continue;
                }

                if (inJail()) {
                    if (!tryBail()) {
                        continue;
                    }
                } else {
                    while (rollDice() == 6 + 6) {
                        if (counter == 3) {
                            goToJail();
                            continue;
                        }
                        counter++

                    }
                }

                move();

                if (tile == GOTOJAIL) {
                    goToJail();
                    continue;
                }

                if (tile == START) {
                    startEvent();
                    continue;
                }

                if (tile == PROPERTY) {
                    propertyEvent();
                    continue;
                }

                TURN FINISHED!

                updateDatabase();
                if (anyWinners()) {
                    finished = true;
                } else {
                    if (turnNo = playersAmt) {
                        turnNo++;
                    } else {
                        turnNo = 0;
                    }
                }
            }
         */

    }

    public void waitForTurn() {

    }
}
