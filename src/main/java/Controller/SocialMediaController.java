package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;


/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    // Services to handle business logic
    AccountService accountService = new AccountService();
    MessageService messageService = new MessageService();

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        //app.get("example-endpoint", this::exampleHandler);
        
        // Register a new account
        app.post("/register", this::handleRegister);

        // Login to existing account
        app.post("/login", this::handleLogin);

        // Post a new message
        app.post("/messages", this::handlePostMessage);

        // Get all messages
        app.get("/messages", this::handleGetAllMessages);

        // Get a message by its ID
        app.get("/messages/{message_id}", this::handleGetMessageById);

        // Delete a message by its ID
        app.delete("/messages/{message_id}", this::handleDeleteMessageById);

        // Update the text of a message
        app.patch("/messages/{message_id}", this::handleUpdateMessage);

        // Get all messages from a specific user
        app.get("/accounts/{account_id}/messages", this::handleGetMessagesByAccount);
        

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    /**
     * Notes:
     * Handlers, Based this off of FlightTracker
     * Response status:
     * 200 (default/OK)
     * 400 (client error)
     * 401 (Unauthorized)
     * 
    */

    /**
     * Handles user registration.
     * Accepts a JSON object without account_id and attempts to create a new account.
     * If the username is taken or input is invalid, returns 400.
     */
    private void handleRegister(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON to Account object
        Account acc = mapper.readValue(ctx.body(), Account.class); 

        // Attempt to register user
        Account registered = accountService.register(acc); 

        if (registered == null) {
            // Validation failed
            ctx.status(400); 
        } else {
            // Return registered user with account_id
            ctx.json(mapper.writeValueAsString(registered)); 
        }
    }

    /**
     * Handles user login.
     * Accepts a JSON object with username and password.
     * Returns 401 if login fails.
     */
    private void handleLogin(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Read JSON input
        Account acc = mapper.readValue(ctx.body(), Account.class); 
        
        // Try logging in
        Account result = accountService.login(acc.getUsername(), acc.getPassword()); 

        if (result == null) {
            ctx.status(401); // Unauthorized
        } else {
            ctx.json(mapper.writeValueAsString(result)); // Return logged-in account
        }
    }

    /**
     * Handles creation of a new message.
     * Accepts a JSON message object and validates content before inserting.
     * Returns 400 if validation fails.
     */
    private void handlePostMessage(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Convert input to Message
        Message msg = mapper.readValue(ctx.body(), Message.class); 
        
        // Attempt to save
        Message created = messageService.createMessage(msg); 

        if (created == null) {
            ctx.status(400); 
        } else {// Return new message
            ctx.json(mapper.writeValueAsString(created)); 
        }
    }

    /**
     * Handles retrieval of all messages.
     * Returns a list of all messages from the database.
     */
    private void handleGetAllMessages(Context ctx) {
        ctx.json(messageService.getAllMessages());
    }

    /**
     * Handles retrieval of a specific message by its ID.
     * Returns an empty body if the message does not exist.
     */
    private void handleGetMessageById(Context ctx) {
        // Get path parameter
        int id = Integer.parseInt(ctx.pathParam("message_id")); 
        // Retrieve message
        Message msg = messageService.getMessageById(id); 

        if (msg != null) {
            ctx.json(msg);
        }
    }

    /**
     * Handles deletion of a message by its ID.
     * Returns the deleted message object or an empty body if none existed.
     */
    private void handleDeleteMessageById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Message deleted = messageService.deleteMessageById(id);

        if (deleted != null) {
            ctx.json(deleted);
        }
    }

    /**
     * Handles updating of a message's text.
     * Only updates if the message exists and the new text is valid.
     */
    private void handleUpdateMessage(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // Get message ID from path
        int id = Integer.parseInt(ctx.pathParam("message_id")); 

        // Get new message_text
        Message update = mapper.readValue(ctx.body(), Message.class); 
        Message updated = messageService.updateMessageText(id, update.getMessage_text());

        if (updated == null) {
            ctx.status(400); 
        } else { // Return updated message
            ctx.json(mapper.writeValueAsString(updated));
        }
    }

    /**
     * Handles retrieval of all messages by a specific account ID.
     * Always returns a list, even if empty.
     */
    private void handleGetMessagesByAccount(Context ctx) {
        // Get account ID from path
        int accountId = Integer.parseInt(ctx.pathParam("account_id")); 
        ctx.json(messageService.getMessagesByAccountId(accountId));
    }
}