package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.*;
import com.teamfour.monopolish.gui.controllers.Handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 *
 * @author      eirikhem
 * @version     1.0
 */

public class EntityManager {
    int gameId;
    ArrayList<Player> players = new ArrayList<>();
    PlayerDAO playerDAO = new PlayerDAO();
    Bank bank;

    /**
     * Constructor
     * @param gameId
     */
    public EntityManager(int gameId) {
        this.gameId = gameId;
        this.bank = new Bank(gameId);
    }

    public ArrayList<Player> getPlayers() {
        //kanskje like lurt?
        return (players);
    }

    public Player getPlayer(String username) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUsername().equals(username)) {
                return (players.get(i));
            }
        }
        return (null);
    }

    public Player getPlayerByPosition(int pos){
        for(int i = 0; i<players.size(); i++){
            if(players.get(i).getPosition() == pos){
                return(players.get(i));
            }
        }
        return(null);
    }

    public int getPlayerPosition(String username){
        Player temp = getPlayer(username);
        return(temp.getPosition());
    }

    public void removePlayer(String username){
        playerDAO.removePlayer(gameId, username);
        Player temp = getPlayer(username);
        players.remove(temp);
    }

    public boolean transferMoneyFromBank(String username, int amount) {
        Player player = getPlayer(username);
        if (player == null) {
            return false;
        }

        return bank.transferMoney(player, amount);
    }

    public boolean distributeMoneyFromBank(int amount) {
        for (Player p : players) {
            bank.transferMoney(p, amount);
        }

        return true;
    }

    public boolean transferMoneyFromTo(String from, String to, int amount) {
        Player fromPlayer = getPlayer(from);
        Player toPlayer = getPlayer(to);

        if (fromPlayer == null || toPlayer == null)
            return false;

        return fromPlayer.transferMoney(toPlayer, amount);

    }

    /**
     * Retrieves all player data from the database, to update the current game
     * after an opponent's round
     */
    public void updateFromDatabase() throws SQLException {
        players.clear();
        players = playerDAO.getPlayersInGame(gameId);
    }

    public Player getYou() {
        for(Player p : players) {
            if (p.getUsername().equals(Handler.getAccount().getUsername())) {
                return p;
            }
        }

        return null;
    }

    public void updateBankruptcy() {
        for (Player p : players) {
            if (p.getMoney() <= 0) {
                p.setBankrupt(true);
            }
        }
    }

    public String findWinner() {
        String result = null;
        int notBankrupt = 0;
        for (Player p : players) {
            if (!p.isBankrupt()) {
                notBankrupt++;
                result = p.getUsername();
            }
        }

        if (notBankrupt == 1) {
            return result;
        } else {
            return null;
        }
    }

    public String[] generateTurnOrder() {
        String[] turns = new String[players.size()];
        for (int i = 0; i < turns.length; i++) {
            turns[i] = players.get(i).getUsername();
        }

        return turns;
    }

    /**
     * Writes all player updates to the database
     */
    public void updateToDatabase() throws SQLException {
        for (Player p : players) {
            playerDAO.updatePlayer(p, gameId);
        }
    }
}