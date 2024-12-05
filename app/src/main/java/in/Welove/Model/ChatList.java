package in.Welove.Model;

public class ChatList {

    private String id;
    private String lastMessage;
    private long timestamp;

    public ChatList() {
    }

    public ChatList(String id, String lastMessage, long timestamp) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public ChatList(String receiver) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
