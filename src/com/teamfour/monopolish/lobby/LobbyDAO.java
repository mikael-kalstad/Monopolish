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

    public int newLobby(String username) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call new_lobby(?, ?)}");

        cStmt.setString(1, username);
        cStmt.registerOutParameter(2, Types.INTEGER);

        int lobby_id = -1;
        if (cStmt.executeUpdate() > 0) lobby_id = cStmt.getInt(2);

        return lobby_id+1;
    }

    /**
     * Add the specified user to the specified lobby
     * @param lobby_id Lobby id
     * @param username Username
     * @return True if successful
     * @throws SQLException
     */
    public boolean addPlayer(String username, int lobby_id) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call join_lobby(?, ?, ?)}");

        cStmt.setString(1, username);
        cStmt.setInt(2, lobby_id);
        cStmt.registerOutParameter(3, Types.BOOLEAN);

        boolean res = false;
        if (cStmt.executeUpdate() > 0) res  = cStmt.getBoolean(3);

        return res;
    }

    /**
     * Deletes the specified user from the specified lobby
     * @param lobby_id Lobby id
     * @param username Username
     * @return True if successful
     * @throws SQLException
     */
    public boolean removePlayer(String username, int lobby_id) throws SQLException {
        getConnection();
        cStmt = connection.prepareCall("{call lobby_delete_user(?, ?)}");

        cStmt.setInt(1, lobby_id);
        cStmt.setString(2, username);

        int count = cStmt.executeUpdate();

        return (count > 0);
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

    public ArrayList<String[]> getAllLobbies() throws SQLException {
        ArrayList<String[]> lobbyInfo = new ArrayList<>();

        getConnection();
        cStmt = connection.prepareCall("{CALL getAllLobbies()}");

        ResultSet rs = cStmt.executeQuery();

        while (rs.next()) {
            String[] info = {rs.getString(1), rs.getString(2), rs.getString(3)};
            lobbyInfo.add(info);
        }

        releaseConnection();

        return lobbyInfo;
    }
}
