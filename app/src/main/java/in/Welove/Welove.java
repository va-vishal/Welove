package in.Welove;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Welove extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityReferences = 0; // Initialize to 0
    private boolean isActivityChangingConfigurations = false;
    private final long appID = 405107665;
    private final String appSign = "38a7d30f075a3442d4830d35880ba11d26079c8bfe1aa584cd71c8530fae0193";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        registerActivityLifecycleCallbacks(this);
        initZegoCallService(); // Initialize Zego Cloud Call Service
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        updateStatus(false); // Ensure to update status when app is terminated
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (activityReferences == 0 && !isActivityChangingConfigurations) {
            updateStatus(true); // App enters foreground
        }
        activityReferences++;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityReferences--;
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (activityReferences == 0 && !isActivityChangingConfigurations) {
            updateStatus(false); // App goes to background
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

    private void updateStatus(boolean status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.e("WeloveApp", firebaseUser.getUid() + " current userid ");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

            if (status) {
                reference.child("status").setValue(true);
                reference.child("lastSeen").setValue("");
            } else {
                reference.child("status").setValue(false);
                Calendar time1 = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm,a");
                String saveTime = currentTime.format(time1.getTime());
                reference.child("lastSeen").setValue(saveTime);
            }
        }
    }

    private void initZegoCallService() {
        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);

            reference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);
                    if (userName == null) {
                        userName = "Unknown User";
                    }
                    ZegoUIKitPrebuiltCallService.init(
                            (Application) getApplicationContext(),
                            appID,
                            appSign,
                            userID,
                            userName,
                            callInvitationConfig
                    );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("WeloveApp", "Failed to retrieve username: " + databaseError.getMessage());
                }
            });
        }
    }

}
