package Service;

import Model.Account;
import DAO.AccountDAO;

public class AccountService {
    AccountDAO accountDAO;

    // Default constructor
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    // Constructor if account dao exists
    public AccountService(AccountDAO accountDAOs){
        this.accountDAO = accountDAOs;
    }

    /**
     * Handles account registration.
     * Validates username and password before inserting into the database.
     * 
     * @param acc Account object without account_id
     * @return Inserted Account with account_id if successful; null if invalid input or username exists.
     */
    public Account register(Account acc) {
        // Validate: username not blank (from readme #1)
        if (acc.getUsername() == null || acc.getUsername().isBlank()) {
            return null;
        }

        // Validate: password at least 4 characters (from readme #1)
        if (acc.getPassword() == null || acc.getPassword().length() < 4) {
            return null;
        }

        // Validate: username must not already exist
        if (accountDAO.usernameExists(acc.getUsername())) {
            return null;
        }

        // If valid, insert account and return it
        return accountDAO.insertAccount(acc);
    }

    /**
     * Handles user login by checking username and password match.
     * 
     * @param username Input username
     * @param password Input password
     * @return Account if credentials are valid; null if not found.
     */
    public Account login(String username, String password) {
        return accountDAO.getAccountByUsernameAndPassword(username, password);
    }




}
