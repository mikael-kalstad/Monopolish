package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.board.Board;
import com.teamfour.monopolish.game.playerlogic.Player;

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

    public void startGame() {

    }
    public void waitForTurn() {

    }
}
