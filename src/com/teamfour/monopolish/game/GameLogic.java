package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.entities.player.Player;

import java.util.ArrayList;

public class GameLogic {
    private GameDAO gameDAO;
    private int gameId;
    private Dice dice = new Dice(2,6);
    private int turn;
    private int roundNumber;
    private boolean finished;
    private boolean yourTurn;
    private Board board;

    public GameLogic(int gameId, ArrayList<Player> players) {


    }

    public void run() {
        // Load board, graphics, etc.

        // Initiate variables, set defaults, get data from database
        // Write to database??

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
