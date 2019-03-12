package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This class handles all database communication towards the 'Game' table in database
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

public class GameDAO extends DataAccessObject {
    /**
     * Generates a new game from the lobby Id
     * @param lobbyId Lobby Id to pick players from
     * @return The Id of the new game
     * @throws SQLException
     */
    public int generateGame(int lobbyId) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call generate_game(?, ?)}");

        cStmt.setInt(1, lobbyId);
        cStmt.registerOutParameter(2, Types.INTEGER);

        int gameId = -1;
        if (cStmt.execute())
            gameId = cStmt.getInt(2);

        return gameId;
    }
}
