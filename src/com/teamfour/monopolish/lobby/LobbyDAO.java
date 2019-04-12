package com.teamfour.monopolish.lobby;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.*;
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
     */
    public int insertLobby(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int roomId = -1;
        try {
            cStmt = connection.prepareCall("{call lobby_insert(?, ?)}");

            cStmt.setString(1, username);
            cStmt.registerOutParameter(2, Types.INTEGER);

            if (cStmt.executeUpdate() > 0)
                roomId = cStmt.getInt(2);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
        return roomId;
    }

    /**
     * creates a new lobby with the given lobbyname, with the user in it
     * @param username Username
     * @param lobbyname Lobbyname
     * @return The lobby id
     */
    public int newLobby(String username, String lobbyname) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int lobby_id = -1;
        try {
            cStmt = connection.prepareCall("{call new_lobby(?, ?, ?)}");

            cStmt.setString(1, username);
            cStmt.setString(2, lobbyname);
            cStmt.registerOutParameter(3, Types.INTEGER);

            if (cStmt.executeUpdate() > 0) lobby_id = cStmt.getInt(3);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally{
            close(cStmt);
            releaseConnection(connection);
        }
        return lobby_id+1;
    }

    /**
     * Add the specified user to the specified lobby
     * @param lobby_id Lobby id
     * @param username Username
     * @return True if successful
     * @throws SQLException
     */
    public boolean addPlayer(String username, int lobby_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        boolean res = false;
        try {
            cStmt = connection.prepareCall("{call join_lobby(?, ?, ?)}");

            cStmt.setString(1, username);
            cStmt.setInt(2, lobby_id);
            cStmt.registerOutParameter(3, Types.BOOLEAN);

            if (cStmt.executeUpdate() > 0) res = cStmt.getBoolean(3);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
        return res;
    }

    /**
     * Deletes the specified user from the specified lobby
     * @param lobby_id Lobby id
     * @param username Username
     * @return True if successful
     * @throws SQLException
     */
    public boolean removePlayer(String username, int lobby_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call lobby_delete_user(?, ?)}");

            cStmt.setInt(1, lobby_id);
            cStmt.setString(2, username);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
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
    public boolean setReady(int roomId, String username, boolean ready) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call lobby_set_player_ready(?, ?, ?)}");

            cStmt.setInt(1, roomId);
            cStmt.setString(2, username);
            cStmt.setBoolean(3, ready);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
        return (count > 0);
    }

    /**
     * Deletes an entire lobby session
     * @param roomId Id of the lobby session
     * @return True if successful
     * @throws SQLException
     */
    public boolean deleteLobby(int roomId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call lobby_delete(?)}");

            cStmt.setInt(1, roomId);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
        return (count > 0);
    }

    /**
     * Retrieves all the active users in this lobby
     * @param roomId Id of the lobby session
     * @return
     */
    public ArrayList<String> getUsersInLobby(int roomId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<String> users = new ArrayList<>();
        try {
            cStmt = connection.prepareCall("{CALL lobby_get_users_in_lobby(?)}");

            cStmt.setInt(1, roomId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    users.add(rs.getString(1));
                }
            }
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return users;
    }

    /**
     * Retrieves all active lobbies
     */
    public ArrayList<String[]> getAllLobbies() {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ArrayList<String[]> lobbyInfo = new ArrayList<>();
        ResultSet rs = null;
        try {
            cStmt = connection.prepareCall("{CALL getAllLobbies()}");

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    String[] info = {rs.getString(1), rs.getString(2), String.valueOf(rs.getBoolean(3)), rs.getString(4)};
                    lobbyInfo.add(info);
                }
            }
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return lobbyInfo;
    }
    /**
     * Retrieves all ready users in this lobby
     * @param lobby_id Id of the lobby session
     */
    public int getAllReadyInLobby(int lobby_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int num = 0;
        ResultSet rs = null;
        try {
            cStmt = connection.prepareCall("{CALL getALlReadyInLobby(?)}");
            cStmt.setInt(1, lobby_id);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (rs.next()) num = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return num;
    }
    /**
     * removes any empty lobbies
     */
    public void removeEmptyLobbies() {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{CALL lobby_removeEmptyLobbies()}");
            cStmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }
    /**
     * Returns the id of the lobby in which the user is
     * @param username Username
     */
    public int getLobbyId(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int lobby = 0;
        ResultSet rs = null;
        try {
            cStmt = connection.prepareCall("{CALL lobby_get_id(?)}");
            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (rs.next())
                    lobby = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return lobby;
    }
}
