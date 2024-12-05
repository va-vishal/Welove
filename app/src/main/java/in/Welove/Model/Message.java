package in.Welove.Model;

public class Message {
    private String messageId;
    private String senderId;
    private String messageText;
    private long timestamp;
    private String postimage;

    public Message() {
        // Default constructor required for Firebase
    }


    public Message(String messageId, String senderId, String messageText, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Message(String postimage) {
        this.postimage = postimage;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

// Getters and setters
}
