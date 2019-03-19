package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.*;
import com.teamfour.monopolish.game.propertylogic.Property;
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

    public Property getPropertyAtPosition(int position) {
        Property prop = null;
        for (Player p : players) {
            prop = p.getPropertyAtPosition(position);
            if (prop != null) {
                return prop;
            }
        }

        return bank.getPropertyAtPosition(position);
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

    public void transactProperty(Entity receiver, Property property) {
        Entity propertyOwner;
        String owner = property.getOwner();
        if (owner.equals("") || owner == null) {
            propertyOwner = bank;
        } else {
            propertyOwner = getPlayer(property.getOwner());
        }

        if (receiver instanceof Player)
            property.setOwner(((Player) receiver).getUsername());
        else
            property.setOwner("");

        // Transfer properties
        receiver.getProperties().add(property);
        propertyOwner.getProperties().remove(property);

        // Transfer money
        receiver.transferMoney(propertyOwner, property.getPrice());
    }

    /**
     * Retrieves all player data from the database, to update the current game
     * after an opponent's round
     */
    public void updateFromDatabase() throws SQLException {
        players.clear();
        players = playerDAO.getPlayersInGame(gameId);
        for (Player p : players) {
            p.updatePropertiesFromDatabase(gameId);
        }

        bank.updatePropertiesFromDatabase(gameId);
    }

    /**
     * Writes all player updates to the database
     */
    public void updateToDatabase() throws SQLException {
        for (Player p : players) {
            playerDAO.updatePlayer(p, gameId);
            p.updatePropertiesToDatabase(gameId);
        }
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

    public String toString() {
        String result = "Properties: \n";
        for (Property p : bank.getProperties()) {
            result += p.toString() + "\n";
        }

        return result;
    }
}