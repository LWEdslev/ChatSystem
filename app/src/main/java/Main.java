import java.util.UUID;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        Spark.get("/hello", (req, res) -> "Hello, World!");
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