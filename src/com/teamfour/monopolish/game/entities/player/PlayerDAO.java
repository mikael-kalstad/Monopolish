package com.teamfour.monopolish.game.entities.player;

import com.teamfour.monopolish.database.ConnectionPool;
import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Handles Player-DB connection and methods
 *
 * @author      lisawil & eirikhem
 * @version     1.0
 */
public class PlayerDAO extends DataAccessObject {

    /**
     * creates players in the database and Player objects.
     *
     * @param game_id   the id of the current game
     * @param usernames the usernames of the players that's created
     */

    public ArrayList<Player> createPlayers(int game_id, String[] usernames) throws SQLException {
        ArrayList<Player> players = null;
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call player_create(?, ?)}");
            for (int i = 0; i < usernames.length; i++) {
                cStmt.setInt(1, game_id);
                cStmt.setString(2, usernames[i]);

                players.add(new Player(cStmt.executeQuery().getString("username")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }
        return (null);
    }

    /**
     * creates one player in the database and a Player object.
     * @param game_id the id of the current game
     * @param username the username of the player that is created
     */
    public Player createPlayer(int game_id, String username) throws SQLException {
        ArrayList<Player> players = null;
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call player_create(?, ?)}");

            cStmt.setInt(1, game_id);
            cStmt.setString(2, username);

            players.add(new Player(cStmt.executeQuery().getString("username")));

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }
        return (null);
    }

    /**
     * removes a player that forfiets the game, by giving this player active status == 2
     *
     * @param game_id  the id of the current game
     * @param username the username of the player that is removed
     */
    public void removePlayer(int game_id, String username) {
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call player_remove(?, ?)}");

            cStmt.setString(1, username);
            cStmt.setInt(2, game_id);
            cStmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }

    /**
     * updates a players, position or money.
     *
     * @param player  the Player object that gets updated
     * @param game_id the id of the current game
     */
    public boolean updatePlayer(Player player, int game_id) throws SQLException {
        int count = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call player_update(?, ?, ?, ?, ?, ?, ?, ?)}");

            cStmt.setString(1, player.getUsername());
            cStmt.setInt(2, game_id);
            cStmt.setInt(3, player.getPosition());
            cStmt.setInt(4, player.getMoney());
            cStmt.setBoolean(5, player.isInJail());
            cStmt.setBoolean(6, player.isBankrupt());
            cStmt.setInt(7, player.getActive());
            cStmt.setInt(8, player.getMoney());

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
     * create Player objects for all players with the given game_id
     *
     * @param gameId the id of the current game
     */
    public ArrayList<Player> getPlayersInGame(int gameId) {
        ArrayList<Player> players = new ArrayList<>();
        try {
            getConnection();
            cStmt = connection.prepareCall("{call player_getByGameId(?)}");

            cStmt.setInt(1, gameId);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString(1);
                int money = rs.getInt(2);
                int position = rs.getInt(3);
                boolean inJail = rs.getBoolean(4);
                boolean bankrupt = rs.getBoolean(5);
                int active = rs.getInt(6);
//                int score = rs.getInt(7);

                players.add(new Player(username, money, position, inJail, bankrupt, active));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }

        return players;
    }

    /**
     * ends the game and registers each player's score in the database
     *
     * @param game_id the id of the current game
     */
    public void endGame(int game_id) throws SQLException {
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();
            cStmt = connection.prepareCall("{call player_endgame(?)}");

            for (int i = 0; i < 10; i++) {

                cStmt.setInt(1, game_id);
                cStmt.executeQuery();

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            releaseConnection();
        }
    }

    /**
     * Makes a String[][] with top 10 highscores
     *
     * @return list 2d String array with playes and scores
     */

    public String[][] getHighscoreList() {
        String[][] list = new String[10][2];

        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();

            cStmt = connection.prepareCall("{call player_get_highscore()}");

            ResultSet rs = cStmt.executeQuery();
            int counter = 0;

            while (rs.next()) {
                list[counter][0] = rs.getString(1);
                list[counter][1] = rs.getString(2);

                counter++;
                }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }

        return list;
    }

    /**
     * Set forfeit status on player in game
     *
     * @param username usernmae
     * @param gameId gameId
     * @param forfeitStatus 0 = default, 1 = quit, 2 = continue
     */
    public void setForfeitStatus(String username, int gameId, int forfeitStatus) {
        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();

            cStmt = connection.prepareCall("{call player_set_forfeit(?, ?, ?)}");  // player_id, game_id, forfeit_status
                                                                                // 0 = default, 1 = quit, 2 = continue
            cStmt.setString(1, username);
            cStmt.setInt(2, gameId);
            cStmt.setInt(3, forfeitStatus);

            cStmt.executeQuery();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
    }

    /**
     *
     *
     * @param gameId
     * @return // 0 = default, 1 = quit, 2 = continue
     */

    public int[] getForfeitStatus(int gameId) {
        int[] list = new int[2];

        try {
            connection = ConnectionPool.getMainConnectionPool().getConnection();

            cStmt = connection.prepareCall("{call player_get_forfeit(?)}");  // player_id, game_id, forfeit_status

            cStmt.setInt(1, gameId);

            ResultSet rs = cStmt.executeQuery();

            while (rs.next()) {
                list[0] = rs.getInt(1);
                list[1] = rs.getInt(2);
            }

        } catch(SQLException sql){
                sql.printStackTrace();
        } finally{
                releaseConnection();
        }
        return list;

    }
}