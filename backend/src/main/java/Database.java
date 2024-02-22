import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

/**
 * Database class with methods for resetting tables, posting messages, and getting messages.
 */
public class Database implements IDatabase {
    private Connection conn;

    /**
     * Constructor for Database class.
     * @param filename the name of the SQLite database file, should be in the resources folder.
     */
    public Database(String filename) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::resource:" + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the tables in the database and creates a new table for messages and indexes for the timestamp and id.
     */
    @Override
    public void resetTables() {
        Statement stmt = null;
        try {
            stmt = this.conn.createStatement();
            stmt.setQueryTimeout(5);
            stmt.executeUpdate("""
                DROP TABLE IF EXISTS messages;

                CREATE TABLE messages (
                    id VARCHAR(36) PRIMARY KEY, 
                    content TEXT,
                    from_user TEXT,
                    timestamp TEXT
                );

                CREATE INDEX idx_timestamp ON messages(timestamp);
                CREATE INDEX idx_id ON messages(id);
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Posts a message to the database.
     * @param message the message to be posted.
     */
    @Override
    public void postMessage(Message message) {
        PreparedStatement stmt1 = null;
        try {
            stmt1 = conn.prepareStatement("INSERT INTO messages VALUES(?, ?, ?, ?)");

            stmt1.setString(1, message.getID().toString());
            stmt1.setString(2, message.getContent());
            stmt1.setString(3, message.getFromUsername());
            stmt1.setString(4, Instant.now() + "");

            stmt1.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt1 != null) { stmt1.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets all messages from the database.
     * @return a list of all messages in the database.
     */
    @Override
    public List<Message> getAllMessages() {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("""
                SELECT * FROM messages ORDER BY timestamp
            """);
            
            List<Message> messages = new ArrayList<>();
            while (res.next()) {
                messages.add(new Message(
                    UUID.fromString(res.getString("id")),
                    res.getString("content"),
                    res.getString("from_user")
                ));
            }
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Returning empty list");
        return new ArrayList<>();
    }

    /**
     * Gets all messages after a given id from the database.
     * @param id the id of the message to get messages after.
     * @return a list of all messages after the given id.
     */
    @Override
    public List<Message> getMessagesAfter(UUID id) {
        Statement stmt = null;
        try {
            // get timestamp of message with id
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT timestamp FROM messages WHERE id = '" + id.toString() + "'");
            String timestamp = res.getString("timestamp");
            
            // get all messages after timestamp
            stmt = conn.createStatement();
            System.out.println(timestamp);
            res = stmt.executeQuery("SELECT * FROM messages WHERE timestamp > '" + timestamp + "' ORDER BY timestamp");

            List<Message> messages = new ArrayList<>();
            while (res.next()) {
                messages.add(new Message(
                    UUID.fromString(res.getString("id")),
                    res.getString("content"),
                    res.getString("from_user")
                ));
            }

            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Returning empty list");
        return new ArrayList<>();
    }    
}

interface IDatabase {
    void resetTables();
    void postMessage(Message message);
    List<Message> getAllMessages();
    List<Message> getMessagesAfter(UUID id);
}