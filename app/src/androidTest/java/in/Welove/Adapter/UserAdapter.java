package in.Welove.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.Welove.CustomDialog;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.Profile.ProfileActivity;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final Set<String> likedUsers;
    private final Set<String> dislikedUsers;
    private Set<String> Notifications = null;
    private double currentUserLat;
    private double currentUserLon;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.Notifications = Notifications;
        this.likedUsers = new HashSet<>();
        this.dislikedUsers = new HashSet<>();
        loadUserLists();
        loadCurrentUserLocation();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        if (user != null) {
            updateUI(holder, user);
            setupClickListeners(holder, user);
        }
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(currentUserId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean isPremium =  dataSnapshot.child("p").getValue(Boolean.class);
                            Boolean isPremiumPlus =dataSnapshot.child("pp").getValue(Boolean.class);
                            Boolean isHot =dataSnapshot.child("hot").getValue(Boolean.class);

                            if ((isPremium != null && isPremium) || (isPremiumPlus != null && isPremiumPlus)|| (isHot != null && isHot)) {
                                Intent intent = new Intent(context, messagingActivity.class);
                                intent.putExtra("userid", user.getId());
                                context.startActivity(intent);
                            }
                            else {
                                DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(user.getId())  // This is the other user's likedList
                                        .child("matches");

                                matchRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // If the current user is in the other user's liked list
                                        if (dataSnapshot.exists()) {
                                            Intent intent = new Intent(context, messagingActivity.class);
                                            intent.putExtra("userid", user.getId());
                                            context.startActivity(intent);
                                        }
                                        else {
                                            CustomDialog customDialog = new CustomDialog(context);
                                            customDialog.show(
                                                    "Subscription Required",
                                                    "You must subscribe to a plan to message this user. Would you like to subscribe now?",
                                                    v -> {
                                                        Intent intent = new Intent(context, SubscriptionActivity.class);
                                                        context.startActivity(intent);
                                                        customDialog.dismiss();
                                                    },
                                                    false // Show the cancel button
                                            );
                                            customDialog.setNegativeButton("Cancel", v -> customDialog.dismiss());
                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(context, "Error checking liked list.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(context, "Unable to check premium status. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "Error checking premium status.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            addNotification(user.getId(), "visited", "Your profile was visited", " has visited your profile");
            handleProfileVisit(user.getId());
            context.startActivity(intent);
            sendUserActionNotification(user.getId(), "visit");
        });

        holder.profileview.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            context.startActivity(intent);
            handleProfileVisit(user.getId());
            addNotification(user.getId(), "visited", "Your profile was visited", "Someone has visited your profile");
            sendUserActionNotification(user.getId(), "visit");
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void handleProfileVisit(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference visitedUserVisitsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("visitsList");

        // Update the visited user's visits list
        visitedUserVisitsRef.child(currentUserId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ProfileVisit", "Visit recorded successfully");
            } else {
                Log.e("ProfileVisit", "Failed to record visit: " + task.getException().getMessage());
            }
        });
    }

    private void loadUserLists() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        userRef.child("likedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likedUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    likedUsers.add(snapshot.getKey());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
        userRef.child("dislikedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dislikedUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dislikedUsers.add(snapshot.getKey());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private void loadCurrentUserLocation() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    currentUserLat = currentUser.getLatitude();
                    currentUserLon = currentUser.getLongitude();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
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

    private void updateUI(UserViewHolder holder, User user) {
        handleUserStatus(holder, user);
        handleUserNameAndAge(holder, user);
        handleUserJobAndState(holder, user);
        handleUserImages(holder, user);
        handleUserDistance(holder, user);
        updateButtonStates(holder, user);
    }

    private void handleUserStatus(UserViewHolder holder, User user) {
        Boolean isPremiumPlus = user.getPp();
        Boolean isPremium = user.getP();

        if (isPremiumPlus != null && isPremiumPlus) {
            holder.premimage.setVisibility(View.VISIBLE);
            holder.premimage.setImageResource(R.drawable.vip);
        } else if (isPremium != null && isPremium) {
            holder.premimage.setVisibility(View.VISIBLE);
            holder.premimage.setImageResource(R.drawable.premium);
        } else {
            holder.premimage.setVisibility(View.GONE);
        }

        if (user.getStatus() != null && user.getStatus()) {
            holder.online.setVisibility(View.VISIBLE);
            holder.offline.setVisibility(View.GONE);
        } else {
            holder.offline.setVisibility(View.VISIBLE);
            holder.online.setVisibility(View.GONE);
        }
    }
    private void handleUserNameAndAge(UserViewHolder holder, User user) {
        holder.username.setText(user.getName() != null && (user.getHideName() == null || !user.getHideName()) ?
                user.getName() + "," : "");
        holder.age.setText(user.getAge() != null && (user.getHideAge() == null || !user.getHideAge()) ?
                String.valueOf(user.getAge()) : "Hidden");
    }
    private void handleUserJobAndState(UserViewHolder holder, User user) {
        holder.job.setText(user.getJobType() + ",");
        holder.state.setText(user.getState());
    }
    private void handleUserImages(UserViewHolder holder, User user) {

        holder.viewFlipper.removeAllViews();

        addImageToFlipper(holder.viewFlipper, user.getImageurl());
        addImageToFlipper(holder.viewFlipper, user.getImageurl1());
        addImageToFlipper(holder.viewFlipper, user.getImageurl2());
        addImageToFlipper(holder.viewFlipper, user.getImageurl3());
        addImageToFlipper(holder.viewFlipper, user.getImageurl4());
        addImageToFlipper(holder.viewFlipper, user.getImageurl5());

        int childCount = holder.viewFlipper.getChildCount();
        if (childCount == 0 || childCount == 1) {
            holder.viewFlipper.setAutoStart(false);
            holder.viewFlipper.stopFlipping();
        } else {
            holder.viewFlipper.setFlipInterval(3000);
            holder.viewFlipper.setAutoStart(true);
            holder.viewFlipper.startFlipping();
        }
    }
    private void addImageToFlipper(ViewFlipper viewFlipper, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView imageView = new ImageView(viewFlipper.getContext());
            imageView.setLayoutParams(new ViewFlipper.LayoutParams(
                    ViewFlipper.LayoutParams.MATCH_PARENT,
                    ViewFlipper.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(viewFlipper.getContext()).load(imageUrl).into(imageView);
            viewFlipper.addView(imageView);
        }
    }
    private void handleUserDistance(UserViewHolder holder, User user) {
        if (user.getLatitude() != 0 && user.getLongitude() != 0 &&
                (user.getHideLocation() == null || !user.getHideLocation())) {
            double distance = calculateDistance(currentUserLat, currentUserLon, user.getLatitude(), user.getLongitude());
            holder.kmtext.setText(String.format("%.2f km away", distance));
        } else {
            holder.kmtext.setText("not available");
        }
    }
    private void updateButtonStates(UserViewHolder holder, User user) {
        holder.likeButton.setImageResource(likedUsers.contains(user.getId()) ? R.drawable.liked : R.drawable.heart);
        holder.dislikeButton.setImageResource(dislikedUsers.contains(user.getId()) ? R.drawable.disliked : R.drawable.dislike);
    }
    private void setupClickListeners(UserViewHolder holder, User user) {
        holder.likeButton.setOnClickListener(v -> {
            handleLikeClick(user);

        });
        holder.dislikeButton.setOnClickListener(v -> handleDislikeClick(user));
    }
    private void handleLikeClick(User user) {
        String userId = user.getId();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            removeFromLikedList(userId);
            removeMatchFromFirebase(user);
        } else if (dislikedUsers.contains(userId)) {
            Toast.makeText(context, "Undo dislike before liking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            likedUsers.add(userId);
            addToLikedList(userId);
            sendUserActionNotification(user.getId(), "like");
            addNotification(userId, "liked", "You have a new like", "Someone has liked your profile");
            checkForMatch(user, currentUserId);
        }
        notifyDataSetChanged();
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

    private void checkForMatch(User user, String currentUserId) {
        String otherUserId=user.getId();
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getId())
                .child("likedList");

        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendUserActionNotification(otherUserId, "match");
                    addNotification(otherUserId, "match", "new Connection!", "You have found a match");
                    matchNotification(otherUserId, "match", "new Connection!", "You have found a match");
                    LayoutInflater inflater = LayoutInflater.from(context);
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
                                    // Load the current user image using Glide
                                    Glide.with(context)
                                            .load(currentUserImageUrl)
                                            .into(currentUserImage);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getId())
                            .child("imageurl");

                    otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String otherUserImageUrl = snapshot.getValue(String.class);
                                if (otherUserImageUrl != null) {
                                    // Load the other user image using Glide
                                    Glide.with(context)
                                            .load(otherUserImageUrl)
                                            .into(otherUserImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.show();

                        keepsearching.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, messagingActivity.class);
                                intent.putExtra("userid", user.getId());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Log.e("UserAdapter", "Activity is finishing or context is not valid, cannot show dialog.");
                    }

                    saveMatchesToFirebase(user.getId());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Log.e("MatchCheck", "Database error: " + error.getMessage());
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
    }
    private void removeFromLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("likedList")
                .child(userId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("likesList")
                .child(currentUserId).removeValue();
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

    private void removeMatchFromFirebase(User user) {
        String userId = user.getId();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).removeValue();
        userRef.child(currentUserId).removeValue();
    }

    private void handleDislikeClick(User user) {
        String userId = user.getId();

        if (dislikedUsers.contains(userId)) {
            dislikedUsers.remove(userId);
            removeFromDislikedList(userId);
        } else if (likedUsers.contains(userId)) {
            Toast.makeText(context, "Undo like before disliking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            dislikedUsers.add(userId);
            addToDislikedList(userId);
        }
        notifyDataSetChanged();
    }

    private void addToDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).setValue(true);
    }

    private void removeFromDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).removeValue();
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
    private void sendUserActionNotification(String targetedUserId, String actionType) {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                        Log.d("Target User", "Retrieved target user: " + targetUser.toString());
                        String userToken = targetUser.getFcmToken();
                        if (userToken != null) {
                            Log.e("FCM Token", userToken);
                            FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                    userToken,
                                    "Notification",
                                    notificationBody,
                                    context
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


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image1,image2,image3,image4,image5,image6;
        private final ViewFlipper viewFlipper;
        private final TextView username;
        private final TextView age;
        private final TextView job;
        private final TextView state;
        private final TextView kmtext;
        private final FloatingActionButton likeButton,dislikeButton,profileview,message;
        private final ImageView online;
        private final ImageView offline;
        private final ImageView premimage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.image11);
            image2 = itemView.findViewById(R.id.image22);
            image3 = itemView.findViewById(R.id.image33);
            image4 = itemView.findViewById(R.id.image44);
            image5 = itemView.findViewById(R.id.image55);
            image6 = itemView.findViewById(R.id.image66);
            viewFlipper = itemView.findViewById(R.id.viewFlipper);
            username = itemView.findViewById(R.id.username);
            age = itemView.findViewById(R.id.age);
            job = itemView.findViewById(R.id.job);
            state = itemView.findViewById(R.id.state);
            kmtext = itemView.findViewById(R.id.kmtext);
            online = itemView.findViewById(R.id.online_status);
            offline = itemView.findViewById(R.id.offline_status);
            profileview = itemView.findViewById(R.id.profileview);
            likeButton = itemView.findViewById(R.id.like_button);
            dislikeButton = itemView.findViewById(R.id.dislike_button);
            message = itemView.findViewById(R.id.message);
            premimage = itemView.findViewById(R.id.premimage);
        }
    }
}
