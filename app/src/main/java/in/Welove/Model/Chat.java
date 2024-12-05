package in.Welove.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String type;
    private boolean isseen;
    private Long timestamp;
    private String imageurl;
    private String date;
    private long seenTimestamp;

    // Default constructor
    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String type, boolean isseen, Long timestamp, String imageurl, String date, long seenTimestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
        this.timestamp = timestamp;
        this.imageurl = imageurl;
        this.date = date;
        this.seenTimestamp = seenTimestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSeenTimestamp() {
        return seenTimestamp;
    }

    public void setSeenTimestamp(long seenTimestamp) {
        this.seenTimestamp = seenTimestamp;
    }
}
