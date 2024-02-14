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

public class Database implements IDatabase {
    private Connection conn;

    public Database(String filename) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::resource:" + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetTables() {
        Statement stmt = null;
        try {
            stmt = this.conn.createStatement();
            stmt.setQueryTimeout(5);
            stmt.executeUpdate("""
                DROP TABLE IF EXISTS users;
                DROP TABLE IF EXISTS messages;
                DROP TABLE IF EXISTS messages_info;

                CREATE TABLE users (
                    id VARCHAR(36) PRIMARY KEY, 
                    username TEXT
                );
                CREATE TABLE messages (
                    id VARCHAR(36) PRIMARY KEY, 
                    content TEXT
                );
                CREATE TABLE messages_info (
                    id VARCHAR(36) PRIMARY KEY, 
                    from_id VARCHAR(36), 
                    timestamp DATETIME
                );
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

    @Override
    public void postMessage(Message message) {
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        try {
            stmt1 = conn.prepareStatement("INSERT INTO messages VALUES(?, ?)");

            stmt2 = conn.prepareStatement("INSERT INTO messages_info VALUES(?, ?, ?)");

            stmt1.setString(1, message.getID().toString());
            stmt1.setString(2, message.getContent());
            stmt2.setString(1, message.getID().toString());
            stmt2.setString(2, message.getFromID().toString());
            stmt2.setString(3, "datetime('now')");

            stmt1.executeUpdate();
            stmt2.executeUpdate();
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

    @Override
    public void newUser(UUID userID, String username) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO users VALUES(?, ?)");
            stmt.setString(1, userID.toString());
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<String> getUsername(UUID userID) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT username FROM users WHERE id = ?");
            stmt.setString(1, userID.toString());
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                return Optional.of(res.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Message> getMessagesFrom(UUID userID) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("""
                SELECT m.id AS message_id, m.content, mi.from_id, mi.timestamp
                FROM messages m
                JOIN messages_info mi ON m.id = mi.id
                WHERE mi.from_id = ?
            """);

            stmt.setString(1, userID.toString());
            ResultSet res = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (res.next()) {
                UUID message_id = UUID.fromString(res.getString("message_id"));
                String content = res.getString("content");
                UUID from = UUID.fromString(res.getString("from_id"));
                messages.add(new Message(message_id, content, from));
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

        return new ArrayList<>();
    }

    @Override
    public List<Message> getAllMessages() {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("""
                SELECT m.id AS message_id, m.content, mi.from_id, mi.timestamp
                FROM messages AS m
                JOIN messages_info AS mi ON m.id = mi.id
            """);
            
            List<Message> messages = new ArrayList<>();
            while (res.next()) {
                UUID message_id = UUID.fromString(res.getString("message_id"));
                String content = res.getString("content");
                UUID from = UUID.fromString(res.getString("from_id"));
                messages.add(new Message(message_id, content, from));
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
        return new ArrayList<>();
    }
}

interface IDatabase {
    void resetTables();
    void postMessage(Message message);
    List<Message> getAllMessages();
    List<Message> getMessagesFrom(UUID userID);
    void newUser(UUID userID, String username);
    Optional<String> getUsername(UUID userID);
}