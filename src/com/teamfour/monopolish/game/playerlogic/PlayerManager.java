package com.teamfour.monopolish.game.playerlogic;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerManager {
    ArrayList<Player> players;
    PlayerDAO playerDAO = new PlayerDAO();

    /**
     * Constructor
  //   * @param players
     */
    public PlayerManager(int game_id, String[] usernames) {
        this.players = playerDAO.createPlayers(game_id, usernames);

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

    /**
     * Retrieves all player data from the database, to update the current game
     * after an opponent's round
     */
    public void update() {
        // TODO: Call DAO
    }
}