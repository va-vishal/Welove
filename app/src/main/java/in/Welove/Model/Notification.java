package in.Welove.Model;

public class Notification {
    private String notificationId;
    private String currentuserid;
    private String otheruserid;
    private String title;
    private String body;
    private String notificationType;
    private long timestamp;
    private boolean seen;


    public Notification() {
    }

    public Notification(String notificationId, String currentuserid, String otheruserid, String title, String body, String notificationType, long timestamp, boolean seen) {
        this.notificationId = notificationId;
        this.currentuserid = currentuserid;
        this.otheruserid = otheruserid;
        this.title = title;
        this.body = body;
        this.notificationType = notificationType;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getCurrentuserid() {
        return currentuserid;
    }

    public void setCurrentuserid(String currentuserid) {
        this.currentuserid = currentuserid;
    }

    public String getOtheruserid() {
        return otheruserid;
    }

    public void setOtheruserid(String otheruserid) {
        this.otheruserid = otheruserid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
