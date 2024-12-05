package in.Welove.FCM;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {

    private final String userFcmToken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/welove-20c9d/messages:send";

    public FcmNotificationSender(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void sendNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObject = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();

            notificationObject.put("title", title);
            notificationObject.put("body", body);

            messageObject.put("token", userFcmToken);
            messageObject.put("notification", notificationObject);

            mainObject.put("message", messageObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObject, response -> {
                // Handle the response here if needed
            }, volleyError -> {
                // Handle the error here
                volleyError.printStackTrace();
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    AccessToken accessToken = new AccessToken();
                    String accessKey = accessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer " + accessKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
