package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.entities.player.*;
import com.teamfour.monopolish.game.propertylogic.Property;
import com.teamfour.monopolish.gui.controllers.Handler;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class manages all Entities in the game session. Entities are classified by players and the bank object.
 * This class uses its arraylist of players and the bank object to handle the communication between this objects,
 * as well as acting as an abstraction layer between all the objects and the rest of the application
 *
 * @author      eirikhem
 * @version     1.3
 */

public class EntityManager {
    // Attributes
    int gameId;                                         // ID for the game that contains this manager
    ArrayList<Player> players = new ArrayList<>();      // All players in the game
    PlayerDAO playerDAO = new PlayerDAO();              // Player database connection
    Bank bank;                                          // Bank object for handling available money and properties

    /**
     * Constructor
     * @param gameId
     */
    public EntityManager(int gameId) {
        this.gameId = gameId;
        this.bank = new Bank(gameId);
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
        // TODO: Fix this shit
        while (players.size() <= 0) {
            //players = playerDAO.getPlayersInGame(gameId);
        }
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

    /**
     * Returns the current player
     * @return You object
     */
    public Player getYou() {

        for(Player p : players) {
            if (p.getUsername().equals(Handler.getAccount().getUsername())) {
                return p;
            }
        }

        return null;
    }

    /**
     * Cycles through all players and updates their bankruptcy according to their value
     */
    public void updateBankruptcy() {
        for (Player p : players) {
            if (p.getMoney() <= 0) {
                p.setBankrupt(true);
            }
        }
    }

    /**
     * Check if there's a winner
     * @return The winner username
     */
    public String findWinner() {
        String result = null;
        // Count the number of not bankrupt players
        int notBankrupt = 0;
        for (Player p : players) {
            if (!p.isBankrupt()) {
                notBankrupt++;
                // If only one player is not bankrupt, this will be the winner
                result = p.getUsername();
            }
        }

        // If there are more or less than one not bankrupt, no winner was found
        if (notBankrupt == 1) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Generates a string array with the turn order of the players, based on their Player ID's
     * @return String array with the play order
     */
    public String[] generateTurnOrder() {
        String[] turns = new String[players.size()];
        for (int i = 0; i < turns.length; i++) {
            turns[i] = players.get(i).getUsername();
        }

        return turns;
    }

    /**
     * Returns a string presentation of all the entities
     * @return
     */
    public String toString() {
        String result = "Entities: \n";
        for (Player p : players) {
            result += "Name: " + p.getUsername() + "; " + p.toString();
        }

        result += bank.toString();

        return result;
    }

    // GETTERS & SETTERS

    public ArrayList<Player> getPlayers() {
        return (players);
    }
}