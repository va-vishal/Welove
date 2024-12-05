package in.Welove.Fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
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
import in.Welove.Adapter.UserAdapter2;
import in.Welove.Model.User;
import in.Welove.R;

public class MatchedFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private UserAdapter2 userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ImageView matchesImage;
    private TextView matchesText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_matched, container, false);


        matchesImage=view.findViewById(R.id.matchesimage);
        matchesText=view.findViewById(R.id.matchestext);

        recyclerView = view.findViewById(R.id.recyclerViewMatched);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = 8;
                int pacing=-25;
                int position = parent.getChildAdapterPosition(view);
                int spanCount = 2;

                outRect.right = pacing;
                outRect.bottom = spacing;

                if (position % spanCount == 0) {
                    outRect.left = spacing;
                } else {
                    outRect.left = pacing;
                    outRect.right = spacing;
                }
                if (position < spanCount) {
                    outRect.top =spacing;
                }
            }
        });

        userList = new ArrayList<>();
        userAdapter = new UserAdapter2(getContext(), userList);
        recyclerView.setAdapter(userAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
            getMatchedUsers();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getMatchedUsers() {
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUserId).child("matches");

            matchesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear(); // Clear the existing user list

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String matchedUserId = snapshot.getKey();
                        if (snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class)) {
                            fetchUserDetails(matchedUserId); // Fetch user details for each matched user
                        }
                    }
                    updateVisibility();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to get matched users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User user = userSnapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user); // Add the fetched user to the list
                    userAdapter.notifyDataSetChanged(); // Ensure UI is updated
                }

                updateVisibility(); // Update visibility after each user detail fetch
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVisibility() {
        if (userList.isEmpty()) {
            matchesImage.setVisibility(View.VISIBLE);
            matchesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            matchesImage.setVisibility(View.GONE);
            matchesText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}
