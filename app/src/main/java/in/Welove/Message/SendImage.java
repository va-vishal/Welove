package in.Welove.Message;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import in.Welove.BaseActivity;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.R;


public class SendImage extends BaseActivity {

    private ImageView imageView;
    private Button sendButton;
    private FirebaseUser firebaseUser;
    private Uri fileUri;
    private TextView text;
    private ProgressBar progressBar;
    private String userid;
    private static final String TAG = "SendImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        userid = getIntent().getStringExtra("userid");
        imageView = findViewById(R.id.iv_sendimage);
        sendButton = findViewById(R.id.btn_send);
        text = findViewById(R.id.tv_dont);
        progressBar = findViewById(R.id.pb_sendImage);

        text.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            String uriString = intent.getStringExtra("u");
            if (uriString != null) {
                fileUri = Uri.parse(uriString);
                Glide.with(this).load(fileUri).into(imageView);
            } else {
                Log.e(TAG, "No URI received");
                finish();
            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri != null) {
                    text.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    checkBlockingStatus(firebaseUser.getUid(), userid);
                } else {
                    Toast.makeText(SendImage.this, "No file selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkBlockingStatus(String currentUserId, String otherUserId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        databaseReference.child(otherUserId).child("blockedList").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isBlockedByOtherUser = dataSnapshot.exists();

                        databaseReference.child(currentUserId).child("blockedList").child(otherUserId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean isBlockedByCurrentUser = dataSnapshot.exists();

                                        if (isBlockedByOtherUser && isBlockedByCurrentUser) {
                                            showToast("You cannot send or receive messages from this user");
                                        } else if (isBlockedByOtherUser) {
                                            showToast("You cannot send messages, other user has blocked you");
                                        } else if (isBlockedByCurrentUser) {
                                            showToast("You cannot send messages, you have blocked this user");
                                        } else {
                                            uploadFile("image");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void uploadFile(String type) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(fileUri));

        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        sendMessage(firebaseUser.getUid(), userid, downloadUrl, "image");
                        showToast("Image Sent");
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting download URL", e);
                    showToast("Failed to get download URL");
                });
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Upload failed", e);
            showToast("Upload failed: " + e.getMessage());
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendMessage(String sender, String receiver, String message, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String messageId = reference.child("users").child(sender).child("Chatlist").child(receiver).child("Chats").push().getKey();

        if (messageId != null) {
            long timestamp = System.currentTimeMillis();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("type", type);
            hashMap.put("isseen", false);
            hashMap.put("timestamp", timestamp);
            hashMap.put("date", date);

            reference.child("users").child(sender).child("Chatlist").child(receiver).child("Chats").child(messageId).setValue(hashMap);
            reference.child("users").child(receiver).child("Chatlist").child(sender).child("Chats").child(messageId).setValue(hashMap);

            DatabaseReference chatRef = reference.child("users").child(sender).child("Chatlist").child(receiver);
            chatRef.child("id").setValue(receiver);

            DatabaseReference chatRefReceiver = reference.child("users").child(receiver).child("Chatlist").child(sender);
            chatRefReceiver.child("id").setValue(sender);

            addNotification(receiver, "image", "New Message", "You got a new message");
            sendUserActionNotification(receiver, "message");
        }
    }

    private void sendUserActionNotification(String targetedUserId, String actionType) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // Ensure firebaseUser is initialized
        if (firebaseUser == null) {
            Log.e("Notification", "FirebaseUser is null");
            return;
        }

        String currentUserId = firebaseUser.getUid();

        if (currentUserId == null || targetedUserId == null) {
            Log.e("Notification", "User ID is null");
            return;
        }

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String currentUserName = currentUser.getName();
                String notificationBody = generateNotificationMessage(currentUserName, actionType);

                DatabaseReference targetUserRef = FirebaseDatabase.getInstance().getReference("users").child(targetedUserId);
                targetUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User targetUser = snapshot.getValue(User.class);
                        if (targetUser == null) {
                            Log.e("Notification", "Target user data is null");
                            return;
                        }

                        String userToken = targetUser.getFcmToken(); // Assuming User class has getFcmToken()
                        if (userToken != null) {
                            Log.d("FCM Token", userToken);
                            FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                    userToken,
                                    "Notification",
                                    notificationBody,
                                    SendImage.this // Make sure this context is correct
                            );
                            fcmNotificationSender.sendNotification();

                        } else {
                            Log.e("Notification", "Target user FCM token is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Failed to fetch target user data: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to fetch current user data: " + databaseError.getMessage());
            }
        });
    }


    private String generateNotificationMessage(String userName, String actionType) {
        if ("message".equals(actionType)) {
            return userName + " sent you a message.";
        }
        return userName + " performed an action.";
    }

    private void addNotification(String targetedUserId, String notificationType, String title, String body) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }

        String currentUserId = currentUser.getUid();

        if (targetedUserId == null || targetedUserId.isEmpty()) {
            Log.e("Notification", "Targeted user ID is invalid");
            return;
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String currentUsername = currentUser.getName();
                String personalizedBody;

                switch (notificationType) {
                    case "image":
                        personalizedBody = String.format("%s has sent u a image", currentUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the provided body if no match
                        break;
                }

                DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(targetedUserId)
                        .child("Notifications")
                        .push(); // Generate a unique ID for the notification

                // Create a Notification object and set properties
                Notification notification = new Notification();
                notification.setNotificationId(notificationRef.getKey());
                notification.setTitle(title);
                notification.setBody(personalizedBody);
                notification.setNotificationType(notificationType);
                notification.setTimestamp(System.currentTimeMillis());
                notification.setSeen(false);
                notification.setOtheruserid(targetedUserId);
                notification.setCurrentuserid(currentUserId);

                // Push the notification to Firebase
                notificationRef.setValue(notification).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notification", "Notification recorded successfully");
                    } else {
                        Log.e("Notification", "Failed to record notification", task.getException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to fetch current user details", databaseError.toException());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(SendImage.this, message, Toast.LENGTH_SHORT).show();
    }
}
