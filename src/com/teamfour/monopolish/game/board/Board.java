package com.teamfour.monopolish.game.board;

import com.teamfour.monopolish.game.playerlogic.Player;
import com.teamfour.monopolish.game.playerlogic.PlayerManager;
import com.teamfour.monopolish.game.propertylogic.PropertyManager;

import java.util.ArrayList;

public class Board {
    // Attributes
    private int gameId;
    private PlayerManager playerManager;
    private PropertyManager propertyManager;
    private Layout layout;

    /**
     * Constructor
     * @param gameId Game Id
     * @param players Players in this game
     */
    public Board(int gameId, ArrayList<Player> players) {
        this.gameId = gameId;
        playerManager = new PlayerManager(players);
    }
}
