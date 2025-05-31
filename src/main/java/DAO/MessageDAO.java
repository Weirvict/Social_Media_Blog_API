package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * REQs from read me
 * API should be able to process the creation of new messages. 
 * API should be able to retrieve all messages.
 * API should be able to retrieve a message by its ID.
 * API should be able to delete a message identified by a message ID.
 * API should be able to update a message text identified by a message ID.
 * API should be able to retrieve all messages written by a particular user.
 * 
 */

/**
 * Data Access Object (DAO) for managing Message-related database operations.
 * This class connects to the database via ConnectionUtil and performs CRUD operations.
 * NOTE: The ConnectionUtil uses a singleton connection. Do NOT close the connection manually.
 * Used FlightTracker for reference
 */
public class MessageDAO {

    /**
     * Retrieves all messages stored in the database.
     * @return List of all Message objects.
     */
    public List<Message> getAllMessages() {
        // Get shared connection
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            // SQL query to select all rows from the Message table
            String sql = "SELECT * FROM Message";

            // Prepare the SQL
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            // Execute query and get result set
            ResultSet rs = preparedStatement.executeQuery(); 

            // Iterate through each row of the result set
            while (rs.next()) {
                // Create a Message object from the current row's data
                Message mes = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
                // Add message to list
                messages.add(mes); // Add message to list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    // Return list 
        return messages; 
    }

    /**
     * Retrieves a message by its ID.
     * @param id The message_id of the message to retrieve.
     * @return Message object if found, else null.
     */
    public Message getMessageId(int id) {
        // Get shared connection
        Connection connection = ConnectionUtil.getConnection();

        try {
            // SQL to retrieve a specific message by ID
            String sql = "SELECT * FROM Message WHERE message_id = ?";

            // Prepare SQL
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Replace ? with actual ID
            preparedStatement.setInt(1, id); 

            // Execute and get result
            ResultSet rs = preparedStatement.executeQuery(); 

            // If a result is found, create and return a Message object
            if (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // If no result found
        return null; 
    }

    /**
     * Inserts a new message into the database.
     * @param message The Message object containing posted_by, message_text, and timestamp.
     * @return The inserted Message object with generated message_id, or null if insertion fails.
     */
    public Message insertMessage(Message message) {
        // Get shared connection
        Connection connection = ConnectionUtil.getConnection();

        try {
            // SQL to insert a new message (note: message_id is auto-generated)
            String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

            // Prepare and return keys
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Fill in the placeholders
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            // Execute insert
            int rowsAffected = preparedStatement.executeUpdate(); 

            if (rowsAffected > 0) {
                // Get generated message_id
                ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
                if (pkeyResultSet.next()) {
                    int generated_id = pkeyResultSet.getInt(1);
                    return new Message(
                            generated_id,
                            message.getPosted_by(),
                            message.getMessage_text(),
                            message.getTime_posted_epoch()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updates an existing message's text by message_id.
     * @param id The ID of the message to update.
     * @param newText The new message text to replace the old one.
     * @return The updated Message object if successful, otherwise null.
     */
    public Message updateMessage(int id, String newText) {
        Connection connection = ConnectionUtil.getConnection(); // Get shared connection

        try {
            // SQL to update the message_text field
            String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?";
            
            // Prepare SQL
            PreparedStatement preparedStatement = connection.prepareStatement(sql); 
            // New message text
            preparedStatement.setString(1, newText); 
            // Message ID
            preparedStatement.setInt(2, id); 
            // Execute update
            int rowsUpdated = preparedStatement.executeUpdate(); 

            if (rowsUpdated > 0) {
                 // Return updated message
                return getMessageId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Update failed
        return null;
    }

    /**
     * Deletes a message by its ID.
     * @param id The ID of the message to delete.
     * @return The deleted Message object if it existed, otherwise will be null.
     */
    public Message deleteMessageById(int id) {
        // Get shared connection
        Connection connection = ConnectionUtil.getConnection(); 

        // First retrieve the message to return it later
        Message toDelete = getMessageId(id);

        // Make sure the message exists
        if (toDelete == null) return null; 

        try {
            // SQL to delete the message
            String sql = "DELETE FROM Message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id); 
            preparedStatement.executeUpdate(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return deleted message
        return toDelete; 
    }

    /**
     * Retrieves all messages posted by a specific user.
     * @param accountId The ID of the user (account_id).
     * @return List of messages posted by the user.
     */
    public List<Message> getMessagesByAccountId(int accountId) { //Same comments as before
        Connection connection = ConnectionUtil.getConnection(); 
        List<Message> messages = new ArrayList<>();

        try {
            // SQL to select all messages where posted_by = accountId
            String sql = "SELECT * FROM Message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId); 
            ResultSet rs = preparedStatement.executeQuery(); 

            // Loop through each message found
            while (rs.next()) {
                Message mes = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
                messages.add(mes); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return list 
        return messages; 
    }
}
