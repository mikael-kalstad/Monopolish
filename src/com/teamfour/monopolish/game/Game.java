package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.gui.controllers.Handler;

/**
 * This class represents the current game session instance.
 *
 * @author      eirikhem
 * @version     1.0
 */

public class Game {
    // Attributes
    private int gameId;
    private Board board;
    private EntityManager entities;
    private String[] players;
    private int currentTurn = 0;
    private int roundNumber = 0;
    private Dice dice;
    private int throwCounter = 0;

    /**
     * Initializes the game instance and sets this to the current game
     * @param gameId
     */
    public Game(int gameId) {
        this.gameId = gameId;
        dice = new Dice(2, 6);
        Handler.setCurrentGame(this);
    }

    // SETTERS & GETTERS

    public int getGameId() {
        return gameId;
    }

    public Dice getDice() {
        return dice;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public EntityManager getEntities() {
        return entities;
    }

    public void setEntities(EntityManager entities) {
        this.entities = entities;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public int getThrowCounter() {
        return throwCounter;
    }

    public void setThrowCounter(int throwCounter) {
        this.throwCounter = throwCounter;
    }

    public void addThrowCounter() {
        this.throwCounter++;
    }

    public int getCurrentTurn() { return currentTurn; }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void incrementTurn() {
        if (currentTurn < (players.length - 1)) {
            currentTurn++;
        } else {
            currentTurn = 0;
            roundNumber++;
        }
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void incrementRound() {
        roundNumber++;
    }
}
