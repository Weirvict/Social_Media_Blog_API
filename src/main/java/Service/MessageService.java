package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    MessageDAO messageDAO;

    // Default constructor
    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    // Constructor for if exists
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    /**
     * Inserts a new message after validating message text and posted_by. REQs from
     * README #3
     * 
     * @param message The Message object to insert (no message_id yet)
     * @return The inserted Message object, or null if validation fails.
     */
    public Message createMessage(Message message) {
        // Validate: message_text is not blank and <= 255 characters
        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            return null;
        }
        if (message.getMessage_text().length() > 255) {
            return null;
        }

        // Validate: posted_by must be a valid user (positive ID)
        if (message.getPosted_by() <= 0) {
            return null;
        }

        // Passes all checks → call DAO to insert
        return messageDAO.insertMessage(message);
    }

    /**
     * Retrieves all messages from the database.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    /**
     * Retrieves a single message by its message_id.
     */
    public Message getMessageById(int id) {
        return messageDAO.getMessageId(id);
    }

    /**
     * Deletes a message by its ID and returns the deleted message.
     */
    public Message deleteMessageById(int id) {
        return messageDAO.deleteMessageById(id);
    }

    /**
     * Updates a message's text if valid and message exists.
     * 
     * @param id      The message_id of the message to update
     * @param newText The new message_text
     * @return Updated Message object, or null if validation fails or not found
     */
    public Message updateMessageText(int id, String newText) {
        // Validate: message_text not blank or too long
        if (newText == null || newText.isBlank()) {
            return null;
        }
        if (newText.length() > 255) {
            return null;
        }

        // Only update if the message exists
        Message existing = messageDAO.getMessageId(id);
        if (existing == null) {
            return null;
        }

        return messageDAO.updateMessage(id, newText);
    }

    /**
     * Retrieves all messages posted by a specific account_id.
     */
    public List<Message> getMessagesByAccountId(int accountId) {
        return messageDAO.getMessagesByAccountId(accountId);
    }
}
