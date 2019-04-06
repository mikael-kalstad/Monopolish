package com.teamfour.monopolish.account;

import com.mysql.cj.protocol.Resultset;
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
    public int insertAccount(Account account, String password) throws SQLException {
        CallableStatement cStmt = null;
        int status = 0;
        try {
            getConnection();
            //cStmt = connection.prepareCall("{call account_insert_user(?, ?, ?, ?, ?)}");
            cStmt = connection.prepareCall("{call account_insert_user(?, ?, ?, ?)}");

            cStmt.setString(1, account.getUsername());
            cStmt.setString(2, account.getEmail());
            cStmt.setString(3, password);
            cStmt.setDate(4, Date.valueOf(account.getRegDate()));
            //cStmt.registerOutParameter(5, Types.INTEGER);

            if (cStmt.execute())
                //status = cStmt.getInt(5);
                status = 0;
        } catch (SQLIntegrityConstraintViolationException sql) {
            if (sql.getMessage().contains(account.getUsername())) {
                System.out.println("Brukernavn ikke unikt");
                status = 1;
            } else if (sql.getMessage().contains(account.getEmail())) {
                System.out.println("Email ikke unik");
                status = 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            close(cStmt);
            releaseConnection();
        }

        return status;
    }

    /**
     * Attempts to reset the password
     * @param username
     * @param currentPassword
     * @param newPassword
     * @return 1 if successful, 0 if no user, -1 if wrong password
     */
    public int resetPassword(String username, String currentPassword, String newPassword) throws SQLException {
        CallableStatement cStmt = null;
        int status = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call account_reset_password(?, ?, ?, ?)}");

            cStmt.setString(1, username);
            cStmt.setString(2, currentPassword);
            cStmt.setString(3, newPassword);
            cStmt.registerOutParameter(4, Types.BIT);

            cStmt.executeUpdate();

            status = cStmt.getInt(4);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            close(cStmt);
            releaseConnection();
        }

        return status;
    }

    /**
     * Finds a user based on its credentials
     * @param username Username of the account
     * @param password Password of the account
     * @return Null if credentials are wrong
     */
    public Account getAccountByCredentials(String username, String password) throws SQLException {
        CallableStatement cStmt = null;
        ResultSet rs = null;
        Account account = null;
        try {
            getConnection();
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
            throw new SQLException();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection();
        }

        return account;
    }

    public void setActive(String username) /*throws SQLException */{
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
            releaseConnection();
        }
    }

    public boolean getActive(String username) {
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
            releaseConnection();
        }
        return active;
    }

    public int getGamesPlayed(String username) /*throws SQLException */{
        CallableStatement cStmt = null;
        ResultSet rs = null;
        int games = 0;
        try {
            getConnection();
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
            //throw new SQLException();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection();
        }
        return games;
    }

    public int getHighscore(String username) /*throws SQLException */{
        CallableStatement cStmt = null;
        ResultSet rs = null;
        int score = 0;
        try {
            getConnection();
            cStmt = connection.prepareCall("{call account_highscore(?)}");

            cStmt.setString(1, username);

            if (cStmt.execute()) {
                rs = cStmt.getResultSet();
                if (rs.next())
                    score = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new SQLException();
        } finally {
            close(rs);
            close(cStmt);
            releaseConnection();
        }
        return score;
    }
}
