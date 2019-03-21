package com.teamfour.monopolish.lobby;

import com.mysql.cj.xdevapi.SqlDataResult;
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
    public int insertLobby(String username) {
        int roomId = -1;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call lobby_insert(?, ?)}");

            cStmt.setString(1, username);
            cStmt.registerOutParameter(2, Types.INTEGER);


            if (cStmt.executeUpdate() > 0)
                roomId = cStmt.getInt(2);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
        }

        return roomId;
    }


    public int newLobby(String username, String lobbyname) {
        int lobby_id = -1;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call new_lobby(?, ?, ?)}");

            cStmt.setString(1, username);
            cStmt.setString(2, lobbyname);
            cStmt.registerOutParameter(3, Types.INTEGER);


            if (cStmt.executeUpdate() > 0) lobby_id = cStmt.getInt(3);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally{
            releaseConnection();
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
        boolean res = false;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call join_lobby(?, ?, ?)}");

            cStmt.setString(1, username);
            cStmt.setInt(2, lobby_id);
            cStmt.registerOutParameter(3, Types.BOOLEAN);


            if (cStmt.executeUpdate() > 0) res = cStmt.getBoolean(3);
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
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
        int count = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call lobby_delete_user(?, ?)}");

            cStmt.setInt(1, lobby_id);
            cStmt.setString(2, username);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
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
        int count = 0;
        try {
            getConnection();
            System.out.println("Setting ready in procedure.. " + ready);
            cStmt = connection.prepareCall("{call lobby_set_player_ready(?, ?, ?)}");

            cStmt.setInt(1, roomId);
            cStmt.setString(2, username);
            cStmt.setBoolean(3, ready);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
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
        int count = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call lobby_delete(?)}");

            cStmt.setInt(1, roomId);

            count = cStmt.executeUpdate();
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
        }
        return (count > 0);
    }

    /**
     * Retrieves all the active users in this lobby
     * @param roomId
     * @return
     */
    public ArrayList<String> getUsersInLobby(int roomId) {
        ArrayList<String> users = new ArrayList<>();
        try {

            getConnection();
            cStmt = connection.prepareCall("{CALL lobby_get_users_in_lobby(?)}");

            cStmt.setInt(1, roomId);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getString(1));
            }
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
        }
        return users;
    }

    public ArrayList<String[]> getAllLobbies() {
        ArrayList<String[]> lobbyInfo = new ArrayList<>();
        try {

            getConnection();
            cStmt = connection.prepareCall("{CALL getAllLobbies()}");

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                String[] info = {rs.getString(1), rs.getString(2), String.valueOf(rs.getBoolean(3)), rs.getString(4)};
                lobbyInfo.add(info);
            }
        }catch (SQLException sql){
            sql.printStackTrace();
        }finally {
            releaseConnection();
        }
        return lobbyInfo;
    }

    public int getAllReadyInLobby(int lobby_id) {
        int num = 0;
        getConnection();

        try {
            ResultSet rs;
            cStmt = connection.prepareCall("{CALL getALlReadyInLobby(?)}");
            cStmt.setInt(1, lobby_id);
            rs = cStmt.executeQuery();

            if (rs.next()) num = rs.getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            releaseConnection();
        }
        return num;
    }

    public void removeEmptyLobbies() {
        getConnection();

        try {
            cStmt = connection.prepareCall("{CALL lobby_removeEmptyLobbies()}");
            cStmt.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            releaseConnection();
        }
    }
}
