package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

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

    /**
     * Gets the current player Id in the specified game session
     * @param gameId The session id
     * @return Id of the current player
     * @throws SQLException
     */
    public int getCurrentPlayer(int gameId) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call get_current_player(?, ?)}");

        cStmt.setInt(1, gameId);
        cStmt.registerOutParameter(2, Types.INTEGER);

        int currentPlayer = -1;
        if (cStmt.execute())
            currentPlayer = cStmt.getInt(2);

        return currentPlayer;
    }

    /**
     * Sets a new current player in the specified session
     * @param gameId Session id
     * @param currentPlayer Current player id
     * @return True if successful
     * @throws SQLException
     */
    public boolean setCurrentPlayer(int gameId, int currentPlayer) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call set_current_player(?, ?)}");

        cStmt.setInt(1, gameId);
        cStmt.setInt(2, currentPlayer);

        int count = cStmt.executeUpdate();

        return (count > 0);
    }

    /**
     * Finish the game and ties up loose ends in the table
     * @param gameId Session id
     * @param finishDate Date the game was finished
     * @param winnerId Userid of the winner. 0 if no winner
     * @return True if operation was successful
     * @throws SQLException
     */
    public boolean closeGame(int gameId, LocalDate finishDate, int winnerId) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call close_game(?, ?, ?)}");

        cStmt.setInt(1, gameId);
        cStmt.setDate(2, Date.valueOf(finishDate));
        cStmt.setInt(3, winnerId);

        int count = cStmt.executeUpdate();

        return (count > 0);
    }

    /**
     * Returns winner id from a specified game
     * @param gameId Session id
     * @return User id of the winner
     * @throws SQLException
     */
    public int getWinner(int gameId) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call getWinner(?, ?)}");

        cStmt.setInt(1, gameId);
        cStmt.registerOutParameter(2, Types.INTEGER);

        int winnerId = -1;
        if (cStmt.execute())
            winnerId = cStmt.getInt(2);

        return winnerId;
    }
}