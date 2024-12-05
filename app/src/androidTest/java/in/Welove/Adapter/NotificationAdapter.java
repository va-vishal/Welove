package in.Welove.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import in.Welove.Fragments.MatchedFragment;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.Profile.LikeActivity;
import in.Welove.Profile.VisitsActivity;
import in.Welove.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<Notification> notificationList;
    private final String currentUserImageUrl;

    public NotificationAdapter(Context context, List<Notification> notificationList, String currentUserImageUrl) {
        this.context = context;
        this.notificationList = notificationList;
        this.currentUserImageUrl = currentUserImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        String anotherUserId = notification.getCurrentuserid();
        if (anotherUserId != null) {
            getUserInfo(holder.anotherUserImage, anotherUserId);
        } else {
            holder.anotherUserImage.setImageResource(R.drawable.defaultimage);
        }

        String currentUserId = notification.getOtheruserid();
        if (currentUserId != null) {
            getUserInfo(holder.currentuserimage, currentUserId);
        } else {
            holder.currentuserimage.setImageResource(R.drawable.defaultimage);
        }

        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(notification.getTimestamp());
        holder.timestamp.setText(formattedDate);

        holder.notificationstatus.setVisibility(notification.isSeen() ? View.GONE : View.VISIBLE);

        // Handle click and mark notification as seen
        holder.itemView.setOnClickListener(v -> {
            String notificationId = notification.getNotificationId();
            if (notificationId != null && anotherUserId != null) {
                markNotificationAsSeen(notificationId, currentUserId);
                holder.notificationstatus.setVisibility(View.GONE);
            } else {
                Log.e("NotificationAdapter", "Notification ID or Current User ID is null");
            }
            handleNotificationClick(notification); // Handle different notification types
        });

        // Handle long press to delete the notification
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteConfirmationPopup(notification.getNotificationId(), position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body, timestamp;
        public ImageView notificationstatus;
        public RoundedImageView anotherUserImage, currentuserimage;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            timestamp = itemView.findViewById(R.id.timestamp);
            anotherUserImage = itemView.findViewById(R.id.anotherUser);
            currentuserimage = itemView.findViewById(R.id.currentuser);
            notificationstatus = itemView.findViewById(R.id.notificationstatus);
        }
    }

    // Fetch user info for notifications
    private void getUserInfo(RoundedImageView imageView, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getImageurl() != null) {
                    Glide.with(context).load(user.getImageurl()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.defaultimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    // Mark notification as seen
    private void markNotificationAsSeen(String notificationId, String userId) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("Notifications")
                .child(notificationId);

        notificationRef.child("seen").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("NotificationAdapter", "Notification marked as seen.");
            } else {
                Log.e("NotificationAdapter", "Error marking notification as seen: " + task.getException());
            }
        });
    }

    // Handle different notification types (open activities, fragments, etc.)
    private void handleNotificationClick(Notification notification) {
        String type = notification.getNotificationType();
        if (type.equals("liked")) {
            Intent intent = new Intent(context, LikeActivity.class);
            context.startActivity(intent);
        } else if (type.equals("visited")) {
            Intent intent = new Intent(context, VisitsActivity.class);
            intent.putExtra("publisherid", notification.getOtheruserid());
            context.startActivity(intent);
        } else if (type.equals("match")) {
            MatchedFragment matchedFragment = new MatchedFragment();
            Bundle args = new Bundle();
            args.putString("publisherid", notification.getOtheruserid());
            matchedFragment.setArguments(args);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, matchedFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (type.equals("message") || type.equals("audiocall") || type.equals("videocall")) {
            Intent intent = new Intent(context, messagingActivity.class);
            intent.putExtra("userid", notification.getCurrentuserid());
            context.startActivity(intent);
        }
    }


    private void showDeleteConfirmationPopup(String notificationId, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_notification, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        Button buttonDelete = popupView.findViewById(R.id.button_delete);
        Button buttonCancel = popupView.findViewById(R.id.button_cancel);
        buttonDelete.setOnClickListener(v -> {
            deleteNotification(notificationId, position);
            popupWindow.dismiss();
        });
        buttonCancel.setOnClickListener(v -> popupWindow.dismiss());
        popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
    private void deleteNotification(String notificationId, int position) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUserId)
                .child("Notifications")
                .child(notificationId);

        notificationRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (position >= 0 && position < notificationList.size()) {
                    notificationList.remove(position); // Remove from list
                    notifyItemRemoved(position); // Update RecyclerView
                    Log.d("NotificationAdapter", "Notification deleted successfully.");
                } else {
                    Log.w("NotificationAdapter", "deleteNotification: Invalid position " + position + ", list size: " + notificationList.size());
                }
            } else {
                Log.e("NotificationAdapter", "Error deleting notification: " + task.getException());
            }
        });
    }
}
