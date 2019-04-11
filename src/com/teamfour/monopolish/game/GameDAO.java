package com.teamfour.monopolish.game;

import com.teamfour.monopolish.database.DataAccessObject;
import com.teamfour.monopolish.game.property.Street;

import java.sql.*;
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
    public int insertGame(int lobbyId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int gameId = -1;
        try {
            cStmt = connection.prepareCall("{call game_insert(?, ?)}");

            cStmt.setInt(1, lobbyId);
            cStmt.registerOutParameter(2, Types.INTEGER);

            cStmt.executeUpdate();
            gameId = cStmt.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }

        return gameId;
    }

    /**
     * Gets the current player Id in the specified game session
     * @param gameId The session id
     * @return Id of the current player
     * @throws SQLException
     */
    public String getCurrentPlayer(int gameId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        String player = "";
        try {
            cStmt = connection.prepareCall("{call game_get_current_player(?)}");

            cStmt.setInt(1, gameId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (rs.next())
                    player = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
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
    public boolean setCurrentPlayer(int gameId, String currentPlayer) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call game_set_current_player(?, ?)}");

            cStmt.setInt(1, gameId);
            cStmt.setString(2, currentPlayer);

            count = cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
        return (count > 0);
    }

    /**
     * Finish the game and ties up loose ends in the table
     * @param gameId Session id
     * @return True if operation was successful
     */
    public boolean finishGame(int gameId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call game_close(?)}");

            cStmt.setInt(1, gameId);

            count = cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }

        return (count > 0);
    }


    /**
     * Gets everything from the game chat
     * @param gameId The session id
     * @return chat content
     */

    public ArrayList<String[]> getChat(int gameId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<String[]> chatList= new ArrayList<String[]>();
        try {
            cStmt = connection.prepareCall("{call chat_get(?)}");
            cStmt.setInt(1, gameId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    String[] chatLine = new String[3];
                    chatLine[0] = rs.getString("username");
                    chatLine[1] = rs.getString("time_String");
                    chatLine[2] = rs.getString("message");
                    chatList.add(chatLine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return chatList;
    }

    /**
     * adds a chat-message to the chat
     * @param username The session id
     * @param message the chat-message
     */
    public void addChatMessage(String username, String message){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call chat_add(?,?)}");
            cStmt.setString(1, username);
            cStmt.setString(2, message);

            cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    public boolean getForfeit(int gameId){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        boolean forfeit = false;
        try {
            cStmt = connection.prepareCall("{call get_forfeit(?)}");
            cStmt.setInt(1, gameId);
            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                 while (rs.next())
                    forfeit = rs.getBoolean("forfeit");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return forfeit;
    }

    public void setForfeit(int gameId, boolean forfeit){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call set_forfeit(?,?)}");
            cStmt.setInt(1, gameId);
            cStmt.setBoolean(2, forfeit);
            cStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    public String getWinner(int gameId){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        String winner = null;
        try {
            cStmt = connection.prepareCall("{call game_get_winner(?, ?)}");
            cStmt.setInt(1, gameId);
            cStmt.registerOutParameter(2, Types.VARCHAR);
            if (cStmt.execute()) {
                winner = cStmt.getString(2);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return winner;
    }
}
