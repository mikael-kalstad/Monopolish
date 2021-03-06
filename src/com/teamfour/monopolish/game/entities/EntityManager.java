package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.game.GameConstants;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.gui.Handler;

import java.util.ArrayList;

/**
 * Manages all Entities in the game session. Entities are classified by players and the bank object.
 * This class uses its arraylist of players and the bank object to handle the communication between these objects,
 * as well as acting as an abstraction layer between all the objects and the rest of the application
 *
 * @author      eirikhem
 * @version     1.4
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

    public EntityManager() {
        this.bank = new Bank();
    }

    /**
     * Gets a player object based on their username
     * @param username The player's username
     * @return Player object
     */
    public Player getPlayer(String username) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUsername().equals(username)) {
                return (players.get(i));
            }
        }
        return (null);
    }

    /**
     * Removes a player from this entitymanager
     * @param username
     */
    public void removePlayer(String username){
        playerDAO.removePlayer(gameId, username);
        Player temp = getPlayer(username);
        players.remove(temp);
    }

    /**
     * Returns a property object placed on the specified position
     * @param position Position
     * @return Property object
     */
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

    /**
     * Transfers money from the bank to a specified player
     * @param username Username of player to receive money
     * @param amount Amount to be transferred. Set to negative to transfer other way
     */
    public void transferMoneyFromBank(String username, int amount) {
        Player player = getPlayer(username);
        if (amount > 0 && bank.getMoney() < amount)
            amount = bank.getMoney();
        else if (amount < 0 && player.getMoney() < Math.abs(amount))
            amount = -player.getMoney();

        bank.transferMoney(player, amount);
    }

    /**
     * Distributes money from the bank at the start of the game
     * @param amount Amount each player should get
     */
    public void distributeStartMoney(int amount) {
        for (Player p : players) {
            p.setMoney(0);
            bank.transferMoney(p, amount);
        }
    }

    /**
     * Transfers money from one player to another
     * @param from Username of player to transfer from
     * @param to Username of player to transfer to
     * @param amount Amount to be transferred
     * @return True if successful
     */
    public boolean transferMoneyFromTo(String from, String to, int amount) {
        Player fromPlayer = getPlayer(from);
        Player toPlayer = getPlayer(to);

        if (fromPlayer == null || toPlayer == null)
            return false;

        if (fromPlayer.getMoney() < amount) {
            amount = fromPlayer.getMoney();
        }

        return fromPlayer.transferMoney(toPlayer, amount);
    }

    /**
     * Let's a player purchase a specified property
     * @param receiver Entity to receive object
     * @param property Property to be purchased
     * @return True if receiver has enough money
     */
    public boolean purchaseProperty(Entity receiver, Property property) {
        // If not enough money, return false
        if (receiver.getMoney() < property.getPrice()) {
            return false;
        }

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

        return true;
    }

    /**
     * Retrieves all player data from the database, to update the current game
     * after an opponent's round
     */
    public void updateFromDatabase() {
        int moneyInGame = 0;
        players.clear();
        players = playerDAO.getPlayersInGame(gameId);
        for (Player p : players) {
            p.updatePropertiesFromDatabase(gameId);
            moneyInGame += p.getMoney();
        }

        // Update bank
        bank.updatePropertiesFromDatabase(gameId);
        bank.setMoney(GameConstants.MAX_GAME_MONEY - moneyInGame);
    }

    /**
     * Writes all player updates to the database
     */
    public void updateToDatabase() {
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
    public String[] getUsernames() {
        String[] turns = new String[players.size()];
        for (int i = 0; i < turns.length; i++) {
            turns[i] = players.get(i).getUsername();
        }

        return turns;
    }

    /**
     * Returns a string presentation of all the entities
     * @return String
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

    public int[] getPlayerPositions() {
        updateFromDatabase();
        int[] positions = new int[players.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = players.get(i).getPosition();

        }

        return positions;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) { this.bank = bank; }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}