package com.teamfour.monopolish.game.board;

import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.entities.player.PlayerManager;

import java.util.ArrayList;

/**
 * Represents the board object in the game. Manages things like players and properties,
 * as well as the layout of the tiles
 *
 * @author      eirikhem
 * @version     1.0
 */

public class Board {
    // Attributes
    private int gameId;
    private PlayerManager playerManager;
    private Layout layout;

    /**
     * Constructor
     * @param gameId Game Id
     * @param players Players in this game
     */
    public Board(int gameId, ArrayList<Player> players) {
        this.gameId = gameId;
        //playerManager = new PlayerManager(players);
        layout = new Layout();
    }

    /**
     * Called at the start of the game
     */
    public void init() {}

    /**
     * Called each turn of the game
     */
    public void update() {}
}