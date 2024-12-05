package in.Welove.Model;
public class VideoCallModel {
    private String calleruid;
    private String meetingRoomName; // Add this field

    public VideoCallModel() {}

    public String getCalleruid() {
        return calleruid;
    }

    public void setCalleruid(String calleruid) {
        this.calleruid = calleruid;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }
}
