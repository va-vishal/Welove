package in.Welove.Calls;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import in.Welove.BaseActivity;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.VcModel;
import in.Welove.Model.VideoCallModel;
import in.Welove.R;


public class VideoCallOutgoing extends BaseActivity {

    ImageView imageView;
    TextView tvname,tvprof;
    FloatingActionButton declinebtn;
    String receiver_url,receiver_name,receiver_token,reciver_uid,sender_uid;
    DatabaseReference reference,reference_response,videocallref;
    VcModel model;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    VideoCallModel videoCallModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_outgoing);
        model = new VcModel();

        videoCallModel  = new VideoCallModel();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();

        imageView = findViewById(R.id.og_vc_image);
        tvname = findViewById(R.id.og_vc_name);
        declinebtn = findViewById(R.id.og_vc_decline);

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            reciver_uid = bundle.getString("uid");

        }else {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
        }

        reference = FirebaseDatabase.getInstance().getReference("users").child(reciver_uid);
        videocallref = FirebaseDatabase.getInstance().getReference("vc");
        reference_response = database.getReference("vcref").child(sender_uid).child(reciver_uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve values safely
                    String receiverName = snapshot.child("name").getValue(String.class);
                    String receiverUrl = snapshot.child("imageurl").getValue(String.class);
                    if (receiverName != null) {
                        tvname.setText(receiverName);
                    } else {
                        tvname.setText("Unknown Name");
                    }
                    if (receiverUrl != null && !receiverUrl.isEmpty()) {
                        Picasso.get().load(receiverUrl).into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.defaultimage);
                    }
                } else {
                    Toast.makeText(VideoCallOutgoing.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoCallOutgoing.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        sendCallInvitation();

        checkResponse();

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelVC();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        cancelVC();
    }

    private void cancelVC() {
        DatabaseReference cancelRef;
        cancelRef = FirebaseDatabase.getInstance().getReference("cancel");

        model.setResponse("no");
        cancelRef.child(sender_uid).setValue(model);
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VideoCallOutgoing.this, messagingActivity.class);
        startActivity(intent);
        reference_response.removeValue();
        videocallref.removeValue();
        finish();


    }

    private void checkResponse() {



        reference_response.child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String key = snapshot.child("key").getValue().toString();
                    String response = snapshot.child("response").getValue().toString();

                    if (response.equals("yes")){


                        Toast.makeText(VideoCallOutgoing.this, "Call Accepted", Toast.LENGTH_SHORT).show();

                    }else  if (response.equals("no")){
                        Toast.makeText(VideoCallOutgoing.this, "Call denied", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VideoCallOutgoing.this, messagingActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }else {

                    // Toast.makeText(VideoCallOutgoing.this, "Not responding", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void sendCallInvitation() {

        videoCallModel.setCalleruid(sender_uid);
        videocallref.child(reciver_uid).setValue(videoCallModel);
        FirebaseDatabase.getInstance().getReference("users").child(reciver_uid).child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                receiver_token = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationSender notificationsSender =
                        new FcmNotificationSender(receiver_token,"Incoming Video Call",sender_uid, VideoCallOutgoing.this);

                notificationsSender.sendNotification();

            }
        },1000);
    }
}
