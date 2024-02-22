import java.util.UUID;

import org.json.JSONObject;

public class Message {
    private UUID id;
    private String content;
    private String fromUsername;

    public Message(String content, String fromUsername) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.fromUsername = fromUsername;
    }

    public Message(UUID id, String content, String fromUsername) {
        this.id = id;
        this.content = content;
        this.fromUsername = fromUsername;
    }

    public Message(JSONObject jsonMessage) {
        this.id = UUID.randomUUID();
        this.content = jsonMessage.getString("content");
        this.fromUsername = jsonMessage.getString("fromUsername");
    }

    public UUID getID() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Message {\n" +
                "  id = " + id +
                "\n  content = '" + content + '\'' +
                "\n  from = " + fromUsername +
                "\n}";
    }

    public String getFromUsername() {
        return this.fromUsername;
    }
}

