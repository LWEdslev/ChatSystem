import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import spark.Spark;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        runRestAPI();
    }

    static void runRestAPI() {
        Database db = new Database("sqlite.db");
        db.resetTables();

        System.out.println("Server started on http://localhost:8080/");

        Spark.port(8080);

        Spark.staticFiles.externalLocation("../frontend/build/");
        Path path = Path.of("../frontend/build/index.html");

        try {
            String html = Files.readString(path);
            Spark.get("/", (req, res) -> html);
        } catch (Exception e) {
            System.out.println(e);
        } 

        Spark.post("/post-message", (req, res) -> {
            JSONObject jsonMessage = new JSONObject(req.body());
            Message message = new Message(jsonMessage);
            db.postMessage(message);
            res.status(204); // no content response
            return "";
        });

        Spark.get("/get-all-messages", (req, res) -> {
            JSONArray messages = new JSONArray();
            for (Message message : db.getAllMessages()) {
                messages.put(new JSONObject(message));
            }
            return messages;
        });

        Spark.get("/get-messages-after/:id", (req, res) -> {
            UUID id = UUID.fromString(req.params(":id"));
            System.out.println("Getting messages after " + id);
            JSONArray messages = new JSONArray();
            for (Message message : db.getMessagesAfter(id)) {
                messages.put(new JSONObject(message));
            }
            return messages;
        });
    }

    static void test() {
        Database db = new Database("sqlite.db");
        db.resetTables();

        db.postMessage(new Message("Hej med dig fra Alice", "Alice"));
        db.postMessage(new Message("Hej med dig fra Bob", "Bob"));
        
        System.out.println(db.getAllMessages());
    } 

    static void test2() {
        Database db = new Database("sqlite.db");
        db.resetTables();

        sendMessages(db, 10);

        List<Message> messages = db.getAllMessages();

        Message middleMessage = messages.get(5);
        List<Message> messagesAfter = db.getMessagesAfter(middleMessage.getID());

        assert(messagesAfter.size() == 4);
    }

    static void sendMessages(Database db, int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            db.postMessage(new Message("Dette er besked nr. " + i, "Alice"));
        }
    }
}