package com.teamfour.monopolish.lobby;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Handles all database communication to the Lobby table in the database
 *
 * @author      Eirik Hemstad
 * @version     1.2
 */

public class LobbyDAO extends DataAccessObject {

    /**
     * Makes the user join the first available lobby (with less than 4 players). If none were found,
     * create a new one
     * @param username Username
     * @return The lobby id
     * @throws SQLException
     */
    public int insertLobby(String username) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call lobby_insert(?, ?)}");

        cStmt.setString(1, username);
        cStmt.registerOutParameter(2, Types.INTEGER);

        int roomId = -1;
        if (cStmt.executeUpdate() > 0)
            roomId = cStmt.getInt(2);

        return roomId;
    }

    /**
     * Sets the specified users 'ready' status
     * @param roomId Lobby id
     * @param username Username
     * @param ready Ready or not?
     * @return True if successful
     * @throws SQLException
     */
    public boolean setReady(int roomId, String username, boolean ready) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call lobby_set_player_ready(?, ?, ?)}");

        cStmt.setInt(1, roomId);
        cStmt.setString(2, username);
        cStmt.setBoolean(3, ready);

        int count = cStmt.executeUpdate();

        return (count > 0);
    }

    /**
     * Deletes an entire lobby session
     * @param roomId Id of the lobby session
     * @return True if successful
     * @throws SQLException
     */
    public boolean deleteLobby(int roomId) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call lobby_delete(?)}");

        cStmt.setInt(1, roomId);

        int count = cStmt.executeUpdate();

        return (count > 0);
    }

    /**
     * Retrieves all the active users in this lobby
     * @param roomId
     * @return
     */
    public ArrayList<String> getUsersInLobby(int roomId) throws SQLException {
        ArrayList<String> users = new ArrayList<>();

        getConnection();
        cStmt = connection.prepareCall("{CALL lobby_get_users_in_lobby(?)}");

        cStmt.setInt(1, roomId);

        ResultSet rs = cStmt.executeQuery();

        while (rs.next()) {
            users.add(rs.getString(1));
        }

        releaseConnection();

        return users;
    }

    /**
     * Deletes the specified user from the specified lobby
     * @param roomId Lobby id
     * @param username Username
     * @return True if successful
     * @throws SQLException
     */
    public boolean deleteUserFromLobby(int roomId, String username) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call lobby_delete_user(?, ?)}");

        cStmt.setInt(1, roomId);
        cStmt.setString(2, username);

        int count = cStmt.executeUpdate();

        return (count > 0);
    }
}
