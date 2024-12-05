package in.Welove.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.Welove.CustomDialog;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.Chat;
import in.Welove.Model.User;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isChat;
    private final List<User> selectedUsers;

    public UsersAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
        this.selectedUsers = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);

        holder.username.setText(user.getName());

        if (isChat) {
            holder.last_msg.setVisibility(View.VISIBLE);
            lastMessage(user.getId(), holder.last_msg, holder.lastmsgdate, holder.lastmsgTime, holder.unseenMessages);
            holder.img_on.setVisibility(user.getStatus() ? View.VISIBLE : View.GONE);
            holder.img_off.setVisibility(user.getStatus() ? View.GONE : View.VISIBLE);
        } else {
            holder.last_msg.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
            holder.unseenMessages.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

            // Check if the selected user is in the matched list of the current user
            currentUserRef.child("matchedList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isMatched = false;
                    for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                        String matchedUserId = matchSnapshot.getValue(String.class);
                        if (matchedUserId != null && matchedUserId.equals(user.getId())) {
                            isMatched = true;
                            break;
                        }
                    }

                    if (isMatched) {
                        // If the selected user is in the matched list, proceed to the messaging activity
                        navigateToMessagingActivity(user.getId());
                    } else {
                        // If not matched, check if the user has premium or hot subscription
                        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Boolean isPremium = snapshot.child("p").getValue(Boolean.class);
                                Boolean isPP = snapshot.child("pp").getValue(Boolean.class);
                                Boolean isHot = snapshot.child("hot").getValue(Boolean.class);

                                if ((isPremium != null && isPremium) || (isPP != null && isPP) || (isHot != null && isHot)) {
                                    // If any subscription is active, proceed to the messaging activity
                                    navigateToMessagingActivity(user.getId());
                                } else {
                                    // If no subscription, show the subscription dialog
                                    showSubscriptionDialog();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Failed to fetch subscription status: " + error.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Failed to fetch matched list: " + error.getMessage());
                }
            });
        });
    }
    private void navigateToMessagingActivity(String userId) {
        Intent intent = new Intent(mContext, messagingActivity.class);
        intent.putExtra("userid", userId);
        mContext.startActivity(intent);
    }

    private void showSubscriptionDialog() {
        CustomDialog customDialog = new CustomDialog(mContext);
        customDialog.show(
                "Make a Match or Upgrade Subscription",
                "You have not matched with this user. To chat, either make a match or subscribe to Premium, Premium Plus, or Hot Profile. Would you like to upgrade?",
                v -> {
                    // Navigate to SubscriptionActivity
                    Intent intent = new Intent(mContext, SubscriptionActivity.class);
                    mContext.startActivity(intent);
                    customDialog.dismiss();
                },
                false
        );
        customDialog.setNegativeButton("Cancel", v -> customDialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, last_msg, lastmsgdate, lastmsgTime, unseenMessages;
        public ImageView profile_image;
        public View img_on, img_off;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            lastmsgdate = itemView.findViewById(R.id.Date);
            last_msg = itemView.findViewById(R.id.last_msg);
            lastmsgTime = itemView.findViewById(R.id.time);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
        }
    }
    private void lastMessage(String userid, TextView last_msg, TextView lastmsgdate, TextView lastmsgTime, TextView unseenMessages) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(firebaseUser.getUid())
                .child("Chatlist")
                .child(userid)
                .child("Chats");

        reference.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            String formattedTime = formatTimestamp(chat.getTimestamp());
                            String latestMessage = chat.getType().equals("text") ? chat.getMessage() : "Image";

                            last_msg.setText(latestMessage);
                            lastmsgTime.setText(formattedTime);
                            lastmsgdate.setText(formatDate(chat.getTimestamp()));
                        }
                    }
                } else {
                    last_msg.setText("");
                    lastmsgTime.setText("");
                    lastmsgdate.setText("");
                }
                fetchUnseenMessagesCount(userid, unseenMessages);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("lastMessage", "Error fetching last message: " + error.getMessage());
            }
        });
    }

    private void fetchUnseenMessagesCount(String userid, TextView unseenMessages) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("Chatlist")
                .child(userid)
                .child("Chats");

        reference.orderByChild("isseen").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unseenCount = 0; // Count of unseen messages
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && !chat.getSender().equals(currentUser.getUid())) {
                        unseenCount++;
                    }
                }
                unseenMessages.setText(unseenCount > 0 ? String.valueOf(unseenCount) : "");
                unseenMessages.setVisibility(unseenCount > 0 ? View.VISIBLE : View.GONE); // Show or hide based on count
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("unseenMessages", "Error fetching unseen messages count: " + error.getMessage());
            }
        });
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp != null) {
            Date date = new Date(timestamp);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return timeFormat.format(date);
        }
        return "";
    }

    private String formatDate(Long timestamp) {
        if (timestamp != null) {
            Date date = new Date(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(date);
        }
        return "";
    }
}
