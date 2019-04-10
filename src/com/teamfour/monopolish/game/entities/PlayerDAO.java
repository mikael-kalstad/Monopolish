package com.teamfour.monopolish.game.entities;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.*;
import java.util.ArrayList;
/**
 * Handles Player-DB connection and methods
 *
 * @author      lisawil & eirikhem
 * @version     1.0
 */
public class PlayerDAO extends DataAccessObject {
    /**
     * removes a player that forfiets the game, by giving this player active status == 2
     *
     * @param game_id  the id of the current game
     * @param username the username of the player that is removed
     */
    public void removePlayer(int game_id, String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call player_remove(?, ?)}");

            cStmt.setString(1, username);
            cStmt.setInt(2, game_id);
            cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     * updates a players, position or money.
     *
     * @param player  the Player object that gets updated
     * @param game_id the id of the current game
     */
    public boolean updatePlayer(Player player, int game_id) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int count = 0;
        try {
            cStmt = connection.prepareCall("{call player_update(?, ?, ?, ?, ?, ?, ?, ?)}");

            cStmt.setString(1, player.getUsername());
            cStmt.setInt(2, game_id);
            cStmt.setInt(3, player.getPosition());
            cStmt.setInt(4, player.getMoney());
            cStmt.setBoolean(5, player.isInJail());
            cStmt.setBoolean(6, player.isBankrupt());
            cStmt.setInt(7, player.getActive());
            cStmt.setBoolean(8, player.hasFreeParking());

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
     * create Player objects for all players with the given game_id
     *
     * @param gameId the id of the current game
     */
    public ArrayList<Player> getPlayersInGame(int gameId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<Player> players = new ArrayList<>();
        try {
            cStmt = connection.prepareCall("{call player_getByGameId(?)}");

            cStmt.setInt(1, gameId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    String username = rs.getString(1);
                    int money = rs.getInt(2);
                    int position = rs.getInt(3);
                    boolean inJail = rs.getBoolean(4);
                    boolean bankrupt = rs.getBoolean(5);
                    int active = rs.getInt(6);
                    boolean freeParking = rs.getBoolean(7);
//                int score = rs.getInt(7);

                    players.add(new Player(username, money, position, inJail, bankrupt, active, freeParking));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return players;
    }


    /**
     * ends the game and registers each player's score in the database
     *
     * @param game_id the id of the current game
     * @param username the username of the player
     */
    public void endGame(int game_id, String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call player_endgame(?,?)}");

            for (int i = 0; i < 10; i++) {
                cStmt.setInt(1, game_id);
                cStmt.setString(2, username);
                cStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     * Makes a String[][] with top 10 highscores
     *
     * @return list 2d String array with players(username) and scores
     */

    public String[][] getHighscoreList() {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        String[][] list = new String[10][2];

        try {
            cStmt = connection.prepareCall("{call player_get_highscore()}");

            if (cStmt.execute()) {
                int counter = 0;
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    list[counter][0] = rs.getString(1);
                    list[counter][1] = rs.getString(2);

                    counter++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return list;
    }

    /**
     * Set forfeit status on player in game
     *
     * @param username      username
     * @param gameId        gameId
     * @param forfeitStatus 0 = default, 1 = quit, 2 = continue
     */
    public void setForfeitStatus(String username, int gameId, int forfeitStatus) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call player_set_forfeit(?, ?, ?)}");  // player_id, game_id, forfeit_status
            // 0 = default, 1 = quit, 2 = continue
            cStmt.setString(1, username);
            cStmt.setInt(2, gameId);
            cStmt.setInt(3, forfeitStatus);

            cStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }


    /**
     * Gets the current forfeit status
     * @param gameId
     * @return // 0 = default, 1 = quit, 2 = continue
     */

    public int[] getForfeitStatus(int gameId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        int[] list = new int[2];

        try {
            cStmt = connection.prepareCall("{call player_get_forfeit(?)}");  // player_id, game_id, forfeit_status

            cStmt.setInt(1, gameId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    list[0] = rs.getInt(1);
                    list[1] = rs.getInt(2);
                }
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return list;
    }

    /**
     * Adds a trade to DB
     * @param seller
     * @param buyer
     * @param sellerPrice
     * @param buyerPrice
     * @param propertyId
     */

    public void addTrade(String seller, String buyer, int sellerPrice, int buyerPrice, int propertyId) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call trading_add_trade(?, ?, ?, ?, ?)}");

            cStmt.setString(1,seller);
            cStmt.setString(2, buyer);
            cStmt.setInt(3, sellerPrice);
            cStmt.setInt(4, buyerPrice);
            cStmt.setInt(5, propertyId);

            cStmt.executeUpdate();
            System.out.println("adding trade.....");

        } catch (SQLException sql) {
            sql.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     * Gets the elements of the Trade
     *
     * @param username username of the player
     * @return String ArrayList of Strings, containing seller, buyer, price and properties in the trade.
     */
    public ArrayList<String[]> getTrade(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        ArrayList<String[]> props = new ArrayList<>();

        try {
            // seller_id, buyer_id, price, prperty_id
            cStmt = connection.prepareCall("{call trading_get_trade4(?)}");  // player_id, game_id, forfeit_status

            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    String[] data = new String[5];
                    data[0] = rs.getString(1); // sellerId
                    data[1] = rs.getString(2); // buyerId
                    data[2] = rs.getString(3); // sellerPrice
                    data[3] = rs.getString(4); // buyerPrice
                    data[4] = rs.getString(5); // propId

                    props.add(data);
                }
            }
        } catch(SQLException sql){
            sql.printStackTrace();
        } finally{
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return props;
    }

    /**
     * sets the trade as accepted
     *
     * @param seller username of the player that proposed the trade
     * @param buyer username of the player that accepted the trade
     */
    public void acceptTrade(String seller, String buyer) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call trading_accept_trade(?, ?)}");  // player_id

            cStmt.setString(1, seller);
            cStmt.setString(2, buyer);
        } catch(SQLException sql){
            sql.printStackTrace();
        } finally{
            close(cStmt);
            releaseConnection(connection);
        }
    }
    /**
     * checks if the trade is accepted
     *
     * @param username username of the current player
     */
    public boolean isTrade(String username) {  // check if trade on user
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        boolean trade = false;
        try {
            // seller_id, buyer_id, price, prperty_id
            cStmt = connection.prepareCall("{call trading_is_trade(?)}");  // player_id, game_id, forfeit_status
            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    trade = rs.getBoolean(1);
                }
            }
        } catch(SQLException sql){
            sql.printStackTrace();
        } finally{
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return trade;
    }

    /**
     * Removes trades from DB
     * @param username
     */

    public void removeTrade(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call trading_remove_trade(?)}");  // player_id

            cStmt.setString(1, username);
        } catch(SQLException sql){
            sql.printStackTrace();
        } finally{
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     *
     * @param gameId
     * @return
     */


    public boolean getForfeitCheck(int gameId){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        boolean checked = false;
        try {
            cStmt = connection.prepareCall("{call get_forfeit_check(?)}");
            cStmt.setInt(1, gameId);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next()) {
                    checked= rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return(checked);
    }

    public void setForfeitCheck(int gameId, String username, boolean check){
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            //getConnection();
            cStmt = connection.prepareCall("{call set_forfeit_check(?,?,?)}");
            cStmt.setInt(1, gameId);
            cStmt.setString(2, username);
            cStmt.setBoolean(3, check);

            cStmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }
}