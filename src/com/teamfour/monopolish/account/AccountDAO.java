package com.teamfour.monopolish.account;

import com.teamfour.monopolish.database.DataAccessObject;

import java.sql.*;

/**
 * Handles database communication towards the 'account' table in the database.
 *
 * @author      Eirik Hemstad
 * @version     1.1
 */

public class AccountDAO extends DataAccessObject {

    /**
     * Inserts an account object into the account table
     * @param account Object to insert
     * @return True if the operation was successful, false if this user already exists
     * @throws SQLException
     */
    public int insertAccount(Account account, String password) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        int status = 0;
        try {
            //cStmt = connection.prepareCall("{call account_insert_user(?, ?, ?, ?, ?)}");
            cStmt = connection.prepareCall("{call account_insert_user(?, ?, ?, ?)}");

            cStmt.setString(1, account.getUsername());
            cStmt.setString(2, account.getEmail());
            cStmt.setString(3, password);
            cStmt.setDate(4, Date.valueOf(account.getRegDate()));

            if (cStmt.execute())
                status = 0;
        } catch (SQLIntegrityConstraintViolationException sql) {
            if (sql.getMessage().contains(account.getUsername())) {
                status = 1;
            } else if (sql.getMessage().contains(account.getEmail())) {
                status = 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }

        return status;
    }

    /**
     * Finds a user based on its credentials
     * @param username Username of the account
     * @param password Password of the account
     * @return Null if credentials are wrong
     */
    public Account getAccountByCredentials(String username, String password) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        Account account = null;
        try {
            cStmt = connection.prepareCall("{call account_validate_user(?, ?)}");

            cStmt.setString(1, username);
            cStmt.setString(2, password);

            if(cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (!rs.next()) {
                    return null;
                }

                account = new Account(rs.getString(1), rs.getString(2), rs.getDate(3).toLocalDate(),
                        0, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }

        return account;
    }

    /**
     * Sets the account active stat as false
     * @param username Username of the account
     */
    public void setInactive(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call account_set_inactive(?)}");

            cStmt.setString(1, username);
            cStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(cStmt);
            releaseConnection(connection);
        }
    }

    /**
     * Gets the account active stat
     * @param username Username of the account
     */
    public boolean getActive(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        boolean active = false;

        try {
            getConnection();
            cStmt = connection.prepareCall("{call account_get_active(?)}");

            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                while (rs.next())
                    active = rs.getBoolean("active");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return active;
    }

    /**
     * Gets the number of games the user has played
     * @param username Username of the account
     * @return number of games played
     */
    public int getGamesPlayed(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        int games = 0;
        try {
            cStmt = connection.prepareCall("{call account_games_played(?)}");

            cStmt.setString(1, username);

            if(cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (!rs.next()) {
                    return (0);
                }
                games = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return games;
    }

    /**
     * Gets the highscore of the user
     * @param username Username of the account
     * @return the highscore of the user
     */
    public int getHighscore(String username) {
        Connection connection = getConnection();
        CallableStatement cStmt = null;
        ResultSet rs = null;
        int score = 0;
        try {
            cStmt = connection.prepareCall("{call account_highscore(?)}");

            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (rs.next())
                    score = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection(connection);
        }
        return score;
    }
}
