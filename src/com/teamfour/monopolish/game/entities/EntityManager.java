package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.*;

import java.util.ArrayList;

public class EntityManager {
    ArrayList<Player> players;
    PlayerDAO playerDAO;
    Bank bank;

    /**
     * Constructor
     * @param gameId
     */
    public EntityManager(int gameId) {
        //this.players = playerDAO.getPlayersInGame(gameId);
        this.playerDAO = new PlayerDAO();
        this.bank = new Bank();
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
        playerDAO.removePlayer(username);
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
    public void update() {
        // TODO: Call DAO
    }
}