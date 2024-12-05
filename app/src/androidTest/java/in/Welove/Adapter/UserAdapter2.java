package in.Welove.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import in.Welove.CustomDialog;
import in.Welove.Model.User;
import in.Welove.Profile.ProfileActivity;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    public UserAdapter2(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName() + ",");
        holder.ageTextView.setText(user.getAge());
        holder.genderTextView.setText(user.getState());
        holder.motherTongueTextView.setText(user.getJobType());
        Glide.with(context).load(user.getImageurl()).into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(currentUserId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean isPremium = dataSnapshot.child("p").getValue(Boolean.class);
                        Boolean isPremiumPlus = dataSnapshot.child("pp").getValue(Boolean.class);
                        Boolean isHot =dataSnapshot.child("hot").getValue(Boolean.class);

                        if ((isPremium != null && isPremium) || (isPremiumPlus != null && isPremiumPlus)|| (isHot != null && isHot)) {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("publisherid", user.getId());
                            context.startActivity(intent);
                        } else {
                            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("matches");

                            matchRef.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Intent intent = new Intent(context, ProfileActivity.class);
                                        intent.putExtra("publisherid", user.getId());
                                        context.startActivity(intent);
                                    } else {
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
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, "Error checking premium status.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
        @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView profileImage;
        ImageView profileView;
        TextView nameTextView;
        TextView ageTextView;
        TextView genderTextView;
        TextView motherTongueTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.name);
            ageTextView = itemView.findViewById(R.id.age);
            genderTextView = itemView.findViewById(R.id.state);
            motherTongueTextView = itemView.findViewById(R.id.jobType);
            profileView=itemView.findViewById(R.id.profileview);
        }
    }
}
