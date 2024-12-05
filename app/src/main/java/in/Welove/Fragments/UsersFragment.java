package in.Welove.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import in.Welove.Model.User;
import in.Welove.R;
import in.demo.myapplication.Adapter.UsersAdapter;


public class UsersFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private TextView noMatches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.recycler1_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noMatches=view.findViewById(R.id.nomatch);

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), userList,false);
        recyclerView.setAdapter(usersAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            getMatchedUsers();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getMatchedUsers() {
        String currentUserId = currentUser.getUid();
        DatabaseReference matchesRef = databaseReference.child("users").child(currentUserId).child("matches");

        matchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear existing data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String matchedUserId = snapshot.getKey();
                    if (snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class)) {
                        loadUserDetails(matchedUserId);
                    }
                }
                // Call updateVisibility after processing all matched users
                updateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Ensure the fragment is attached to an activity and context is not null
                if (getContext() != null && isAdded()) {
                    Toast.makeText(getContext(), "Failed to get matched users", Toast.LENGTH_SHORT).show();
                } else {
                    // Log a message or handle the case where context is unavailable
                    Log.e("UsersFragment", "Context or Fragment is not attached. Cannot show Toast.");
                }
            }

        });
    }

    private void loadUserDetails(String userId) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User user = userSnapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user);
                    usersAdapter.notifyDataSetChanged();
                }
                updateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVisibility() {
        if (userList.isEmpty()) {
            noMatches.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMatches.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
