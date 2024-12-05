package in.Welove.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import in.Welove.Adapter.PreferredGenderCallback;
import in.Welove.Adapter.UserAdapter;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.HomeActivity;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.R;

public class HomeFragment extends BaseFragment {

    private RecyclerView swipeView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private ProgressBar progressBar;
    private Drawable likeIcon;
    private Drawable dislikeIcon;
    private Integer ageRange,minage=1;
    private TextView noUsersTextView;
    //private Double maxDistance;
    private Double latitudeC,longitudeC,latitudeO,longitudeO;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        noUsersTextView=view.findViewById(R.id.noUsersTextView);

        swipeView = view.findViewById(R.id.swipe_view);
        progressBar = view.findViewById(R.id.progress_circular);
        swipeView.setHasFixedSize(true);
        swipeView.setLayoutManager(new OneItemLinearLayoutManager(requireContext()));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(), mUsers);
        swipeView.setAdapter(userAdapter);

        likeIcon = getResources().getDrawable(R.drawable.liked);
        dislikeIcon = getResources().getDrawable(R.drawable.disliked);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ageRange = dataSnapshot.child("ageRange").getValue(Integer.class);
                    //maxDistance = Double.valueOf(dataSnapshot.child("maxDistance").getValue(Integer.class));
                    latitudeC=dataSnapshot.child("latitude").getValue(Double.class);
                    longitudeC=dataSnapshot.child("longitude").getValue(Double.class);

                } else {
                    Log.d("UserPreferences", "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserPreferences", "Database error: " + databaseError.getMessage());
            }
        });

        setUpItemTouchHelper();

        readUsers();

        return view;
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        getCurrentUserPreferredGender(new PreferredGenderCallback() {
            @Override
            public void onPreferredGenderRetrieved(String preferredGender) {
                DatabaseReference currentUserRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot currentUserSnapshot) {
                        User currentUser = currentUserSnapshot.getValue(User.class);
                        boolean isCurrentUserHotProfile = currentUser != null && currentUser.getHotProfile() != null && currentUser.getHotProfile();
                        boolean hotProfileOrNormalProfile = currentUser != null && currentUser.getHotProfieOrNormalProfile() != null && currentUser.getHotProfieOrNormalProfile();

                        Query query;
                        if (preferredGender != null) {
                            query = reference.orderByChild("gender").equalTo(preferredGender);
                        } else {
                            query = reference;
                        }

                        query.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mUsers.clear();
                                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                fetchLikedUsers(currentUserId, likedUsers -> {
                                    fetchDislikedUsers(currentUserId, dislikedUsers -> {
                                        fetchMatchedUsers(currentUserId, matchedUsers -> {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                User user = snapshot.getValue(User.class);
                                                boolean isProfileHidden = user != null && (user.getHideProfile() != null ? user.getHideProfile() : false);

                                                if (user != null && !isProfileHidden) {
                                                    String otherUserId = user.getId();
                                                    boolean isOtherUserHotProfile = user.getHotProfile() != null && user.getHotProfile();

                                                    if (!likedUsers.contains(otherUserId) &&
                                                            !dislikedUsers.contains(otherUserId) &&
                                                            !matchedUsers.contains(otherUserId)) {
                                                        Integer otherUserAge = Integer.valueOf(user.getAge());
                                                        longitudeO = user.getLongitude();
                                                        latitudeO = user.getLatitude();

                                                        // Filtering logic based on hotProfile preference
                                                        if (isCurrentUserHotProfile && hotProfileOrNormalProfile) {
                                                            if (isOtherUserHotProfile && otherUserAge >= 18 && otherUserAge <= ageRange) {
                                                                mUsers.add(user);
                                                            }
                                                        } else {
                                                            if (otherUserAge >= 18 && otherUserAge <= ageRange) {
                                                                mUsers.add(user);
                                                            }
                                                        }
                                                        Log.d("HomeFragment", "User added: " + user.getName());
                                                    } else {
                                                        Log.d("HomeFragment", "User skipped (matched/liked/disliked): " + user.getName());
                                                    }
                                                }
                                            }

                                            userAdapter.notifyDataSetChanged();
                                            if (mUsers.isEmpty()) {
                                                noUsersTextView.setVisibility(View.VISIBLE);
                                                swipeView.setVisibility(View.GONE);
                                            } else {
                                                noUsersTextView.setVisibility(View.GONE);
                                                swipeView.setVisibility(View.VISIBLE);
                                            }

                                            progressBar.setVisibility(View.GONE);
                                        });
                                    });
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("HomeFragment", "Database error: " + error.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeFragment", "Failed to fetch current user data: " + error.getMessage());
                    }
                });
            }
        });
    }


    private void fetchLikedUsers(String userId, OnUsersFetchedListener listener) {

        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("likedList");
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> likedUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    likedUsers.add(snapshot.getKey()); // Assuming the user ID is the key
                }
                listener.onUsersFetched(likedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Error fetching liked users: " + error.getMessage());
                listener.onUsersFetched(Collections.emptyList());
            }
        });
    }


    // Sample method to fetch disliked users
    private void fetchDislikedUsers(String userId, OnUsersFetchedListener listener) {
        // Implement your logic to fetch disliked users from the database
        DatabaseReference dislikesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("dislikedList");
        dislikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> dislikedUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dislikedUsers.add(snapshot.getKey()); // Assuming the user ID is the key
                }
                listener.onUsersFetched(dislikedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Error fetching disliked users: " + error.getMessage());
                listener.onUsersFetched(Collections.emptyList());
            }
        });
    }

    // Sample method to fetch matched users
    private void fetchMatchedUsers(String userId, OnUsersFetchedListener listener) {
        // Implement your logic to fetch matched users from the database
        DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");
        matchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> matchedUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    matchedUsers.add(snapshot.getKey()); // Assuming the user ID is the key
                }
                listener.onUsersFetched(matchedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Error fetching matched users: " + error.getMessage());
                listener.onUsersFetched(Collections.emptyList());
            }
        });
    }

    // Listener interface for fetching users
    interface OnUsersFetchedListener {
        void onUsersFetched(List<String> users);
    }
    private void getCurrentUserPreferredGender(final PreferredGenderCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("prefGender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String preferredGender = dataSnapshot.getValue(String.class);
                    callback.onPreferredGenderRetrieved(preferredGender);
                } else {
                    callback.onPreferredGenderRetrieved(null); // Handle the case where preferred gender is not set
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to get preferred gender: " + error.getMessage());
                callback.onPreferredGenderRetrieved(null); // Handle the error case
            }
        });
    }
    private void addToLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("likedList")
                .child(userId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("likesList")
                .child(currentUserId).setValue(true);
        checkForMatch(userId, currentUserId);
        sendUserActionNotification(userId, "like");
        addNotification(userId, "liked", "You have a new like", "Someone has liked your profile");
    }
    private void addToDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).setValue(true);
    }
    private void checkForMatch(String userId, String currentUserId) {
        String otherUserId= userId;
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("likedList");


        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendUserActionNotification(otherUserId, "match");
                    addNotification(otherUserId, "match", "new Connection!", "You have found a match");
                    matchNotification(otherUserId, "match", "new Connection!", "You have found a match");
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.dialog_custom_match, null);

                    RoundedImageView currentUserImage = dialogView.findViewById(R.id.currentuserimage);
                    RoundedImageView otherUserImage = dialogView.findViewById(R.id.otheruserimage);
                    Button Button = dialogView.findViewById(R.id.startchat);
                    TextView keepsearching = dialogView.findViewById(R.id.keepsearching);
                    TextView textView = dialogView.findViewById(R.id.textView);

                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUserId)
                            .child("imageurl");

                    currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String currentUserImageUrl = snapshot.getValue(String.class);
                                if (currentUserImageUrl != null) {

                                    Glide.with(getActivity())
                                            .load(currentUserImageUrl)
                                            .into(currentUserImage);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userId)
                            .child("imageurl");

                    otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String otherUserImageUrl = snapshot.getValue(String.class);
                                if (otherUserImageUrl != null) {
                                    Glide.with(getActivity())
                                            .load(otherUserImageUrl)
                                            .into(otherUserImage);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    if (getActivity() instanceof Activity && !((Activity) getActivity()).isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.show();

                        keepsearching.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), messagingActivity.class);
                                intent.putExtra("userid", userId);
                                getActivity().startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Log.e("UserAdapter", "Activity is finishing or context is not valid, cannot show dialog.");
                    }
                    saveMatchesToFirebase(userId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MatchCheck", "Database error: " + error.getMessage());
            }
        });
    }
    private void saveMatchesToFirebase(String user) {
        String userId = user;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).setValue(true);
        userRef.child(currentUserId).setValue(true);
    }
    private void sendUserActionNotification(String targetedUserId, String actionType) {

        String currentUserId = targetedUserId;

        if (currentUserId == null || targetedUserId == null) {
            Log.e("Notification", "Either current user or targeted user is null");
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

                        String userToken = targetUser.getFcmToken();
                        if (userToken != null) {
                            Log.e("FCM Token", userToken);
                            FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                    userToken,
                                    "Notification",
                                    notificationBody,
                                    getActivity()
                            );
                            fcmNotificationSender.sendNotification();
                        } else {
                            Log.e("Notification", "Target user FCM token is null");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Failed to retrieve target user data", databaseError.toException());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to retrieve current user data", databaseError.toException());
            }
        });
    }
    private String generateNotificationMessage(String userName, String actionType) {
        switch (actionType) {
            case "like":
                return userName + " has liked your profile";
            case "visit":
                return userName + " has visited your profile";
            case "match":
                return "You and " + userName + " have a new Connection!";
            default:
                return userName + " performed an action";
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
        Log.e("Notification", "targetedUserId user is null"+targetedUserId);
        Log.e("Notification", "Current user is null"+currentUserId);
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
                    case "liked":
                        personalizedBody = String.format("%s has liked your profile", currentUsername);
                        break;
                    case "visited":
                        personalizedBody = String.format("%s has visited your profile", currentUsername);
                        break;
                    case "match":
                        personalizedBody = String.format("You and %s have a new Connection!", currentUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the default body if type doesn't match
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
    private void matchNotification(String targetedUserId, String notificationType, String title, String body) {
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

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(targetedUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String otherUsername = user.getName();
                String personalizedBody;

                switch (notificationType) {
                    case "match":
                        personalizedBody = String.format("You have found a new Connection with %s ", otherUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the default body if type doesn't match
                        break;
                }

                DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(currentUserId)
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
                notification.setOtheruserid(currentUserId);
                notification.setCurrentuserid(targetedUserId);

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
    private static class OneItemLinearLayoutManager extends LinearLayoutManager {
        public OneItemLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            return false; // Disable scrolling
        }
    }
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                User user = mUsers.get(position);
                String userId = user.getId(); // Assuming User model has a method to get user ID

                if (direction == ItemTouchHelper.RIGHT) {
                    addToLikedList(userId);
                } else if (direction == ItemTouchHelper.LEFT) {
                    addToDislikedList(userId);
                }

                mUsers.remove(position);
                userAdapter.notifyItemRemoved(position);
                if (mUsers.isEmpty()) {
                    swipeView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                float alpha = 1.0f - (Math.abs(dX) / recyclerView.getWidth());
                itemView.setAlpha(alpha);
                itemView.setTranslationX(dX);

                int iconSize = (int) (100 * recyclerView.getContext().getResources().getDisplayMetrics().density);

                if (dX > 0) {
                    int iconLeft = itemView.getLeft() + (int) (dX * 0.5f) - (iconSize / 2);
                    int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                    int iconRight = iconLeft + iconSize;
                    int iconBottom = iconTop + iconSize;

                    likeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    likeIcon.draw(c);
                } else if (dX < 0) {
                    int iconLeft = itemView.getRight() - (int) (Math.abs(dX) * 0.5f) - (iconSize / 2);
                    int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                    int iconRight = iconLeft + iconSize;
                    int iconBottom = iconTop + iconSize;

                    dislikeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    dislikeIcon.draw(c);
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(swipeView);
    }
}
