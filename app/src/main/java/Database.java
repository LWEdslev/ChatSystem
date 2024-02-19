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
                DROP TABLE IF EXISTS messages;

                CREATE TABLE messages (
                    id VARCHAR(36) PRIMARY KEY, 
                    content TEXT,
                    from_user TEXT,
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
        try {
            stmt1 = conn.prepareStatement("INSERT INTO messages VALUES(?, ?, ?, datetime('now'))");

            stmt1.setString(1, message.getID().toString());
            stmt1.setString(2, message.getContent());
            stmt1.setString(3, message.getFromUsername());

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
}

interface IDatabase {
    void resetTables();
    void postMessage(Message message);
    List<Message> getAllMessages();
}