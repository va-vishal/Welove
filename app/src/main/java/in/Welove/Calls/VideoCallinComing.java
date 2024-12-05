package in.Welove.Calls;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
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
import in.Welove.Message.messagingActivity;
import in.Welove.Model.VcModel;
import in.Welove.R;


public class VideoCallinComing extends BaseActivity {

    private DatabaseReference referencecaller, referenceVc, vcref;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String sender_uid, receiver_uid;
    private VcModel model;
    private ImageView imageView;
    private FloatingActionButton declinebtn, acceptbtn;
    private TextView tvname;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_incoming);

        imageView = findViewById(R.id.ic_vc_image);
        tvname = findViewById(R.id.ic_vc_name);
        declinebtn = findViewById(R.id.ic_vc_decline);
        acceptbtn = findViewById(R.id.ic_vc_accept);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        receiver_uid = user.getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sender_uid = bundle.getString("sender_id");
        } else {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        model = new VcModel();
        referencecaller = FirebaseDatabase.getInstance().getReference("users").child(sender_uid);
        referenceVc = database.getReference("vcref").child(sender_uid).child(receiver_uid);
        vcref = FirebaseDatabase.getInstance().getReference("vc");

        // Load caller information
        referencecaller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String senderName = snapshot.child("name").getValue(String.class);
                    String senderUrl = snapshot.child("imageurl").getValue(String.class);

                    if (senderName != null) {
                        tvname.setText(senderName);
                    }

                    if (senderUrl != null) {
                        Picasso.get().load(senderUrl).into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.defaultimage);
                    }
                } else {
                    Toast.makeText(VideoCallinComing.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoCallinComing.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Play ringtone
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mp = MediaPlayer.create(getApplicationContext(), notification);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Button click listeners
        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = "yes";
                sendResponse(response);
                mp.stop();
                vcref.removeValue();
                referenceVc.removeValue();
            }
        });

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = "no";
                sendResponse(response);
                Intent intent = new Intent(VideoCallinComing.this, messagingActivity.class);
                startActivity(intent);
                vcref.removeValue();
                referenceVc.removeValue();
                mp.stop();
                finish();
            }
        });

        // Check for call cancellation
        checkCallStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private void checkCallStatus() {
        DatabaseReference cancelRef = database.getReference("cancel");
        cancelRef.child(sender_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String response = snapshot.child("response").getValue(String.class);
                    if ("no".equals(response)) {
                        Intent intent = new Intent(VideoCallinComing.this, messagingActivity.class);
                        startActivity(intent);
                        mp.stop();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendResponse(String response) {
        if (response.equals("yes")) {
            model.setKey(sender_uid + receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
            //joinMeeting();
        } else if (response.equals("no")) {
            model.setKey(sender_uid + receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
        }

        // Remove the reference after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                referenceVc.removeValue();
            }
        }, 3000);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String response = "no";
        sendResponse(response);
        Intent intent = new Intent(VideoCallinComing.this, messagingActivity.class);
        startActivity(intent);
        vcref.removeValue();
        finish();
    }
}
