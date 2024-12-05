package in.Welove.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import in.Welove.Adapter.NotificationAdapter;
import in.Welove.CustomDialog;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.R;


public class NotificationFragment extends BaseFragment {

    private RecyclerView recyclerViewNotification;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private String currentUserImageUrl;
    private ImageView deletenotification;
    private TextView noNotificationtxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerViewNotification = view.findViewById(R.id.recycle_view);
        recyclerViewNotification.setHasFixedSize(true);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        noNotificationtxt=view.findViewById(R.id.notificationtxt);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList, currentUserImageUrl);
        recyclerViewNotification.setAdapter(notificationAdapter);
        
        deletenotification=view.findViewById(R.id.deletenotification);

        deletenotification.setOnClickListener(v -> {
            CustomDialog customDialog = new CustomDialog(getActivity());
            customDialog.show(
                    "Delete Notification",
                    "Are you sure you want to delete all notifications?",
                    v1 -> {
                        deletenotificationaofCurrentUser();
                        customDialog.dismiss();
                    },
                    false
            );
            customDialog.setNegativeButton("No", v1 -> customDialog.dismiss());
        });



        readNotifications();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            currentUserImageUrl = user.getImageurl();
                        }

                        // Set up the adapter with the current user's image URL
                        notificationAdapter = new NotificationAdapter(getContext(), notificationList, currentUserImageUrl);
                        recyclerViewNotification.setAdapter(notificationAdapter);
                        readNotifications();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
        return view;
    }

    private void deletenotificationaofCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }
        String currentUserId = currentUser.getUid();

        // Reference to the current user's notifications
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("Notifications");

        notificationsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Notification", "All notifications deleted successfully");
                Toast.makeText(getContext(), "Notifications Deleted Sucessfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Notification", "Failed to delete notifications", task.getException());
            }
        });
    }


    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid())
                    .child("Notifications");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificationList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        if (notification != null) {
                            notificationList.add(notification);
                        }
                    }
                    Collections.reverse(notificationList);
                    if(notificationList.isEmpty())
                    {
                        noNotificationtxt.setVisibility(View.VISIBLE);
                        recyclerViewNotification.setVisibility(View.GONE);
                    }else{
                        noNotificationtxt.setVisibility(View.GONE);
                        recyclerViewNotification.setVisibility(View.VISIBLE);
                    }
                    notificationAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
