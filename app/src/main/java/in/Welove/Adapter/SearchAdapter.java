package in.Welove.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.Profile.ProfileActivity;
import in.Welove.R;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private List<User> userList;
    private List<User> userListFiltered;
    private Context context;

    public SearchAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.userListFiltered = userList;

        this.context = context;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        User user = userListFiltered.get(position);

        Glide.with(context)
                .load(user.getImageurl())
                .into(holder.profileImage);

        holder.username.setText(user.getName());

        if (user.getStatus() != null && user.getStatus()) {
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            addNotification(user.getId(), "visited", "Your profile was visited", " has visited your profile");
            handleProfileVisit(user.getId());
            context.startActivity(intent);
            sendUserActionNotification(user.getId(), "visit");
        });
        Boolean isPremium = user.getP() != null ? user.getP() : false;
        Boolean isPremiumPlus = user.getPp() != null ? user.getPp() : false;
        Boolean isHot = user.getHot() != null ? user.getHot() : false;

        if (holder.premiumStatus != null) {
            holder.premiumStatus.setVisibility(View.VISIBLE); // Ensure it's visible before setting image

            if (isPremium) {
                holder.premiumStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.premium));
            } else if (isPremiumPlus) {
                holder.premiumStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.vip));
            } else if (isHot) {
                holder.premiumStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hot));
            } else {
                holder.premiumStatus.setVisibility(View.GONE); // Hide if none of the conditions are met
            }
        }



    }
    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }
    @Override
    public Filter getFilter() {
        return userFilter;
    }
    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userList);  // If search query is empty, show all users
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Filtering users based on the username (you can add more filters if needed)
                for (User user : userList) {
                    if (user.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userListFiltered.clear();
            if (results.values != null) {
                userListFiltered.addAll((List<User>) results.values);
            }
            notifyDataSetChanged();  // Notify RecyclerView to update the view with filtered data
        }
    };

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

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImage;
        private final TextView username;
        private final View imgOn, imgOff;
        private final ImageView premiumStatus;

        public SearchViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            imgOn = itemView.findViewById(R.id.img_on);
            imgOff = itemView.findViewById(R.id.img_off);
            premiumStatus=itemView.findViewById(R.id.premiumStatus);
        }
    }
}
