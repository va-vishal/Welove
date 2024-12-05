package in.Welove.Fragments;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import in.Welove.Model.ChatList;
import in.Welove.Model.User;
import in.Welove.R;

public class AllFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> mUsers;
    private ImageView messageImage;
    private TextView messageText;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<ChatList> usersList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all, container, false);

        messageImage = view.findViewById(R.id.messageimage);
        messageText = view.findViewById(R.id.nomessagestxt);
        recyclerView = view.findViewById(R.id.recycler_view_all);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid()).child("Chatlist");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatList chatlist = snapshot.getValue(ChatList.class);
                        if (chatlist != null) {
                            usersList.add(chatlist);
                        }
                    }
                    readChats();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("AllFragment", "Database error: " + error.getMessage());
                }
            });
        } else {
            Toast.makeText(getContext(), "User Not Signed In", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            for (ChatList chatlist : usersList) {
                                if (user.getId() != null && user.getId().equals(chatlist.getId())) {
                                    mUsers.add(user);
                                    break;
                                }
                            }
                        }
                    } catch (DatabaseException e) {
                        Log.e("AllFragment", "Failed to convert value", e);
                    }
                }

                // Update the adapter with the new list of users
                usersAdapter = new UsersAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(usersAdapter);

                // Call the updateVisibility method after setting the adapter
                updateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllFragment", "Database error: " + error.getMessage());
            }
        });
    }

    private void updateVisibility() {
        if (mUsers.isEmpty()) { // Use mUsers to check if there are any chats
            messageImage.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            messageImage.setVisibility(View.GONE);
            messageText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
