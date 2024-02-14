import java.util.UUID;

public class Message {
    private UUID id;
    private String content;
    private UUID from_id;

    public Message(String content, UUID from_id) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.from_id = from_id;
    }

    public Message(UUID id, String content, UUID from_id) {
        this.id = id;
        this.content = content;
        this.from_id = from_id;
    }

    public UUID getID() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public UUID getFromID() {
        return from_id;
    }

    @Override
    public String toString() {
        return "Message {" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", from_id=" + from_id +
                '}';
    }
}

