package in.Welove;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.Authentication.LoginActivity1;
import in.Welove.Fragments.HomeFragment;
import in.Welove.Fragments.MatchedFragment;
import in.Welove.Fragments.NotificationFragment;
import in.Welove.Fragments.ProfileFragment;
import in.Welove.Message.MessageActivity;
import in.Welove.Model.User;
import in.Welove.Profile.PreferenceActivity;
import in.Welove.Profile.ProfileActivity;


public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private CircleImageView profileimage;
    private FirebaseUser currentUser;
    private ImageView preferences,searchoption;
    private ImageButton message;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, initializationStatus -> {});
        loadBannerAd();
        FirebaseApp.initializeApp(this);
        checkPremiumStatus();
        profileimage = findViewById(R.id.profileimage);
        preferences = findViewById(R.id.preferences);
        message = findViewById(R.id.messages);
        searchoption=findViewById(R.id.search_option);

        preferences.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PreferenceActivity.class);
            startActivity(intent);
        });
        message.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
            startActivity(intent);
        });
        profileimage.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        searchoption.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "User is null, redirecting to LoginActivity");
            Intent intent = new Intent(HomeActivity.this, LoginActivity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        countUnseenNotifications();
        countUnreadMessages();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        if (NetworkUtil.isNetworkAvailable(this)) {
            loadUserProfile();
        } else {showNoInternetDialog();
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (getIntent().getExtras() != null && getIntent().hasExtra("publisherid")) {
            String publisher = getIntent().getStringExtra("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            Log.d(TAG, "Navigating to ProfileFragment with publisher ID: " + publisher);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Set profile as selected in nav
        } else {
            Log.d(TAG, "Navigating to HomeFragment as the default");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set home as the selected item by default
        }

        FirebaseMessaging.getInstance().subscribeToTopic("premiumUsers")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully subscribed to topic premiumUsers");
                    }
                });
        getFCMToken();
        initializeHotSubscription();
    }


    private void initializeHotSubscription() {
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean hasHotProfile = dataSnapshot.hasChild("hotProfile");
                    boolean hasHot = dataSnapshot.hasChild("hot");
                    boolean hasHotStartDate = dataSnapshot.hasChild("hotStartDate");
                    boolean hasHotEndDate = dataSnapshot.hasChild("hotEndDate");

                    if (!hasHot || !hasHotStartDate || !hasHotEndDate||!hasHotProfile) {
                        Map<String, Object> hotDetails = new HashMap<>();

                        if (!hasHot) {
                            hotDetails.put("hot", false);
                        }
                        if (!hasHotProfile) {
                            hotDetails.put("hotProfile", false);
                        }
                        if (!hasHotStartDate) {
                            hotDetails.put("hotStartDate", "");
                        }
                        if (!hasHotEndDate) {
                            hotDetails.put("hotEndDate", "");
                        }

                        userRef.updateChildren(hotDetails)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("Firebase", "Successfully updated missing hot subscription fields for user.");
                                    } else {
                                        Log.e("Firebase", "Failed to update missing hot subscription fields.");
                                    }
                                });
                    } else {
                        // If all fields already exist, no update is needed
                        Log.d("Firebase", "User already has hot subscription details.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Error fetching user data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    saveTokenToDatabase(token);
                });
    }
    private void saveTokenToDatabase(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            databaseReference.child("fcmToken").setValue(token);
        }
    }
    private void loadBannerAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

       if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_matched) {
            selectedFragment = new MatchedFragment();
        } else if (item.getItemId() == R.id.nav_noti) {
            selectedFragment = new NotificationFragment();
        } else if (item.getItemId() == R.id.nav_profile) {
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.apply();
            selectedFragment = new ProfileFragment();
        } else {
            return false;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            return true;
        }
        return false;
    };
    private void loadUserProfile() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.e(TAG, "User details fetched ");
                        Picasso.get().load(user.getImageurl()).into(profileimage);
                    } else {
                        Log.e(TAG, "User object is null");
                    }
                } else {
                    Log.e(TAG, "No data available for the user");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "DatabaseError: " + databaseError.getMessage());
            }
        });
    }
    private void countUnseenNotifications() {
        String userId = currentUser.getUid();
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("Notifications");
        notificationsRef.orderByChild("seen").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unseenCount = (int) dataSnapshot.getChildrenCount();
                if (unseenCount > 0) {
                    BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_noti);
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(unseenCount);
                } else {

                    bottomNavigationView.removeBadge(R.id.nav_noti);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching notifications: " + databaseError.getMessage());
            }
        });
    }
    private void countUnreadMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("Chatlist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadCount = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot chatSnapshot : userSnapshot.child("Chats").getChildren()) {
                        String receiverId = chatSnapshot.child("receiver").getValue(String.class);
                        if (receiverId != null && receiverId.equals(currentUser.getUid())) {
                            Boolean isSeen = chatSnapshot.child("isseen").getValue(Boolean.class);
                            if (isSeen != null && !isSeen) {
                                unreadCount++;
                            }
                        }
                    }
                }
                TextView badge = findViewById(R.id.message_badge);
                if (unreadCount > 0) {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(unreadCount));
                } else {
                    badge.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching chatlist: " + databaseError.getMessage());
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        countUnreadMessages();
    }
    private void checkPremiumStatus() {
        if (currentUser == null) {
            Log.e("UserError", "Current user is null");
            return;
        }
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String premiumEndDate = dataSnapshot.child("premiumEndDate").getValue(String.class);
                    String hotEndDate= dataSnapshot.child("hotEndDate").getValue(String.class);
                    if (premiumEndDate != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date currentDate = new Date();
                        try {
                            Date endDate = dateFormat.parse(premiumEndDate);
                            if (endDate != null && currentDate.after(endDate)) {
                                endPremiumUserSubscription();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (hotEndDate != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date currentDate = new Date();
                        try {
                            Date endDate = dateFormat.parse(hotEndDate);
                            if (endDate != null && currentDate.after(endDate)) {
                                endHotUserSubscription();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Error checking premium status: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("DatabaseError", "User reference is null");
        }
    }
    private void endHotUserSubscription() {
        Map<String, Object> hotDetails = new HashMap<>();
        hotDetails.put("hotProfile", false);
        hotDetails.put("hot", false); // Set hot to false
        hotDetails.put("hotStartDate", "");
        hotDetails.put("hotEndDate", "");
        userRef.updateChildren(hotDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(HomeActivity.this, "Your Hot subscription has ended.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "Failed to update Hot subscription status. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void endPremiumUserSubscription() {
        Map<String, Object> premiumDetails = new HashMap<>();
        premiumDetails.put("p", false);
        premiumDetails.put("pp", false);
        premiumDetails.put("premiumStartDate", "");
        premiumDetails.put("premiumEndDate", "");
        userRef.updateChildren(premiumDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(HomeActivity.this, "Your premium subscription has ended.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(HomeActivity.this, "Failed to update subscription status. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showNoInternetDialog() {
        customDialog = new CustomDialog(this);
        customDialog.show(
                "No Internet Connection",
                "Please check your internet connectivity and try again.",
                v -> customDialog.dismiss(),
                true // Hide the cancel button
        );
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            CustomDialog customDialog = new CustomDialog(this);
            customDialog.show(
                    "Exit Confirmation",
                    "Are you sure you want to exit the app?",
                    v -> {
                        finish(); // Close the activity
                    },
                    false // Do not hide the cancel button
            );
            customDialog.setNegativeButton("Cancel", v -> customDialog.dismiss());

        }
        super.onBackPressed();
    }
}