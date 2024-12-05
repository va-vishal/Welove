package in.Welove.Message;

import static com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService.endCall;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.Adapter.MessageAdapter;
import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.DeleteMessageWorker;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.Model.Chat;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class messagingActivity extends BaseActivity {

    private static final int PICK_IMAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final String TAG = "VideoCall";
    private final long appID = 405107665;
    private final String appSign = "38a7d30f075a3442d4830d35880ba11d26079c8bfe1aa584cd71c8530fae0193";
    CircleImageView profile_image;
    TextView username, blockedText, lastSeen, online;
    EditText editText;
    ImageView more;
    View img_oon;
    RecyclerView recyclerView_c;
    FirebaseUser currentUser;
    DatabaseReference reference, typingRef, transactionsRef;
    MessageAdapter messageAdapter;
    LinearLayout msgData;
    List<Chat> mchat;
    LottieAnimationView animation_view;
    ValueEventListener seenListener;
    private String sname, rname, sid, receiver_token;
    private boolean isCallActive;
    private String otherUser;
    private Context mContext;
    private ZegoSendCallInvitationButton videocall, audiocall;
    private Double walletBalance;
    private long totalCallDuration = 0;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        // WindowManager.LayoutParams.FLAG_SECURE);
        EmojiManager.install(new GoogleEmojiProvider());

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(messagingActivity.this, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        recyclerView_c = findViewById(R.id.recycler_view_c);
        recyclerView_c.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView_c.setLayoutManager(linearLayoutManager);
        videocall = findViewById(R.id.video);
        audiocall = findViewById(R.id.voice);
        img_oon = findViewById(R.id.img_onn);
        img_oon.setVisibility(View.GONE);
        if (videocall == null) {
            Log.e(TAG, "Video call button is null");
        }
        if (audiocall == null) {
            Log.e(TAG, "Voice call button is null");
        }
        if (videocall != null) {
            videocall.setOnClickListener(v -> {
                isUserEligibleForCall(isEligible -> {
                    if (isEligible) {
                        initiateVideoCall(); // Initiate video call if the user is eligible
                    } else {
                        showPremiumUpgradePrompt(); // Show prompt to upgrade if not eligible
                    }
                });
            });
        }

        if (audiocall != null) {
            audiocall.setOnClickListener(v -> {
                isUserEligibleForCall(isEligible -> {
                    if (isEligible) {
                        initiateVoiceCall(); // Initiate voice call if the user is eligible
                    } else {
                        showPremiumUpgradePrompt(); // Show prompt to upgrade if not eligible
                    }
                });
            });
        }


        online = findViewById(R.id.online);
        lastSeen = findViewById(R.id.lastSeen);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        animation_view = findViewById(R.id.animation_view);
        animation_view.setVisibility(View.GONE);
        editText = findViewById(R.id.send_msg);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setVisibility(View.VISIBLE);
        more = findViewById(R.id.more);
        blockedText = findViewById(R.id.blockedText);
        msgData = findViewById(R.id.msgData);
        recyclerView_c.setVisibility(View.VISIBLE);
        msgData.setVisibility(View.VISIBLE);
        more.setVisibility(View.VISIBLE);
        blockedText.setVisibility(View.GONE);
        otherUser = getIntent().getStringExtra("userid");
        if (otherUser == null) {
            Log.e("messagingActivity", "UserID is null");
            return;
        } else {
            Log.d("messagingActivity", "UserID: " + otherUser);
        }
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(otherUser);
        typingRef = FirebaseDatabase.getInstance().getReference("typing");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users").child(otherUser);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    rname = user.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(messagingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
        animation_view.setAnimation("animation.json");
        setupSendMessageButton(otherUser);
        loadUserData(otherUser);
        initCallInviteService(currentUser.getUid(), otherUser);
        setupTypingStatus(editText, otherUser, typingRef);
        checkOnlineStatus(otherUser);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserBlocked(currentUser.getUid(), otherUser, new BlockCheckCallback() {
                    @Override
                    public void onResult(boolean isBlocked) {
                        PopupMenu popupMenu = new PopupMenu(mContext, view);
                        popupMenu.inflate(R.menu.chat_menu);
                        popupMenu.getMenu().findItem(R.id.block).setVisible(!isBlocked);
                        popupMenu.getMenu().findItem(R.id.unblock).setVisible(isBlocked);
                        popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == R.id.block) {
                                addToBlockedList(otherUser);
                                Toast.makeText(messagingActivity.this, "You blocked this user", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (itemId == R.id.unblock) {
                                removeFromBlockedList(otherUser);
                                Toast.makeText(messagingActivity.this, "You unblocked this user", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (itemId == R.id.Report) {
                                Toast.makeText(messagingActivity.this, "Report Clicked", Toast.LENGTH_SHORT).show();
                                sendEmailToAdmin("Reporting User", "Please provide details about the report here .Why you want repor this user.mention user's Username and Dob.");

                                return true;
                            } else {
                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });
            }
        });
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                    }
                });

    }

    private void sendEmailToAdmin(String subject, String body) {
        String adminEmail = "vishnugana.prajeeth@gmail.com";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{adminEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setPackage("com.google.android.gm");

        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Gmail app is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void isUserEligibleForCall(PremiumCheckCallback callback) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isPremiumPlus = false;
                boolean isHot = false;

                if (dataSnapshot.hasChild("pp")) {
                    Boolean premiumPlusValue = dataSnapshot.child("pp").getValue(Boolean.class);
                    if (premiumPlusValue != null) {
                        isPremiumPlus = premiumPlusValue;
                    }
                }
                if (dataSnapshot.hasChild("hot")) {
                    Boolean hotValue = dataSnapshot.child("hot").getValue(Boolean.class);
                    if (hotValue != null) {
                        isHot = hotValue;
                    }
                }

                boolean isEligibleForCall = isPremiumPlus || isHot;

                callback.onPremiumCheckComplete(isEligibleForCall);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("isUserEligibleForCall", "Database error: " + databaseError.getMessage());
                callback.onPremiumCheckComplete(false); // Assume not eligible on error
            }
        });
    }


    private void showPremiumUpgradePrompt() {
        CustomDialog customDialog = new CustomDialog(messagingActivity.this);
        customDialog.show(
                "Upgrade to Premium Plus",
                "This feature is available only for Premium Plus users. Would you like to upgrade?",
                v -> {
                    Intent intent = new Intent(messagingActivity.this, SubscriptionActivity.class);
                    startActivity(intent);
                    customDialog.dismiss();
                },
                false
        );
        customDialog.setNegativeButton("Cancel", v -> customDialog.dismiss());

    }

    private void initCallInviteService(String currentUserId, String otherUserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("users").child(currentUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        sname = user.getName();
                        sid = currentUserId;
                        Double balance = user.getWalletBalance();
                        walletBalance = (balance != null) ? balance : 0.0;

                        setupCallInvitation();
                    } else {
                        Log.e("CallActivity", "Current user data does not exist");
                    }
                } else {
                    Log.e("CallActivity", "Current user data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CallActivity", "Error loading current user data: " + databaseError.getMessage());
            }
        });
    }

    private void setupCallInvitation() {


        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        callInvitationConfig.provider = invitationData -> {
            ZegoUIKitPrebuiltCallConfig config;
            boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
            ZegoUIKitUser inviter = invitationData.inviter;
            boolean isCurrentUserCaller = inviter.userID.equals(currentUser.getUid());

            Log.d("CallActivity", "Current User ID: " + currentUser.getUid());
            Log.d("CallActivity", "Inviter User ID: " + inviter.userID);
            Log.d("CallActivity", "Is Current User Caller: " + isCurrentUserCaller);

            // Configure the call
            config = isVideoCall ? ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
                    : ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();

            // Customize the UI and Ringtone settings
            callInvitationConfig.outgoingCallBackground = new ColorDrawable(Color.BLUE);
            callInvitationConfig.incomingCallBackground = new ColorDrawable(Color.GREEN);
            callInvitationConfig.incomingCallRingtone = "incomingCallRingtone";
            callInvitationConfig.outgoingCallRingtone = "outgoingCallRingtone";

            config.topMenuBarConfig.isVisible = true;
            config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);

            config.durationConfig = new ZegoCallDurationConfig();
            config.durationConfig.isVisible = true;

            // Track call duration and update wallet balances
            config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                long previousSecond = 0;

                @Override
                public void onDurationUpdate(long seconds) {
                    Log.d("CallActivity", "Duration: " + seconds + " seconds");

                    totalCallDuration = seconds;
                    if (seconds > previousSecond && seconds % 60 == 0) {
                        double deductAmount = isVideoCall ? 6.0 : 3.0;
                        double addAmount = isVideoCall ? 2.5 : 1.0;
                        if (isCurrentUserCaller) {
                            updateWalletBalances(currentUser.getUid(), deductAmount, true, totalCallDuration);
                            updateWalletBalances(otherUser, addAmount, false, totalCallDuration);
                        }
                        previousSecond = seconds;
                    }
                    if (seconds >= 60 * 5) {
                        Log.d("CallActivity", "Call reached 5 minutes. Ending call.");
                        endCall();
                    }
                }
            };

            return config;
        };

        // Initialize the call service
        ZegoUIKitPrebuiltCallService.init(
                getApplication(),
                appID,
                appSign,
                sid,
                sname,
                callInvitationConfig
        );
    }

    private void updateWalletBalances(String userId, double amount, boolean isDeduction, long totalCallDuration) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        if (isDeduction) {
            // Deduct from user's balance
            userRef.child("walletBalance").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Double currentBalance = mutableData.getValue(Double.class);
                    if (currentBalance == null) {
                        Log.d("CallActivity", "User balance is null, no deduction made.");
                        return Transaction.success(mutableData);
                    }

                    if (currentBalance >= amount) {
                        mutableData.setValue(currentBalance - amount);
                        addTransaction(totalCallDuration, userId, amount, "debit");
                    } else {
                        Log.d("CallActivity", "Insufficient balance for user.");
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                    if (committed) {
                        Log.d("CallActivity", "User balance deducted successfully.");
                    } else {
                        Log.e("CallActivity", "Error updating user's balance: " + databaseError.getMessage());
                    }
                }
            });
        } else {
            DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef1.child("walletBalance").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Double currentBalance = mutableData.getValue(Double.class);
                    if (currentBalance == null) {
                        currentBalance = 0.0; // Start with 0 if null
                    }
                    mutableData.setValue(currentBalance + amount);
                    addTransaction(totalCallDuration, userId, amount, "credit");
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                    if (committed) {
                        Log.d("CallActivity", "User balance updated successfully.");
                    } else {
                        Log.e("CallActivity", "Error updating user's balance: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    private void addTransaction(long totalCallDuration, String userid, double amount, String type) {
        long timestamp = System.currentTimeMillis();
        long minutes = totalCallDuration / 60;
        double amountToDeduct = minutes * amount;

        // Create a new Transaction object and set its properties
        in.Welove.Model.Transaction transaction = new in.Welove.Model.Transaction();
        transaction.setAmount(amountToDeduct); // Set the amount to deduct or add
        transaction.setType(type); // Set the transaction type (e.g., "debit" or "credit")
        transaction.setTimestamp(timestamp); // Set the timestamp
        transaction.setDescription("Call transaction for duration: " + totalCallDuration + " seconds"); // Optional description

        // Get reference to the transactions node in Firebase
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userid)
                .child("transactions");

        // Push the transaction to Firebase
        transactionsRef.push().setValue(transaction)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Transaction", "Transaction added successfully.");
                    } else {
                        Log.e("Transaction", "Failed to add transaction: " + task.getException());
                    }
                });
    }

    private void showRechargeDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.show(
                "Low Balance",
                "Your balance is too low to continue the call. Please recharge.",
                v -> {
                    // Handle recharge action (navigate to recharge screen)
                    customDialog.dismiss();
                },
                false
        );

        customDialog.setNegativeButton("Cancel", v -> customDialog.dismiss());
    }


    private void initiateVideoCall() {
        if (walletBalance < 10) {
            showRechargeDialog();
            return;
        }
        videocall.setIsVideoCall(true);
        videocall.setResourceID("Welove_ResId");  // Ensure this matches the Zego configuration.
        videocall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUser, rname)));
        addNotification(otherUser, "videocall", "Incoming Video Call", "You have a new video call request.");
        sendUserActionNotification(otherUser, "videocall");
        Log.d("VideoCall", "Video call initiated with: " + otherUser + " (" + rname + ")");
    }

    private void initiateVoiceCall() {
        if (walletBalance < 10) {
            showRechargeDialog();
            return;
        }
        audiocall.setIsVideoCall(false);
        audiocall.setResourceID("Welove_ResId");  // Ensure this matches the Zego configuration.
        audiocall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUser, rname)));
        addNotification(otherUser, "audiocall", "Incoming Voice Call", "You have a new voice call request.");
        sendUserActionNotification(otherUser, "audiocall");
        Log.d("VoiceCall", "Voice call initiated with: " + otherUser + " (" + rname + ")");
    }

    private void isUserBlocked(String currentUserId, String otherUserId, BlockCheckCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(currentUserId).child("blocksList").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onResult(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onResult(false); // In case of error, consider user not blocked
            }
        });
    }

    private void addToBlockedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        databaseReference.child(currentUserId).child("blocksList").child(userId).setValue(true);

        // Add the currentUserId to the other user's blockedList
        databaseReference.child(userId).child("blockedList").child(currentUserId).setValue(true);
    }

    private void removeFromBlockedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        // Remove the userId from the current user's blockedList
        databaseReference.child(currentUserId).child("blocksList").child(userId).removeValue();
        // Remove the currentUserId from the other user's blockedList
        databaseReference.child(userId).child("blockedList").child(currentUserId).removeValue();
    }

    private void loadUserData(final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(user.getName());
                    boolean status = user.getStatus();
                    if (status == true) {
                        img_oon.setVisibility(View.VISIBLE);
                    } else {
                        img_oon.setVisibility(View.GONE);
                    }
                    if (!isFinishing() && !isDestroyed()) {
                        if ("default".equals(user.getImageurl())) {
                            profile_image.setImageResource(R.drawable.defaultimage);
                        } else {
                            Glide.with(messagingActivity.this).load(user.getImageurl()).into(profile_image);
                        }
                    }
                    checkBlockingStatus(currentUser.getUid(), userid);
                    readMessages(currentUser.getUid(), userid, user.getImageurl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(messagingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBlockingStatus(String currentUserId, String otherUserId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(otherUserId).child("blockedList").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isBlockedByOtherUser = dataSnapshot.exists();
                databaseReference.child(currentUserId).child("blockedList").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isBlockedByCurrentUser = dataSnapshot.exists();

                        if (isBlockedByOtherUser && isBlockedByCurrentUser) {
                            updateUIForBlockedState(true, true);
                        } else if (isBlockedByOtherUser) {
                            updateUIForBlockedState(true, false);
                        } else updateUIForBlockedState(false, isBlockedByCurrentUser);
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

    private void updateUIForBlockedState(boolean isBlockedByOtherUser, boolean isBlockedByCurrentUser) {
        if (isBlockedByOtherUser && isBlockedByCurrentUser) {
            // Both users have blocked each other
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You both have blocked each other. Messaging is disabled.");
            editText.setEnabled(false);
        } else if (isBlockedByOtherUser) {
            // The current user is blocked by the other user
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You have blocked this user. You can't send or receive messages.");
            editText.setEnabled(false);
        } else if (isBlockedByCurrentUser) {
            // The current user has blocked the other user
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You are blocked by the other user. They can't send or receive messages from you.");
            editText.setEnabled(false);
        } else {
            // No one has blocked anyone
            recyclerView_c.setVisibility(View.VISIBLE);
            msgData.setVisibility(View.VISIBLE);
            videocall.setVisibility(View.VISIBLE);
            audiocall.setVisibility(View.VISIBLE);
            blockedText.setVisibility(View.GONE);
            editText.setEnabled(true);
        }
    }

    private void setupSendMessageButton(final String userid) {
        final EditText editText = findViewById(R.id.send_msg);

        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check for clicks on the drawableRight
                int drawableRightWidth = editText.getCompoundDrawables()[2] != null ? editText.getCompoundDrawables()[2].getBounds().width() : 0;
                if (event.getX() >= (editText.getWidth() - drawableRightWidth - editText.getPaddingRight())) {
                    // Handle the send button click
                    String msg = editText.getText().toString().trim();
                    if (!msg.equals("")) {
                        sendMessage(currentUser.getUid(), userid, msg, "text");
                    } else {
                        Toast.makeText(messagingActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                    }
                    editText.setText("");
                    typingRef.child(currentUser.getUid()).child(userid).removeValue();
                    return true;
                }

                // Check for clicks on the drawableLeft
                int drawableLeftWidth = editText.getCompoundDrawables()[0] != null ? editText.getCompoundDrawables()[0].getBounds().width() : 0;
                if (event.getX() <= (drawableLeftWidth + editText.getPaddingLeft())) {

                    // Inflate the custom popup layout
                    LayoutInflater inflater = getLayoutInflater();
                    View popupView = inflater.inflate(R.layout.dialog_select_option, null);

                    // Create the PopupWindow
                    PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            true);

                    // Set click listeners for the buttons in the popup
                    ImageView cameraOption = popupView.findViewById(R.id.camera);
                    ImageView galleryOption = popupView.findViewById(R.id.gallerys);
                    ImageView emojiOption = popupView.findViewById(R.id.emoji);

                    cameraOption.setOnClickListener(view -> {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
                        popupWindow.dismiss();
                    });

                    galleryOption.setOnClickListener(view -> {
                        // Handle gallery option click
                        openGallery();
                        popupWindow.dismiss();
                    });
                    emojiOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = findViewById(R.id.send_msg);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                            View rootView = findViewById(R.id.root_view); // root_view is the parent view of your layout
                            //editText.requestFocus();
                            EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                                    .build(editText); // Attach it to your EditText
                            emojiPopup.toggle();

                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                        }
                    });

                    // Show the popup window
                    popupWindow.setElevation(10);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    msgData.getLocationOnScreen(location);

                    // Adjust the yOffset to move the popup slightly higher
                    int yOffset = -(msgData.getHeight() + popupWindow.getHeight() + 180); // The 20dp additional offset

                    // Show PopupWindow above msgDataLayout with additional offset
                    popupWindow.showAsDropDown(msgData, 30, yOffset);


                    View container = popupWindow.getContentView().getRootView();
                    if (container != null) {
                        container.setBackgroundResource(android.R.color.transparent);
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                case REQUEST_IMAGE_CAPTURE:
                    handleImageResult(requestCode, data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    handleCroppedImageResult(data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                    Exception error = CropImage.getActivityResult(data).getError();
                    Toast.makeText(this, "Crop error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handleCroppedImageResult(@Nullable Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (result != null) {
            Uri croppedImageUri = result.getUri();
            Intent intent = new Intent(this, SendImage.class);
            intent.putExtra("u", croppedImageUri.toString());
            intent.putExtra("userid", otherUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to get cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageResult(int requestCode, @Nullable Intent data) {
        Uri fileUri = null;

        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            fileUri = data.getData();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                fileUri = getImageUri(this, imageBitmap);
            }
        }

        if (fileUri != null) {
            CropImage.activity(fileUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    // Load Lottie animation from raw resources
    private void setupTypingStatus(EditText editText, String userid, DatabaseReference typingRef) {
        // Get the original bottom margin of the RecyclerView
        final int originalMarginBottom = ((ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams()).bottomMargin;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    typingRef.child(currentUser.getUid()).child(userid).removeValue();
                } else {
                    typingRef.child(currentUser.getUid()).child(userid).setValue(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        typingRef.child(userid).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class))) {
                    animation_view.setVisibility(View.VISIBLE);
                    animation_view.playAnimation();


                    animation_view.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            // Adjust RecyclerView margin
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams();
                            params.bottomMargin = (int) (45 * getResources().getDisplayMetrics().density); // 45dp in pixels
                            recyclerView_c.setLayoutParams(params);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Restore original margin
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams();
                            params.bottomMargin = originalMarginBottom; // Restore original margin
                            recyclerView_c.setLayoutParams(params);

                            // Scroll to the last item after animation ends
                            recyclerView_c.smoothScrollToPosition(mchat.size() - 1);
                        }
                    });

                    online.setVisibility(View.GONE);
                    lastSeen.setVisibility(View.GONE);
                } else {
                    animation_view.setVisibility(View.GONE);
                    animation_view.cancelAnimation();
                    online.setVisibility(View.VISIBLE);
                    lastSeen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error retrieving typing status", databaseError.toException());
            }
        });
    }

    private void checkOnlineStatus(String userId) {
        DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Assuming status is a Boolean, if not adjust comparison
                    Boolean status = user.getStatus();
                    if (status != null && status.equals(true)) {
                        online.setVisibility(View.VISIBLE);
                        lastSeen.setVisibility(View.GONE);
                    } else {
                        online.setVisibility(View.GONE);
                        lastSeen.setVisibility(View.VISIBLE);
                        DatabaseReference userlastSeenRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userlastSeenRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    // Assuming status is a Boolean, if not adjust comparison
                                    String lastseen = user.getLastSeen();
                                    lastSeen.setText("Last Seen " + lastseen);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("FirebaseError", databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String type) {
        checkBlockingStatus(currentUser.getUid(), otherUser);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // Generate a unique ID for the message
        String messageId = reference.child("users").child(sender).child("Chatlist").child(receiver).child("Chats").push().getKey();

        if (messageId != null) {
            long timestamp = System.currentTimeMillis();

            // Format the timestamp into a date string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date(timestamp));

            // Create the message object
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


            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("users").child(sender).child("Chatlist").child(receiver);
            chatRef.child("id").setValue(receiver);

            DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("users").child(receiver).child("Chatlist").child(sender);
            chatRefReceiver.child("id").setValue(sender);

            if (type.equals("text")) {
                message = editText.getText().toString();
            } else {
                message = "image";
            }
            checkReceiverStatusAndSendNotifications(receiver, message);
        }
    }

    private void checkReceiverStatusAndSendNotifications(String receiverId, String message) {
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("users").child(receiverId);
        receiverRef.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the status exists in the database
                if (dataSnapshot.exists()) {
                    Boolean status = dataSnapshot.getValue(Boolean.class);

                    // If the receiver is offline, send notifications
                    if (status == null || !status.equals("online")) {
                        addNotification(receiverId, "message", "New Message", "You got a new message: " + message);
                        sendUserActionNotification(receiverId, "message");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to check receiver status: " + databaseError.getMessage());
            }
        }); // Ensure this closing parenthesis is present
    }

    private void readMessages(final String myId, final String userId, final String imageUrl) {
        mchat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(myId)
                .child("Chatlist")
                .child(userId)
                .child("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(myId)))) {
                        mchat.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(messagingActivity.this, mchat, imageUrl);
                recyclerView_c.setAdapter(messageAdapter);
                if (mchat.size() > 0) {
                    recyclerView_c.scrollToPosition(mchat.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential database errors
                Log.e("Firebase", "Failed to read messages: " + databaseError.getMessage());
            }
        });
    }

    private void sendUserActionNotification(String targetedUserId, String actionType) {
        if (targetedUserId == null) {
            Log.e("Notification", "Targeted user ID is null");
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference targetedRef = FirebaseDatabase.getInstance().getReference("users").child(targetedUserId);
        targetedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User targetedUser = dataSnapshot.getValue(User.class);
                if (targetedUser == null) {
                    Log.e("Notification", "Targeted user data is null");
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

                        String userToken = targetedUser.getFcmToken();
                        if (userToken != null) {
                            Log.e("FCM Token", userToken);
                            FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                    userToken,
                                    "Notification",
                                    notificationBody,
                                    messagingActivity.this // Ensure `context` is correctly initialized
                            );
                            fcmNotificationSender.sendNotification();
                        } else {
                            Log.e("Notification", "Target user FCM token is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Failed to retrieve current user data", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to retrieve targeted user data", databaseError.toException());
            }
        });
    }

    private String generateNotificationMessage(String userName, String actionType) {
        switch (actionType) {
            case "message":
                return "You have a new message from " + userName;
            case "audiocall":
                return "You have an incoming audio call from " + userName;
            case "videocall":
                return "You have a new video call from " + userName;
            default:
                return "You have a new notification from " + userName;
        }
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
                    case "message":
                        personalizedBody = String.format("You have a new message from %s", currentUsername);
                        break;
                    case "audiocall":
                        personalizedBody = String.format("You have an incoming audio call from %s", currentUsername);
                        break;
                    case "videocall":
                        personalizedBody = String.format("You have an incoming video call from %s", currentUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the provided body if no match
                        break;
                }

                DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(targetedUserId)
                        .child("Notifications")
                        .push();

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

    private void markMessagesAsSeen(String receiverId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Reference for the current user's chat with the receiver
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("Chatlist")
                .child(receiverId)
                .child("Chats");

        // Mark messages as seen for the current user
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && chat.getReceiver().equals(currentUser.getUid()) && !chat.isIsseen()) {
                        // Mark message as seen
                        snapshot.getRef().child("isseen").setValue(true);
                        Log.e("markMessagesAsSeen", "Marking messages as seen for current user.");

                        // Schedule deletion after 2 minutes using WorkManager
                        scheduleMessageDeletion(snapshot.getKey(), receiverId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("markMessagesAsSeen", "Error marking messages as seen: " + error.getMessage());
            }
        });

        // Reference for the receiver's chat with the current user
        DatabaseReference reference1 = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(receiverId)
                .child("Chatlist")
                .child(currentUser.getUid())
                .child("Chats");

        // Mark messages as seen for the receiver (other user)
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && chat.getSender().equals(receiverId) && !chat.isIsseen()) {
                        // Mark message as seen
                        snapshot.getRef().child("isseen").setValue(true);
                        Log.e("markMessagesAsSeen", "Marking messages as seen for other user.");

                        // Schedule deletion after 2 minutes using WorkManager
                        scheduleMessageDeletion(snapshot.getKey(), currentUser.getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("markMessagesAsSeen", "Error marking messages as seen: " + error.getMessage());
            }
        });
    }

    private void scheduleMessageDeletion(String messageId, String receiverId) {
        Data inputData = new Data.Builder()
                .putString(DeleteMessageWorker.MESSAGE_ID, messageId)
                .putString(DeleteMessageWorker.RECEIVER_ID, receiverId)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DeleteMessageWorker.class)
                .setInitialDelay(24, TimeUnit.HOURS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        markMessagesAsSeen(otherUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public interface BlockCheckCallback {
        void onResult(boolean isBlocked);
    }
    public interface PremiumCheckCallback {
        void onPremiumCheckComplete(boolean isEligibleForCall);
    }

}