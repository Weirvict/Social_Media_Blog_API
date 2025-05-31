package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * REQs from readme
 * API should be able to process new User registrations
 * API should be able to process User logins.
 * 
 */

/**
 * DAO class responsible for accessing the Account table.
 * Provides methods to insert and retrieve account data for registration and
 * login.
 */
public class AccountDAO {

    /**
     * Inserts a new account into the database.
     * Used during registration.
     * 
     * @param account The account to insert (should not include account_id).
     * @return The newly inserted Account object with generated account_id, or null
     *         if insertion failed.
     */
    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            // SQL to insert a new account with unique username
            String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            int rowsAffected = ps.executeUpdate();

            // If insert was successful, retrieve generated account_id
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int accountId = rs.getInt(1);
                    return new Account(accountId, account.getUsername(), account.getPassword());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // insert failed
    }

    /**
     * Retrieves an account by matching username and password.
     * Used during login.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return The matching Account object if found; otherwise, null.
     */
    public Account getAccountByUsernameAndPassword(String username, String password) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            // SQL to find account with exact username and password
            String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String foundUsername = rs.getString("username");
                String foundPassword = rs.getString("password");

                return new Account(accountId, foundUsername, foundPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // login failed
    }

    //EXTRA 
    /**
     * Checks if an account with the given username already exists.
     * Used to enforce unique usernames during registration.
     * 
     * @param username The username to check.
     * @return true if username exists; false otherwise.
     */
    public boolean usernameExists(String username) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM Account WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            // true if at least one match
            return rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
