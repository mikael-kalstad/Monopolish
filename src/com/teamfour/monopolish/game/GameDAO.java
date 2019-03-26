package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    public int insertGame(int lobbyId, String username) {
        int gameId = -1;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call game_insert(?, ?, ?)}");

            cStmt.setInt(1, lobbyId);
            cStmt.setString(2, username);
            cStmt.registerOutParameter(3, Types.INTEGER);

            cStmt.executeUpdate();
            gameId = cStmt.getInt(3);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }

        return gameId;
    }

    /**
     * Gets the current player Id in the specified game session
     * @param gameId The session id
     * @return Id of the current player
     * @throws SQLException
     */
    public String getCurrentPlayer(int gameId) throws SQLException {
        String player = "";
        try {
            getConnection();
            cStmt = connection.prepareCall("{call game_get_current_player(?)}");

            cStmt.setInt(1, gameId);

            ResultSet rs = cStmt.executeQuery();

            if (rs.next())
                player = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return player;
    }

    /**
     * Sets a new current player in the specified session
     * @param gameId Session id
     * @param currentPlayer Current player id
     * @return True if successful
     * @throws SQLException
     */
    public boolean setCurrentPlayer(int gameId, String currentPlayer) throws SQLException {
        int count = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call game_set_current_player(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, currentPlayer);

            count = cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return (count > 0);
    }

    /**
     * Finish the game and ties up loose ends in the table
     * @param gameId Session id
     * @param winnerId Userid of the winner. 0 if no winner
     * @return True if operation was successful
     * @throws SQLException
     */
    public boolean finishGame(int gameId, int winnerId) throws SQLException {
        int count = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call game_close(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setInt(2, winnerId);

            count = cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return (count > 0);
    }

    /**
     * Returns winner id from a specified game
     * @param gameId Session id
     * @return User id of the winner
     * @throws SQLException
     */
    public int getWinner(int gameId) throws SQLException {
        int winnerId = -1;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call game_get_winner(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.registerOutParameter(2, Types.INTEGER);

            winnerId = -1;
            if (cStmt.execute())
                winnerId = cStmt.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }

        return winnerId;
    }

    public ArrayList<String[]> getChat(int gameId) {
        ArrayList<String[]> chatList= new ArrayList<String[]>();
        try {
            getConnection();
            cStmt = connection.prepareCall("{call chat_get(?)}");
            cStmt.setInt(1, gameId);

            ResultSet rs = cStmt.executeQuery();

            while(rs.next()){
                String[] chatLine = new String[3];
                chatLine[0] = rs.getString("username");
                chatLine[1] = rs.getString("time_String");
                chatLine[2] = rs.getString("message");
                chatList.add(chatLine);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
        return chatList;
    }

    public void addChatMessage(String username, String message){
        System.out.println("message in chat from DAO " + message);
        try {
            getConnection();
            cStmt = connection.prepareCall("{call chat_add(?,?)}");
            cStmt.setString(1, username);
            cStmt.setString(2, message);

            cStmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }
}