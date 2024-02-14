import java.util.UUID;
import spark.Spark;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("sqlite.db");
        db.resetTables();

        Spark.port(8080);

        Spark.get("/post-message", (req, res) -> {
            JSONObject jsonMessage = new JSONObject(req.body());
            Message message = new Message(jsonMessage);
            db.postMessage(message);
            res.status(204); // no content response
            return "";
        });

        Spark.get("/get-all-messages", (req, res) -> {
            // TODO implement
            return null;
        });

        Spark.get("/get-messages-from", (req, res) -> {
            // TODO implement
            return null;
        });

        Spark.get("/new-user", (req, res) -> {
            // TODO implement
            return null;
        });

        Spark.get("/get-username", (req, res) -> {
            // TODO implement
            return null;
        });
    }

    static void test() {
        Database db = new Database("sqlite.db");
        db.resetTables();
        
        UUID alice = UUID.randomUUID();
        UUID bob = UUID.randomUUID();

        db.newUser(alice, "Alice");

        db.postMessage(new Message("Hej med dig fra Alice", alice));
        db.postMessage(new Message("Hej med dig fra Bob", bob));
        
        System.out.println(db.getAllMessages());
        System.out.println(db.getMessagesFrom(bob));
        System.out.println(db.getUsername(alice)); 
    } 
}